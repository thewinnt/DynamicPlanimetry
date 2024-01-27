package net.thewinnt.planimetry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import dev.dewy.nbt.Nbt;
import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.SettingsIO;
import net.thewinnt.planimetry.screen.EditorScreen;
import net.thewinnt.planimetry.screen.FileSelectionScreen;
import net.thewinnt.planimetry.screen.FlatUIScreen;
import net.thewinnt.planimetry.screen.MainMenuScreen;
import net.thewinnt.planimetry.screen.SettingsScreen;
import net.thewinnt.planimetry.ui.Notifications;
import net.thewinnt.planimetry.ui.Theme;
import net.thewinnt.planimetry.ui.text.Component;
import space.earlygrey.shapedrawer.ShapeDrawer;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class DynamicPlanimetry extends Game {
    // public constants
    public static final int MAIN_MENU = 0;
    public static final int EDITOR_SCREEN = 1;
    public static final int FILE_SELECTION_SCREEN = 2;
    public static final int SETTINGS_SCREEN = 3;
    public static final Nbt NBT = new Nbt();
    public static final Theme THEME_LIGHT = new Theme(
        Component.literal("Светлая"),
        new Color(0xFFFFFFFF), // main
        new Color(0xDCDCDCFF), // pressed
        new Color(0xF0F0F0FF), // button
        new Color(0x000000FF), // outlines
        new Color(0x000000FF), // text (button)
        new Color(0x000000FF), // text (ui)
        new Color(0x2D2D2DFF), // inactive text
        new Color(0xFFFFFFFF), // text field
        new Color(0x0060FFB0), // selection background
        new Color(0xC0C0C0FF), // delimiter
        new Color(0x2D2D2DFF), // inactive
        new Color(0x000000FF), // shape
        new Color(0x00C0FFFF), // shape hovered
        new Color(0x0080FFFF), // shape selected
        new Color(0xFF8000FF), // point
        new Color(0xFFC84CFF), // point hovered
        new Color(0xCC5500FF), // point selected
        new Color(0x0080FFFF), // utility point
        new Color(0x00C4FFFF), // utility point hovered
        new Color(0x0055CCFF), // utility point selected
        new Color(0xCCCCCCFF), // grid line
        new Color(0xAAAAAAFF), // grid hint
        new Color(0x000000FF)  // grid center
    );
    
    public static final Theme THEME_DARK = new Theme(
        Component.literal("Тёмная"),
        new Color(0x1F1F1FFF), // main
        new Color(0x000000FF), // pressed
        new Color(0x0F0F0FFF), // button
        new Color(0xA0A0A0FF), // outlines
        new Color(0xD0D0D0FF), // text (button)
        new Color(0xEEEEEEFF), // text (ui)
        new Color(0x2D2D2DFF), // inactive text
        new Color(0x000000FF), // text field
        new Color(0x0060FFB0), // selection background
        new Color(0x303030FF), // delimiter
        new Color(0x2D2D2DFF), // inactive
        new Color(0xEEEEEEFF), // shape
        new Color(0x00C0FFFF), // shape hovered
        new Color(0x0080FFFF), // shape selected
        new Color(0xFF8000FF), // point
        new Color(0xFFC84CFF), // point hovered
        new Color(0xCC5500FF), // point selected
        new Color(0x0080FFFF), // utility point
        new Color(0x00C4FFFF), // utility point hovered
        new Color(0x0055CCFF), // utility point selected
        new Color(0x404040FF), // grid line
        new Color(0x505050FF), // grid hint
        new Color(0x000000FF)  // grid center
    );

    public static final Theme[] BUILT_IN_THEMES = new Theme[]{THEME_LIGHT, THEME_DARK};

    // general stuff
    public final List<Screen> screenByIds = new ArrayList<>();
    public String last_fps = "FPS: ..."; // the last reading of the fps counter
    public static boolean IS_MOBILE = false;

    // settings
    public static final Settings SETTINGS = new Settings();
    private final SettingsIO settingsFile;

    // data
    private Drawing currentDrawing;
    private List<Drawing> allDrawings;

    // screens
    public MainMenuScreen mainMenu;
    public EditorScreen editorScreen;
    public FileSelectionScreen fileSelectionScreen;
    public SettingsScreen settingsScreen;

    // font stuff
    private FreeTypeFontGenerator gen_default;
    private FreeTypeFontGenerator gen_bold;
    private static final String characters;

    static {
        StringBuilder chars = new StringBuilder();
        for (int i = 0x20; i < 0x7B; i++) chars.append((char) i);
        for (int i = 0x401; i < 0x452; i++) chars.append((char) i);
        chars.append('₽');
        characters = chars.toString();
    }

    // more font stuff
    public static final FontType BUTTON_ACTIVE = new FontType(85, THEME_LIGHT.textButton());
    public static final FontType BUTTON_INACTIVE = new FontType(85, THEME_LIGHT.textInactive());
    public static final FontType FPS = new FontType(50, Color.WHITE);

    public static record FontType(int size, Color color) {}

    private Map<FontType, BitmapFont> fonts_default;
    private Map<FontType, BitmapFont> fonts_bold;

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    public static final DateFormat TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    public static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
    public static final DateFormat AUTOSAVE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss.SSS");

    public DynamicPlanimetry(CompoundTag settings, SettingsIO io) {
        super();
        SETTINGS.fromNbt(settings);
        this.settingsFile = io;
    }

    @Override
    public void create() {
        gen_default = new FreeTypeFontGenerator(Gdx.files.internal("denhome.otf"));
        gen_bold = new FreeTypeFontGenerator(Gdx.files.internal("dhmbold.ttf"));
        fonts_default = new HashMap<>();
        fonts_bold = new HashMap<>();

        mainMenu = registerScreen(new MainMenuScreen(this));
        editorScreen = registerScreen(new EditorScreen(this));
        fileSelectionScreen = registerScreen(new FileSelectionScreen(this));
        settingsScreen = registerScreen(new SettingsScreen(this));
        setDrawing(null, false);
        setScreen(MAIN_MENU);
        if (isDebug()) {
            Notifications.addNotification("Включён режим отладки", 2000);
        }
        try {
            Gdx.files.local("drawings").mkdirs();
        } catch (Exception e) {
            if (isDebug()) {
                Notifications.addNotification(e.getMessage(), 7500);
            }
        }
    }

    private <T extends FlatUIScreen> T registerScreen(T screen) {
        this.screenByIds.add(screen);
        screen.hide();
        return screen;
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        for (BitmapFont i : fonts_default.values()) {
            i.dispose();
        }
        for (BitmapFont i : fonts_bold.values()) {
            i.dispose();
        }
        SETTINGS.toNbt(settingsFile.getSettingsFile());
    }

    @Override
    public void pause() {
        super.pause();
    }

    public BitmapFont getFont(int size, Color color) {
        return getFont(new FontType(size, color));
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

    public BitmapFont getBoldFont(int size, Color color) {
        return getBoldFont(new FontType(size, color));
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
        this.setScreen(screenByIds.get(id));
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
        for (FileHandle i : Gdx.files.absolute(folder).list()) {
            try {
                drawings.add(Drawing.load(i.path()));
            } catch (Exception e) {
                Notifications.addNotification("Ошибка при загрузке файла (" + i.nameWithoutExtension() + "):  " + e.getMessage(), 5000);
                e.printStackTrace();
            } catch (StackOverflowError e) {
                Notifications.addNotification("Ошибка при загрузке файла (" + i.nameWithoutExtension() + "):  " + e.getMessage(), 5000);
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

    public static String formatNumber(double number) {
        return String.format((Locale)null, "%." + SETTINGS.getDisplayPresicion() + "f", number);
    }

    public static DynamicPlanimetry getInstance() {
        return (DynamicPlanimetry)Gdx.app.getApplicationListener();
    }

    public static boolean isDebug() {
        return SETTINGS.isDebug();
    }
}