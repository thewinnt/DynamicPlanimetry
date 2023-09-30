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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import net.thewinnt.planimetry.screen.FlatUIScreen;
import net.thewinnt.planimetry.screen.MainMenuScreen;
import net.thewinnt.planimetry.ui.Notifications;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class DynamicPlanimetry extends Game {
    public static final int MAIN_MENU = 0;

    // general stuff
    public final List<Screen> screenByIds = new ArrayList<>();
    public boolean balance_loaded = false;
    public static boolean IS_MOBILE = false;
    public String last_fps = "FPS: ..."; // the last reading of the fps counter
    public static final boolean DEBUG_MODE = true;

    // preloaded files
    // (there's none of them)

    // screens
    public MainMenuScreen mainMenu;

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
    /** 12 */ public static final Color COLOR_MAIN = new Color(rgbColor(3, 209, 255));
    /** 13 */ public static final Color COLOR_PRESSED = new Color(rgbColor(0, 156, 191));
    /** 14 */ public static final Color COLOR_BUTTON = new Color(rgbColor(1, 175, 216));
    /** 16 */ public static final Color COLOR_INACTIVE = new Color(rgbColor(45, 45, 45));

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

    public DynamicPlanimetry() {
        super();
        if (DEBUG_MODE) {
            Notifications.addNotification("App started in debug mode", 5000);
        }
        Notifications.addNotification("This is a test notification please ignore", 7000);
        Notifications.addNotification("This is a test notification please ignore, except its another one loll", 7000);
    }

    @Override
    public void create() {
        gen_default = new FreeTypeFontGenerator(Gdx.files.internal("denhome.otf"));
        gen_bold = new FreeTypeFontGenerator(Gdx.files.internal("dhmbold.ttf"));
        fonts_default = new HashMap<>();
        fonts_bold = new HashMap<>();

        mainMenu = registerScreen(new MainMenuScreen(this));
        setScreen(MAIN_MENU);
    }

    private <T extends FlatUIScreen> T registerScreen(T screen) {
        this.screenByIds.add(screen);
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
        if (type.size == 0) return getFont(FPS);
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
        if (type.size == 0) return getBoldFont(FPS);
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
}