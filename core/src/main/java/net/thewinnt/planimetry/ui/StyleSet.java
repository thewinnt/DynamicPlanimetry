package net.thewinnt.planimetry.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;

public class StyleSet {
    public TextButtonStyle buttonStyle;
    public TextButtonStyle buttonStyleInactive;
    public TextFieldStyle textFieldStyle;
    public SelectBoxStyle listStyle;
    public LabelStyle labelStyleLarge;
    public LabelStyle labelStyleSmall;
    public ButtonStyle checkboxStyle;
    public WindowStyle windowStyle;

    public StyleSet() {}

    public StyleSet(TextButtonStyle buttonStyle, TextButtonStyle buttonStyleInactive, TextFieldStyle textFieldStyle, SelectBoxStyle listStyle, LabelStyle labelStyleLarge, LabelStyle labelStyleSmall, ButtonStyle checkboxStyle) {
        this.buttonStyle = buttonStyle;
        this.buttonStyleInactive = buttonStyleInactive;
        this.textFieldStyle = textFieldStyle;
        this.listStyle = listStyle;
        this.labelStyleLarge = labelStyleLarge;
        this.labelStyleSmall = labelStyleSmall;
        this.checkboxStyle = checkboxStyle;
    }

    @Deprecated(forRemoval = true)
    public void setButtonStyle(TextButtonStyle buttonStyle) {
        this.buttonStyle = buttonStyle;
    }

    @Deprecated(forRemoval = true)
    public void setButtonStyleInactive(TextButtonStyle buttonStyleInactive) {
        this.buttonStyleInactive = buttonStyleInactive;
    }

    @Deprecated(forRemoval = true)
    public void setTextFieldStyle(TextFieldStyle textFieldStyle) {
        this.textFieldStyle = textFieldStyle;
    }

    @Deprecated(forRemoval = true)
    public void setListStyle(SelectBoxStyle listStyle) {
        this.listStyle = listStyle;
    }

    @Deprecated(forRemoval = true)
    public void setLabelStyleLarge(LabelStyle labelStyle) {
        this.labelStyleLarge = labelStyle;
    }

    @Deprecated(forRemoval = true)
    public void setLabelStyleSmall(LabelStyle labelStyle) {
        this.labelStyleSmall = labelStyle;
    }

    @Deprecated(forRemoval = true)
    public void setCheckboxStyle(ButtonStyle checkboxStyle) {
        this.checkboxStyle = checkboxStyle;
    }

    @Deprecated(forRemoval = true)
    public void setWindowStyle(WindowStyle windowStyle) {
        this.windowStyle = windowStyle;
    }

    @Deprecated(forRemoval = true)
    public TextButtonStyle getButtonStyle() {
        return buttonStyle;
    }

    @Deprecated(forRemoval = true)
    public TextButtonStyle getButtonStyleInactive() {
        return buttonStyleInactive;
    }

    @Deprecated(forRemoval = true)
    public TextFieldStyle getTextFieldStyle() {
        return textFieldStyle;
    }

    @Deprecated(forRemoval = true)
    public SelectBoxStyle getListStyle() {
        return listStyle;
    }

    @Deprecated(forRemoval = true)
    public LabelStyle getLabelStyleLarge() {
        return labelStyleLarge;
    }

    @Deprecated(forRemoval = true)
    public LabelStyle getLabelStyleSmall() {
        return labelStyleSmall;
    }

    @Deprecated(forRemoval = true)
    public ButtonStyle getCheckboxStyle() {
        return checkboxStyle;
    }

    @Deprecated(forRemoval = true)
    public WindowStyle getWindowStyle() {
        return windowStyle;
    }
}
