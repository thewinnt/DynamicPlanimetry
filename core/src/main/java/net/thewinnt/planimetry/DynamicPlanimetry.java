package net.thewinnt.planimetry;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.Pair;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.NumberTag;
import net.thewinnt.planimetry.data.DefaultTags;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.Language;
import net.thewinnt.planimetry.data.registry.Identifier;
import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.data.registry.TagKey;
import net.thewinnt.planimetry.platform.NativeIO;
import net.thewinnt.planimetry.platform.PlatformAbstractions;
import net.thewinnt.planimetry.screen.DebugSettingsScreen;
import net.thewinnt.planimetry.screen.EditorScreen;
import net.thewinnt.planimetry.screen.FileSelectionScreen;
import net.thewinnt.planimetry.screen.FlatUIScreen;
import net.thewinnt.planimetry.screen.MainMenuScreen;
import net.thewinnt.planimetry.screen.SettingsScreen;
import net.thewinnt.planimetry.ui.BoardTheme;
import net.thewinnt.planimetry.ui.GuiTheme;
import net.thewinnt.planimetry.ui.Notifications;
import net.thewinnt.planimetry.ui.StyleSet;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingFormatArgumentException;
import java.util.Random;
import java.util.function.Function;
import java.util.zip.Deflater;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import space.earlygrey.shapedrawer.ShapeDrawer;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
@SuppressWarnings("SimpleDateFormat")
public class DynamicPlanimetry extends Game {
    // public constants
    public static final int DATA_VERSION = 1; // public testing - release 0.2
    public static final String APP_VERSION = "v. 0.2";
    public static final int MAIN_MENU = 0;
    public static final int EDITOR_SCREEN = 1;
    public static final int FILE_SELECTION_SCREEN = 2;
    public static final int SETTINGS_SCREEN = 3;
    public static final int DEBUG_SETTINGS_SCREEN = 4;
    public static final Gson GSON = new GsonBuilder().create();
    public static final Random RANDOM = new Random();

    public static final GuiTheme[] LEGACY_GUI_THEMES = new GuiTheme[]{GuiTheme.THEME_GUI_LIGHT, GuiTheme.THEME_GUI_DARK};
    public static final BoardTheme[] LEGACY_BOARD_THEMES = new BoardTheme[]{BoardTheme.THEME_BOARD_WHITE, BoardTheme.THEME_BOARD_GREEN, BoardTheme.THEME_BOARD_BLACK};

    // general stuff
    private static DynamicPlanimetry INSTANCE;
    public final Int2ObjectMap<ScreenInstance<?>> screenByIds = new Int2ObjectArrayMap<>();
    public String last_fps = "FPS: ..."; // the last reading of the fps counter
    public StyleSet styles;

    // settings
    public static final Settings SETTINGS = new Settings();
    private static float DISPLAY_SCALING = 1;
    private final Map<String, Language> languages = new HashMap<>();
    private static PlatformAbstractions PLATFORM;
    private static NativeIO NATIVE_IO;

    // data
    private Drawing currentDrawing;
    private Language currentLanguage = Language.EMPTY;
    private List<Drawing> allDrawings;

    // screens
    public MainMenuScreen mainMenu;
    public EditorScreen editorScreen;
    public FileSelectionScreen fileSelectionScreen;
    public SettingsScreen settingsScreen;
    public DebugSettingsScreen debugSettingsScreen;

    // font stuff
    private FreeTypeFontGenerator gen_default;
    private FreeTypeFontGenerator gen_bold;
    private static final String characters;

    static {
        StringBuilder chars = new StringBuilder();
        for (int i = 0x20; i < 0x7E; i++) chars.append((char) i);
        for (int i = 0xA0; i < 0xFF; i++) chars.append((char) i);
        for (int i = 0x401; i < 0x452; i++) chars.append((char) i);
        chars.append('₽');
        characters = chars.toString();
    }

    // more font stuff
    public static final FontType BUTTON_ACTIVE = new FontType(85, GuiTheme.THEME_GUI_LIGHT.textButton());
    public static final FontType BUTTON_INACTIVE = new FontType(85, GuiTheme.THEME_GUI_LIGHT.textInactive());
    public static final FontType FPS = new FontType(50, Color.WHITE);

    public static record FontType(int size, Color color) {}

    private Map<FontType, BitmapFont> fonts_default;
    private Map<FontType, BitmapFont> fonts_bold;

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    public static final DateFormat TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    public static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
    public static final DateFormat AUTOSAVE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss.SSS");

    public DynamicPlanimetry(CompoundTag settings, PlatformAbstractions platform, NativeIO io, boolean forceDebug) {
        DynamicPlanimetry.INSTANCE = this;
        SETTINGS.fromNbt(settings);
        DynamicPlanimetry.PLATFORM = platform;
        DynamicPlanimetry.NATIVE_IO = io;
        if (forceDebug) {
            SETTINGS.setDebug();
        }
        Registries.init();
        FreeTypeFontGenerator.setMaxTextureSize(FreeTypeFontGenerator.NO_MAXIMUM);
    }

