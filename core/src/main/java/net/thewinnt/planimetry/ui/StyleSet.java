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

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.drawable.CheckboxDrawable;
import net.thewinnt.planimetry.ui.drawable.RectangleDrawable;
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
    public final RectangleDrawable fullOutline;
    public final RectangleDrawable fullMain;
    private final Map<StyleType, TextButtonStyle> buttonStyles = new HashMap<>();
    private final Map<Size, TextButtonStyle> buttonStylesToggleable = new EnumMap<>(Size.class);
    private final Map<StyleType, TextFieldStyle> textFieldStyles = new HashMap<>();
    private final Map<Size, SelectBoxStyle> listStyles = new EnumMap<>(Size.class);
    private final Map<Size, LabelStyle> labelStyles = new EnumMap<>(Size.class);
    private final Map<StyleType, ButtonStyle> checkboxStyles = new HashMap<>();
    private final Map<Size, WindowStyle> windowStyles = new EnumMap<>(Size.class);
    private ScrollPaneStyle scrollPaneStyleNoBg;

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
        this.fullOutline = new CheckboxDrawable(drawer).withColors(Theme.current().outline(), Theme.current().outline());
        this.fullMain = new CheckboxDrawable(drawer).withColors(Theme.current().main(), Theme.current().main());
        rebuild();
    }

    public void rebuild() {
        rebuild(Gdx.graphics.getHeight());
    }

    public void rebuild(final int height) {
        buttonStyles.clear();
        textFieldStyles.clear();
        listStyles.clear();
        labelStyles.clear();
        checkboxStyles.clear();
        windowStyles.clear();
        for (Size size : Size.values()) {
            // text button styles
            TextButtonStyle textButtonStyle = new TextButtonStyle(normal, pressed, normal, font.getFont(height / size.getFactor(), Theme.current().textButton()));
            textButtonStyle.over = over;
            textButtonStyle.checkedOver = over;
            buttonStyles.put(new StyleType(size, true), textButtonStyle);
            textButtonStyle = new TextButtonStyle(disabled, disabled, disabled, font.getFont(height / size.getFactor(), Theme.current().textInactive()));
            buttonStyles.put(new StyleType(size, false), textButtonStyle);

            // toggleable text button styles
            textButtonStyle = new TextButtonStyle(normal, pressed, pressed, font.getFont(height / size.getFactor(), Theme.current().textButton()));
            textButtonStyle.over = over;
            textButtonStyle.checkedOver = normal;
            textButtonStyle.checkedDown = over;
            buttonStylesToggleable.put(size, textButtonStyle);

            // text field styles
            TextFieldStyle textFieldStyle = new TextFieldStyle(font.getFont(height / size.getFactor(), Theme.current().textButton()), Theme.current().textButton(), cursor, selection, field);
            textFieldStyle.messageFontColor = Color.GRAY;
            textFieldStyle.messageFont = font.getFont(height / size.getFactor(), Color.GRAY);
            textFieldStyles.put(new StyleType(size, true), textFieldStyle);
            textFieldStyle = new TextFieldStyle(font.getFont(height / size.getFactor(), Theme.current().inactive()), Theme.current().inactive(), cursorInactive, selectionInactive, fieldInactive);
            textFieldStyle.messageFontColor = Color.GRAY;
            textFieldStyle.messageFont = font.getFont(height / size.getFactor(), Color.GRAY);
            textFieldStyles.put(new StyleType(size, false), textFieldStyle);

            // list styles
            ScrollPaneStyle paneStyle = new ScrollPaneStyle(fullBlack, fullBlack, fullWhite, fullBlack, fullWhite);
            ListStyle innerStyle = new ListStyle(font.getFont(height / size.getFactor(), Theme.current().textButton()), Theme.current().textButton(), Theme.current().textButton(), selectionOver);
            innerStyle.over = selectionOver;
            innerStyle.background = normal;
            innerStyle.down = new RectangleDrawable(drawer).withColors(Theme.current().pressed(), null);
            SelectBoxStyle listStyle = new SelectBoxStyle(font.getFont(height / size.getFactor(), Theme.current().textButton()), Theme.current().textButton(), normal, paneStyle, innerStyle);
            listStyle.backgroundOver = over;
            listStyle.backgroundDisabled = disabled;
            listStyle.backgroundOpen = pressed;
            listStyles.put(size, listStyle);

            // label styles
            labelStyles.put(size, new LabelStyle(font.getFont(height / size.getFactor(), Theme.current().textUI()), null));

            // checkbox styles
            ButtonStyle checkBoxStyle = new ButtonStyle(normal, pressed, checkboxNormal);
            checkBoxStyle.checkedDown = checkboxPressed;
            checkBoxStyle.checkedOver = checkboxOver;
            checkBoxStyle.over = over;
            checkboxStyles.put(new StyleType(size, true), checkBoxStyle);
            checkBoxStyle = new ButtonStyle(disabled, disabled, checkboxDisabled);
            checkboxStyles.put(new StyleType(size, false), checkBoxStyle);

            // window styles
            this.windowStyles.put(size, new WindowStyle(font.getFont(height / size.getFactor(), Theme.current().textButton()), Theme.current().textButton(), normal));
        }
        // scroll pane style
        this.scrollPaneStyleNoBg = new ScrollPaneStyle(null, fullOutline, fullMain, fullOutline, fullMain);
    }

    public TextButtonStyle getButtonStyle(Size size, boolean isActive) {
        return buttonStyles.get(new StyleType(size, isActive));
    }

    public TextButtonStyle getButtonStyleToggleable(Size size) {
        return buttonStylesToggleable.get(size);
    }

    public TextFieldStyle getTextFieldStyle(Size size, boolean isActive) {
        return textFieldStyles.get(new StyleType(size, isActive));
    }

    public SelectBoxStyle getListStyle(Size size) {
        return listStyles.get(size);
    }

    public LabelStyle getLabelStyle(Size size) {
        return labelStyles.get(size);
    }

    public ButtonStyle getCheckboxStyle(Size size, boolean isActive) {
        return checkboxStyles.get(new StyleType(size, isActive));
    }

    public WindowStyle getWindowStyle(Size size) {
        return windowStyles.get(size);
    }

    public ScrollPaneStyle getScrollPaneStyleNoBg() {
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

    public static record StyleType(Size size, boolean isActive) {}

    public static enum Size {
        SUPER_LARGE(9),
        VERY_LARGE(12),
        LARGE(17),
        MEDIUM(22),
        SMALL(26),
        VERY_SMALL(36);

        private final int factor;

        private Size(int factor) {
            this.factor = factor;
        }

        public Size smaller() {
            return switch (this) {
                case SUPER_LARGE -> VERY_LARGE;
                case VERY_LARGE -> LARGE;
                case LARGE -> MEDIUM;
                case MEDIUM -> SMALL;
                default -> VERY_SMALL;
            };
        }

        public Size larger() {
            return switch (this) {
                case VERY_SMALL -> SMALL;
                case SMALL -> MEDIUM;
                case MEDIUM -> LARGE;
                case LARGE -> VERY_LARGE;
                default -> SUPER_LARGE;
            };
        }

        public float getFactor() {
            return factor / DynamicPlanimetry.getDisplayScaling();
        }
    }
}
