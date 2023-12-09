package net.thewinnt.planimetry.ui;

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
import net.thewinnt.planimetry.ui.drawable.RectangleDrawable;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class StyleSet {
    private final ShapeDrawer drawer;
    private final FontProvider font;
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
    private final Map<StyleType, TextButtonStyle> buttonStyles = new HashMap<>();
    private final Map<StyleType, TextFieldStyle> textFieldStyles = new HashMap<>();
    private final Map<Size, SelectBoxStyle> listStyles = new HashMap<>();
    private final Map<Size, LabelStyle> labelStyles = new HashMap<>();
    private final Map<StyleType, ButtonStyle> checkboxStyles = new HashMap<>();
    private final Map<Size, WindowStyle> windowStyles = new HashMap<>();

    public StyleSet(ShapeDrawer drawer, FontProvider font) {
        this.drawer = drawer;
        this.font = font;
        this.fullBlack = new RectangleDrawable(drawer).withColors(Color.BLACK, Color.BLACK);
        this.fullWhite = new RectangleDrawable(drawer).withColors(Color.WHITE, Color.WHITE);
        this.normal = new RectangleDrawable(drawer).withColors(Theme.current().button(), Theme.current().outline());
        this.pressed = new RectangleDrawable(drawer).withColors(Theme.current().pressed(), Theme.current().outline());
        this.over = new RectangleDrawable(drawer).withColors(Theme.current().main(), Theme.current().outline());
        this.disabled = new RectangleDrawable(drawer).withColors(Theme.current().pressed(), Theme.current().inactive());
        this.field = new RectangleDrawable(drawer).withColors(Theme.current().textField(), Theme.current().outline());
        this.selection = new RectangleDrawable(drawer).withColors(Color.BLUE, Color.BLUE);
        this.fieldInactive = new RectangleDrawable(drawer).withColors(Theme.current().main(), Theme.current().inactive());
        this.cursor = new RectangleDrawable(drawer).withColors(Theme.current().textUI(), Theme.current().textUI());
        this.cursorInactive = new RectangleDrawable(drawer).withColors(Theme.current().inactive(), Theme.current().inactive());
        this.selectionInactive = new RectangleDrawable(drawer).withColors(Color.DARK_GRAY, Color.DARK_GRAY);
        this.selectionOver = new RectangleDrawable(drawer).withColors(Theme.current().main(), null);
        this.checkboxNormal = new CheckboxDrawable(drawer).withColors(Theme.current().button(), Theme.current().outline());
        this.checkboxOver = new CheckboxDrawable(drawer).withColors(Theme.current().main(), Theme.current().outline());
        this.checkboxPressed = new CheckboxDrawable(drawer).withColors(Theme.current().pressed(), Theme.current().outline());
        this.checkboxDisabled = new CheckboxDrawable(drawer).withColors(Theme.current().pressed(), Theme.current().inactive());
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
            TextButtonStyle textButtonStyle = new TextButtonStyle(normal, pressed, normal, font.getFont(height / size.factor, Theme.current().textButton()));
            textButtonStyle.over = over;
            textButtonStyle.checkedOver = over;
            buttonStyles.put(new StyleType(size, true), textButtonStyle);
            textButtonStyle = new TextButtonStyle(disabled, disabled, disabled, font.getFont(height / size.factor, Theme.current().textInactive()));
            buttonStyles.put(new StyleType(size, false), textButtonStyle);

            // text field styles
            TextFieldStyle textFieldStyle = new TextFieldStyle(font.getFont(height / size.factor, Theme.current().textButton()), Theme.current().textButton(), cursor, selection, field);
            textFieldStyle.messageFontColor = Color.GRAY;
            textFieldStyle.messageFont = font.getFont(height / size.factor, Color.GRAY);
            textFieldStyles.put(new StyleType(size, true), textFieldStyle);
            textFieldStyle = new TextFieldStyle(font.getFont(height / size.factor, Theme.current().inactive()), Theme.current().inactive(), cursorInactive, selectionInactive, fieldInactive);
            textFieldStyle.messageFontColor = Color.GRAY;
            textFieldStyle.messageFont = font.getFont(height / size.factor, Color.GRAY);
            textFieldStyles.put(new StyleType(size, false), textFieldStyle);

            // list styles
            ScrollPaneStyle paneStyle = new ScrollPaneStyle(fullBlack, fullBlack, fullWhite, fullBlack, fullWhite);
            ListStyle innerStyle = new ListStyle(font.getFont(height / size.factor, Theme.current().textButton()), Theme.current().textButton(), Theme.current().inactive(), selectionOver);
            innerStyle.over = selectionOver;
            innerStyle.background = normal;
            innerStyle.down = new RectangleDrawable(drawer).withColors(Theme.current().pressed(), null);
            SelectBoxStyle listStyle = new SelectBoxStyle(font.getFont(height / size.factor, Theme.current().textButton()), Theme.current().textButton(), normal, paneStyle, innerStyle);
            listStyle.backgroundOver = over;
            listStyle.backgroundDisabled = disabled;
            listStyle.backgroundOpen = pressed;
            listStyles.put(size, listStyle);

            // label styles
            labelStyles.put(size, new LabelStyle(font.getFont(height / size.factor, Theme.current().textUI()), null));

            // checkbox styles
            ButtonStyle checkBoxStyle = new ButtonStyle(normal, pressed, checkboxNormal);
            checkBoxStyle.checkedDown = checkboxPressed;
            checkBoxStyle.checkedOver = checkboxOver;
            checkBoxStyle.over = over;
            checkboxStyles.put(new StyleType(size, true), checkBoxStyle);
            checkBoxStyle = new ButtonStyle(disabled, disabled, checkboxDisabled);
            checkboxStyles.put(new StyleType(size, false), checkBoxStyle);

            // window styles
            this.windowStyles.put(size, new WindowStyle(font.getFont(height / size.factor, Theme.current().textButton()), Theme.current().textButton(), normal));
        }
    }

    public TextButtonStyle getButtonStyle(Size size, boolean isActive) {
        return buttonStyles.get(new StyleType(size, isActive));
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
        SUPER_LARGE(7),
        VERY_LARGE(10),
        LARGE(14),
        MEDIUM(18),
        SMALL(24),
        VERY_SMALL(32);

        public final int factor;

        private Size(int factor) {
            this.factor = factor;
        }
    }
}