    @Override
    public void create() {
        gen_default = new FreeTypeFontGenerator(Gdx.files.internal("denhome.otf"));
        gen_bold = new FreeTypeFontGenerator(Gdx.files.internal("dhmbold.ttf"));
        fonts_default = new HashMap<>();
        fonts_bold = new HashMap<>();

        try {
            Gdx.files.local("drawings").mkdirs();
            Gdx.files.local("screenshots").mkdirs();
        } catch (Exception e) {
            if (isDebug()) {
                Notifications.addNotification(e.getMessage(), 7500);
            }
        }

        reloadLanguages();
        reloadTags();

        registerScreen(MAIN_MENU, dp -> mainMenu = new MainMenuScreen(dp));
        registerScreen(EDITOR_SCREEN, dp -> editorScreen = new EditorScreen(dp));
        registerScreen(FILE_SELECTION_SCREEN, dp -> fileSelectionScreen = new FileSelectionScreen(dp));
        registerScreen(SETTINGS_SCREEN, dp -> settingsScreen = new SettingsScreen(dp));
        registerScreen(DEBUG_SETTINGS_SCREEN, dp -> debugSettingsScreen = new DebugSettingsScreen(dp));
        setDrawing(null, false);
        setScreen(MAIN_MENU);
        if (isDebug()) {
            Notifications.addNotification("Включён режим отладки", 2000);
        }
    }

    private <T extends FlatUIScreen> ScreenInstance<T> registerScreen(int id, Function<DynamicPlanimetry, T> screen) {
        ScreenInstance<T> output = new ScreenInstance<>(screen);
        this.screenByIds.put(id, output);
        return output;
    }

    public void reloadLanguages() {
        String[] builtinLangs = Gdx.files.internal("lang/langs.txt").readString("utf8").split("\n");
        for (String name : builtinLangs) {
            if (name.startsWith("#") || !name.endsWith(".json")) continue;
            FileHandle i = Gdx.files.internal("lang/" + name);
            Language language = Language.fromJson(i.nameWithoutExtension(), GSON.fromJson(i.readString("utf8"), JsonObject.class));
            languages.put(language.getId(), language);
        }
        FileHandle langDir = Gdx.files.local("custom_languages");
        if (langDir.exists() && langDir.isDirectory()) {
            FileHandle[] customLangs = langDir.list();
            for (FileHandle i : customLangs) {
                Language language = Language.fromJson(i.nameWithoutExtension(), GSON.fromJson(i.readString("utf8"), JsonObject.class));
                languages.put(language.getId(), language);
            }
        }
        if (isDebug()) {
            for (Language i : languages.values()) {
                System.out.println(i);
            }
        }
        SETTINGS.initLanguages(languages);
    }

    public void reloadTags() {
        FileHandle tagList = Gdx.files.internal("tags/tags.txt");
        if (tagList.exists()) {
            String[] files = Gdx.files.internal("tags/tags.txt").readString("utf8").split("\n");
            Map<TagKey<?>, List<Identifier>> tags = new HashMap<>();
            for (String name : files) {
                if (name.startsWith("#") || !name.endsWith(".json")) continue;
                Pair<TagKey<?>, List<Identifier>> tag = TagKey.readJson(name);
                tags.put(tag.left(), tag.right());
            }
            Registries.reloadTags(tags);
        }
        // hardcoded tags - needed for native image
        Registries.appendTag(DefaultTags.SELECTABLE_LINES);
        Registries.appendTag(DefaultTags.SELECTABLE_POINTS);
    }

    @Override
    public void render() {
        super.render();
        if (Gdx.input.isKeyJustPressed(Keys.F2)) {
            Pixmap screenshot = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            String filename = AUTOSAVE_DATE_FORMAT.format(new Date(System.currentTimeMillis()));
            PixmapIO.writePNG(Gdx.files.local("screenshots/" + filename + ".png"), screenshot, Deflater.DEFAULT_COMPRESSION, true);
            screenshot.dispose();
            Notifications.addNotification("Скриншот сохранён как " + filename + ".png", 2000);
        } else if (Gdx.input.isKeyJustPressed(Keys.F11)) {
            SETTINGS.toggleFullscreen();
        }
    }

    @Override
    public void dispose() {
        for (BitmapFont i : fonts_default.values()) {
            i.dispose();
        }
        for (BitmapFont i : fonts_bold.values()) {
            i.dispose();
        }
        if (PLATFORM != null) {
            SETTINGS.toNbt(PLATFORM.getSettingsFile());
        }
    }

