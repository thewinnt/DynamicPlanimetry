package net.thewinnt.planimetry.ui;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;

import net.thewinnt.planimetry.ui.drawable.CheckboxDrawable;
import net.thewinnt.planimetry.ui.drawable.DynamicIcon;
import net.thewinnt.planimetry.ui.drawable.RectangleDrawable;
import net.thewinnt.planimetry.ui.drawable.RectangledIconDrawable;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class StyleSet {
    public final ShapeDrawer drawer;
    public final FontProvider font;
    public final RectangleDrawable normal;
    public final RectangleDrawable pressed;
    public final RectangleDrawable over;
    public final RectangleDrawable disabled;
    public final RectangleDrawable field;
    public final RectangleDrawable fullBlack;
    public final RectangleDrawable selection;
    public final RectangleDrawable fieldInactive;
    public final RectangleDrawable cursor;
    public final RectangleDrawable cursorInactive;
    public final RectangleDrawable selectionInactive;
    public final RectangleDrawable fullWhite;
    public final RectangleDrawable selectionOver;
    public final RectangleDrawable checkboxNormal;
    public final RectangleDrawable checkboxPressed;
    public final RectangleDrawable checkboxOver;
    public final RectangleDrawable checkboxDisabled;
    public final RectangleDrawable closeNormal;
    public final RectangleDrawable closePressed;
    public final RectangleDrawable closeOver;
    public final RectangleDrawable closeDisabled;
    public final RectangleDrawable minimizeNormal;
    public final RectangleDrawable minimizePressed;
    public final RectangleDrawable minimizeOver;
    public final RectangleDrawable minimizeDisabled;
    public final RectangleDrawable maximizeNormal;
    public final RectangleDrawable maximizePressed;
    public final RectangleDrawable maximizeOver;
    public final RectangleDrawable maximizeDisabled;
    public final RectangleDrawable fullOutline;
    public final RectangleDrawable fullMain;
    private final Map<StyleType, TextButtonStyle> buttonStyles = new HashMap<>();
    private final Map<Size, TextButtonStyle> buttonStylesToggleable = new EnumMap<>(Size.class);
    private final Map<Size, TextButtonStyle> buttonStylesNoBg = new EnumMap<>(Size.class);
    private final Map<StyleType, TextFieldStyle> textFieldStyles = new HashMap<>();
    private final Map<Size, SelectBoxStyle> listStyles = new EnumMap<>(Size.class);
    private final Map<Size, LabelStyle> labelStyles = new EnumMap<>(Size.class);
    private final Map<StyleType, ButtonStyle> checkboxStyles = new HashMap<>();
    private final Map<StyleType, ButtonStyle> closeStyles = new HashMap<>();
    private final Map<StyleType, ButtonStyle> minimizeStyles = new HashMap<>();
    private final Map<StyleType, ButtonStyle> maximizeStyles = new HashMap<>();
    private final Map<Size, WindowStyle> windowStyles = new EnumMap<>(Size.class);
    private ScrollPaneStyle scrollPaneStyleNoBg;
    private int height;

    public StyleSet(ShapeDrawer drawer, FontProvider font) {
        this.drawer = drawer;
        this.font = font;
        this.fullBlack = new RectangleDrawable(drawer).withColors(Color.BLACK.cpy(), Color.BLACK.cpy());
        this.fullWhite = new RectangleDrawable(drawer).withColors(Color.WHITE.cpy(), Color.WHITE.cpy());
        this.normal = new RectangleDrawable(drawer).withColors(Theme.current().button(), Theme.current().outline());
        this.pressed = new RectangleDrawable(drawer).withColors(Theme.current().pressed(), Theme.current().outline());
        this.over = new RectangleDrawable(drawer).withColors(Theme.current().main(), Theme.current().outline());
        this.disabled = new RectangleDrawable(drawer).withColors(Theme.current().pressed(), Theme.current().inactive());
        this.field = new RectangleDrawable(drawer).withColors(Theme.current().textField(), Theme.current().outline());
        this.selection = new RectangleDrawable(drawer).withColors(Theme.current().textSelectionBg(), Theme.current().textSelectionBg());
        this.fieldInactive = new RectangleDrawable(drawer).withColors(Theme.current().main(), Theme.current().inactive());
        this.cursor = new RectangleDrawable(drawer).withColors(Theme.current().textUI(), Theme.current().textUI());
        this.cursorInactive = new RectangleDrawable(drawer).withColors(Theme.current().inactive(), Theme.current().inactive());
        this.selectionInactive = new RectangleDrawable(drawer).withColors(Color.DARK_GRAY, Color.DARK_GRAY);
        this.selectionOver = new RectangleDrawable(drawer).withColors(Theme.current().main(), null);
        this.checkboxNormal = new CheckboxDrawable(drawer).withColors(Theme.current().button(), Theme.current().outline());
        this.checkboxOver = new CheckboxDrawable(drawer).withColors(Theme.current().main(), Theme.current().outline());
        this.checkboxPressed = new CheckboxDrawable(drawer).withColors(Theme.current().pressed(), Theme.current().outline());
        this.checkboxDisabled = new CheckboxDrawable(drawer).withColors(Theme.current().pressed(), Theme.current().inactive());
        this.closeNormal = new RectangledIconDrawable(drawer, DynamicIcon.CLOSE).withColors(Theme.current().button(), Theme.current().outline());
        this.closeOver = new RectangledIconDrawable(drawer, DynamicIcon.CLOSE).withColors(Theme.current().main(), Theme.current().outline());
        this.closePressed = new RectangledIconDrawable(drawer, DynamicIcon.CLOSE).withColors(Theme.current().pressed(), Theme.current().outline());
        this.closeDisabled = new RectangledIconDrawable(drawer, DynamicIcon.CLOSE).withColors(Theme.current().pressed(), Theme.current().inactive());
        this.minimizeNormal = new RectangledIconDrawable(drawer, DynamicIcon.MINIMIZE).withColors(Theme.current().button(), Theme.current().outline());
        this.minimizeOver = new RectangledIconDrawable(drawer, DynamicIcon.MINIMIZE).withColors(Theme.current().main(), Theme.current().outline());
        this.minimizePressed = new RectangledIconDrawable(drawer, DynamicIcon.MINIMIZE).withColors(Theme.current().pressed(), Theme.current().outline());
        this.minimizeDisabled = new RectangledIconDrawable(drawer, DynamicIcon.MINIMIZE).withColors(Theme.current().pressed(), Theme.current().inactive());
        this.maximizeNormal = new RectangledIconDrawable(drawer, DynamicIcon.DOWN_TRIANGLE).withColors(Theme.current().button(), Theme.current().outline());
        this.maximizeOver = new RectangledIconDrawable(drawer, DynamicIcon.DOWN_TRIANGLE).withColors(Theme.current().main(), Theme.current().outline());
        this.maximizePressed = new RectangledIconDrawable(drawer, DynamicIcon.DOWN_TRIANGLE).withColors(Theme.current().pressed(), Theme.current().outline());
        this.maximizeDisabled = new RectangledIconDrawable(drawer, DynamicIcon.DOWN_TRIANGLE).withColors(Theme.current().pressed(), Theme.current().inactive());
        this.fullOutline = new CheckboxDrawable(drawer).withColors(Theme.current().outline(), Theme.current().outline());
        this.fullMain = new CheckboxDrawable(drawer).withColors(Theme.current().main(), Theme.current().main());
        rebuild();
    }

    public void rebuild() {
        rebuild(Math.min(Gdx.graphics.getHeight(), Gdx.graphics.getWidth()));
    }

    public void rebuild(final int height) {
        buttonStyles.clear();
        textFieldStyles.clear();
        listStyles.clear();
        labelStyles.clear();
        checkboxStyles.clear();
        windowStyles.clear();
        this.height = height;
    }

    public TextButtonStyle getButtonStyle(Size size, boolean isActive) {
        return buttonStyles.computeIfAbsent(new StyleType(size, isActive), type -> {
            if (type.isActive) {
                TextButtonStyle textButtonStyle = new TextButtonStyle(normal, pressed, normal, font.getFont(height / type.size.getFactor(), Theme.current().textButton()));
                textButtonStyle.over = over;
                textButtonStyle.checkedOver = over;
                return textButtonStyle;
            } else {
                return new TextButtonStyle(disabled, disabled, disabled, font.getFont(height / type.size.getFactor(), Theme.current().textInactive()));
            }
        });
    }

    public TextButtonStyle getButtonStyleToggleable(Size size) {
        return buttonStylesToggleable.computeIfAbsent(size, s -> {
            TextButtonStyle textButtonStyle = new TextButtonStyle(normal, pressed, pressed, font.getFont(height / s.getFactor(), Theme.current().textButton()));
            textButtonStyle.over = over;
            textButtonStyle.checkedOver = normal;
            textButtonStyle.checkedDown = over;
            return textButtonStyle;
        });
    }

    public TextButtonStyle getButtonStyleNoBg(Size size) {
        return buttonStylesNoBg.computeIfAbsent(size, s -> {
            TextButtonStyle textButtonStyle = new TextButtonStyle(null, null, null, font.getFont(height / s.getFactor(), Theme.current().textButton()));
            textButtonStyle.over = selectionOver;
            textButtonStyle.checkedOver = selectionOver;
            return textButtonStyle;
        });
    }

    public TextFieldStyle getTextFieldStyle(Size size, boolean isActive) {
        return textFieldStyles.computeIfAbsent(new StyleType(size, isActive), type -> {
            if (type.isActive) {
                TextFieldStyle textFieldStyle = new TextFieldStyle(font.getFont(height / type.size.getFactor(), Theme.current().textButton()), Theme.current().textButton(), cursor, selection, field);
                textFieldStyle.messageFontColor = Color.GRAY;
                textFieldStyle.messageFont = font.getFont(height / type.size.getFactor(), Color.GRAY);
                return textFieldStyle;
            } else {
                TextFieldStyle textFieldStyle = new TextFieldStyle(font.getFont(height / type.size.getFactor(), Theme.current().inactive()), Theme.current().inactive(), cursorInactive, selectionInactive, fieldInactive);
                textFieldStyle.messageFontColor = Color.GRAY;
                textFieldStyle.messageFont = font.getFont(height / type.size.getFactor(), Color.GRAY);
                return textFieldStyle;
            }
        });
    }

    public SelectBoxStyle getListStyle(Size size) {
        return listStyles.computeIfAbsent(size, s -> {
            ScrollPaneStyle paneStyle = new ScrollPaneStyle(fullBlack, fullBlack, fullWhite, fullBlack, fullWhite);
            ListStyle innerStyle = new ListStyle(font.getFont(height / s.getFactor(), Theme.current().textButton()), Theme.current().textButton(), Theme.current().textButton(), selectionOver);
            innerStyle.over = selectionOver;
            innerStyle.background = normal;
            innerStyle.down = new RectangleDrawable(drawer).withColors(Theme.current().pressed(), null);
            SelectBoxStyle listStyle = new SelectBoxStyle(font.getFont(height / s.getFactor(), Theme.current().textButton()), Theme.current().textButton(), normal, paneStyle, innerStyle);
            listStyle.backgroundOver = over;
            listStyle.backgroundDisabled = disabled;
            listStyle.backgroundOpen = pressed;
            return listStyle;
        });
    }

    public LabelStyle getLabelStyle(Size size) {
        return labelStyles.computeIfAbsent(size, s -> new LabelStyle(font.getFont(height / s.getFactor(), Theme.current().textUI()), null));
    }

    public ButtonStyle getCheckboxStyle(Size size, boolean isActive) {
        return checkboxStyles.computeIfAbsent(new StyleType(size, isActive), type -> {
            if (type.isActive) {
                ButtonStyle checkBoxStyle = new ButtonStyle(normal, pressed, checkboxNormal);
                checkBoxStyle.checkedDown = checkboxPressed;
                checkBoxStyle.checkedOver = checkboxOver;
                checkBoxStyle.over = over;
                return checkBoxStyle;
            } else {
                return new ButtonStyle(disabled, disabled, checkboxDisabled);
            }
        });
    }

    public ButtonStyle getCloseStyle(Size size, boolean isActive) {
        return closeStyles.computeIfAbsent(new StyleType(size, isActive), type -> {
            if (type.isActive) {
                ButtonStyle style = new ButtonStyle(closeNormal, closePressed, closeNormal);
                style.over = closeOver;
                style.checkedOver = closeOver;
                return style;
            } else {
                return new ButtonStyle(closeDisabled, closeDisabled, closeDisabled);
            }
        });
    }

    public ButtonStyle getMinimizeStyle(Size size, boolean isActive) {
        return minimizeStyles.computeIfAbsent(new StyleType(size, isActive), type -> {
            if (type.isActive) {
                ButtonStyle style = new ButtonStyle(minimizeNormal, minimizePressed, minimizeNormal);
                style.over = minimizeOver;
                style.checkedOver = minimizeOver;
                return style;
            } else {
                return new ButtonStyle(minimizeDisabled, minimizeDisabled, minimizeDisabled);
            }
        });
    }

    public ButtonStyle getMaximizeStyle(Size size, boolean isActive) {
        return maximizeStyles.computeIfAbsent(new StyleType(size, isActive), type -> {
            if (type.isActive) {
                ButtonStyle style = new ButtonStyle(maximizeNormal, maximizePressed, maximizeNormal);
                style.over = maximizeOver;
                style.checkedOver = maximizeOver;
                return style;
            } else {
                return new ButtonStyle(maximizeDisabled, maximizeDisabled, maximizeDisabled);
            }
        });
    }

    public WindowStyle getWindowStyle(Size size) {
        return windowStyles.computeIfAbsent(size, s -> new WindowStyle(font.getFont(height / size.getFactor(), Theme.current().textButton()), Theme.current().textButton(), normal));
    }

    public ScrollPaneStyle getScrollPaneStyleNoBg() {
        if (scrollPaneStyleNoBg == null) {
            scrollPaneStyleNoBg = new ScrollPaneStyle(null, fullOutline, fullMain, fullOutline, fullMain);
        }
        return scrollPaneStyleNoBg;
    }

    public TextButtonStyle createButtonStyle(BitmapFont font, boolean isActive) {
        if (isActive) {
            TextButtonStyle textButtonStyle = new TextButtonStyle(normal, pressed, normal, font);
            textButtonStyle.over = over;
            textButtonStyle.checkedOver = over;
            return textButtonStyle;
        }
        return new TextButtonStyle(disabled, disabled, disabled, font);
    }

    public int getHeight() {
        return height;
    }

    public static record StyleType(Size size, boolean isActive) {}

}
