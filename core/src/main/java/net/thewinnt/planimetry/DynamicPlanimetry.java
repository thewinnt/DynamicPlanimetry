package net.thewinnt.planimetry;

import static net.thewinnt.gdxutils.ColorUtils.rgbColor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.screen.EditorScreen;
import net.thewinnt.planimetry.screen.FileSelectionScreen;
import net.thewinnt.planimetry.screen.FlatUIScreen;
import net.thewinnt.planimetry.screen.MainMenuScreen;
import net.thewinnt.planimetry.ui.Notifications;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class DynamicPlanimetry extends Game {
    // public constants
    public static final int MAIN_MENU = 0;
    public static final int EDITOR_SCREEN = 1;
    public static final int FILE_SELECTION_SCREEN = 2;
    public static final Nbt NBT = new Nbt();

    // general stuff
    public final List<Screen> screenByIds = new ArrayList<>();
    public String last_fps = "FPS: ..."; // the last reading of the fps counter
    public static final boolean DEBUG_MODE = true;
    public static boolean IS_MOBILE = false;

    // settings
    static byte displayPrecision = 6; // the precision of the displayed numbers, in digits
    static byte calculationPrecision = -23; // accounts for doubles not being precise enough

    // data
    private Drawing currentDrawing;
    private List<Drawing> allDrawings;

    // screens
    public MainMenuScreen mainMenu;
    public EditorScreen editorScreen;
    public FileSelectionScreen fileSelectionScreen;

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

    // colors
    public static final Color COLOR_MAIN = new Color(rgbColor(255, 255, 255));
    public static final Color COLOR_PRESSED = new Color(rgbColor(220, 220, 220));
    public static final Color COLOR_BUTTON = new Color(rgbColor(240, 240, 240));
    public static final Color COLOR_DELIMITER = new Color(0xC0C0C0FF);
    public static final Color COLOR_INACTIVE = new Color(rgbColor(45, 45, 45));
    public static final Color COLOR_SHAPE = Color.BLACK.cpy();
    public static final Color COLOR_SHAPE_HOVER = new Color(0, 0.75f, 1, 1);
    public static final Color COLOR_SHAPE_SELECTED = new Color(0, 0.5f, 1, 1);
    public static final Color COLOR_POINT = new Color(1, 0.5f, 0, 1);
    public static final Color COLOR_POINT_HOVER = new Color(0xFFC84CFF);
    public static final Color COLOR_POINT_SELECTED = new Color(0xCC5500FF);
    public static final Color COLOR_UTIL_POINT = new Color(0x0080FFFF);
    public static final Color COLOR_UTIL_POINT_HOVER = new Color(0x00C4FFFF);
    public static final Color COLOR_UTIL_POINT_SELECTED = new Color(0x0055CCFF);
    public static final Color COLOR_GRID = new Color(0xCCCCCCFF);
    public static final Color COLOR_GRID_HINT = new Color(0xAAAAAAFF);

    // more font stuff
    public static final FontType BUTTON_ACTIVE = new FontType(85, Color.BLACK);
    public static final FontType BUTTON_INACTIVE = new FontType(85, COLOR_INACTIVE);
    public static final FontType FPS = new FontType(50, Color.WHITE);

    public static record FontType(int size, Color color) {}

    private Map<FontType, BitmapFont> fonts_default;
    private Map<FontType, BitmapFont> fonts_bold;

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    public static final DateFormat TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    public static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
    public static final DateFormat AUTOSAVE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss.SSS");

    public DynamicPlanimetry() {
        super();
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
        setScreen(MAIN_MENU);
        if (DEBUG_MODE) {
            Notifications.addNotification("Включён режим отладки", 2000);
        }
        try {
            Gdx.files.local("drawings").mkdirs();
        } catch (Exception e) {
            if (DEBUG_MODE) {
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

    public static double getPrecisionFactor() {
        return Math.pow(2, calculationPrecision);
    }

    public static byte getDisplayPresicion() {
        return displayPrecision;
    }

    public void setDrawing(Drawing drawing, boolean saveOld) {
        if (saveOld && this.currentDrawing != null && this.currentDrawing.hasChanged()) {
            currentDrawing.save();
        }
        this.currentDrawing = drawing;
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
            }
        }
        this.allDrawings = drawings;
    }

    public List<Drawing> getAllDrawings() {
        return allDrawings;
    }
}