    @Override
    public void pause() {
        super.pause();
    }

    public BitmapFont getFont(float size, Color color) {
        return getFont(new FontType((int)(size), color));
    }

    public BitmapFont getFont(FontType type) {
        if (type.size <= 1) return getFont(FPS);
        BitmapFont out = fonts_default.get(type);
        if (out == null) {
            FreeTypeFontParameter param = new FreeTypeFontParameter();
            param.color = type.color();
            param.size = type.size();
            param.characters = characters;
            out = gen_default.generateFont(param);
            fonts_default.put(type, out);
        }
        return out;
    }

    public BitmapFont getBoldFont(float size, Color color) {
        return getBoldFont(new FontType((int)(size), color));
    }

    public BitmapFont getBoldFont(FontType type) {
        if (type.size <= 1) return getBoldFont(FPS);
        BitmapFont out = fonts_bold.get(type);
        if (out == null) {
            FreeTypeFontParameter param = new FreeTypeFontParameter();
            param.color = type.color();
            param.size = type.size();
            param.characters = characters;
            out = gen_bold.generateFont(param);
            fonts_bold.put(type, out);
        }
        return out;
    }

    public void setScreen(int id) {
        this.setScreen(this.screenByIds.get(id).get());
    }

    public void setDrawing(Drawing drawing, boolean saveOld) {
        if (saveOld && this.currentDrawing != null && this.currentDrawing.hasChanged()) {
            currentDrawing.save();
        }
        this.currentDrawing = drawing;
        if (this.currentDrawing != null) {
            this.currentDrawing.clearSwapListeners();
        }
    }

    public Drawing getDrawing() {
        return currentDrawing;
    }

    public void preloadDrawings(String folder) {
        ArrayList<Drawing> drawings = new ArrayList<>();
        for (File i : NATIVE_IO.listFiles(folder)) {
            try {
                drawings.add(Drawing.load(i));
            } catch (Exception | StackOverflowError e) {
                Notifications.addNotification("Ошибка при загрузке файла (" + i.getPath() + "):  " + e.getMessage(), 5000);
                e.printStackTrace();
            }
        }
        this.allDrawings = drawings;
    }

    public List<Drawing> getAllDrawings() {
        return allDrawings;
    }

    /**
     * @deprecated not recommended - always pass shape drawers to your actors!
     */
    @Deprecated
    public ShapeDrawer getCurrentShapeDrawer() {
        return ((FlatUIScreen)this.screen).getDrawer();
    }

    public Map<String, Language> getAllLanguages() {
        return languages;
    }

    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    void setLanguage(Language language) {
        this.currentLanguage = language;
    }

    public static String formatNumber(double number) {
        return String.format((Locale)null, "%." + SETTINGS.getDisplayPresicion() + "f", number);
    }

    public static DynamicPlanimetry getInstance() {
        return INSTANCE;
    }

    public static PlatformAbstractions platform() {
        return DynamicPlanimetry.PLATFORM;
    }

    public static NativeIO io() {
        return DynamicPlanimetry.NATIVE_IO;
    }

    public static boolean isDebug() {
        return SETTINGS.isDebug();
    }

    public static String translate(String string) {
        return DynamicPlanimetry.getInstance().currentLanguage.get(string);
    }

    public static String translate(String string, Object... args) {
        try {
            return DynamicPlanimetry.getInstance().currentLanguage.get(string, args);
        } catch (MissingFormatArgumentException e) {
            throw new IllegalArgumentException(String.format("Exception formatting string %s, arguments: %s", string, Arrays.toString(args)), e);
        }
    }

    public static float getDisplayScaling() {
        return DISPLAY_SCALING;
    }

    /* package-private */ static void setDisplayScaling(float displayScaling) {
        DynamicPlanimetry.DISPLAY_SCALING = displayScaling;
    }

    public static GuiTheme guiThemeFromNbt(CompoundTag nbt, String name) {
        if (nbt.get(name) instanceof NumberTag<?>) {
            return nbt.getInt(name) == 0 ? GuiTheme.THEME_GUI_LIGHT : GuiTheme.THEME_GUI_DARK;
        }
        return GuiTheme.byId(nbt.getString(name));
    }

    public static BoardTheme boardThemeFromNbt(CompoundTag nbt, String name) {
        return BoardTheme.byId(nbt.getString(name));
    }

    private class ScreenInstance<T extends FlatUIScreen> {
        private final Function<DynamicPlanimetry, T> setter;
        private T instance;
        private boolean initialized;

        public ScreenInstance(Function<DynamicPlanimetry, T> setter) {
            this.setter = setter;
        }

        public T get() {
            if (initialized) {
                return instance;
            }
            initialized = true;
            instance = setter.apply(DynamicPlanimetry.this);
            return instance;
        }
    }
}
