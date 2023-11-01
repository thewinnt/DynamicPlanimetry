package net.thewinnt.planimetry.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;

public class StyleSet {
    private TextButtonStyle buttonStyle;
    private TextFieldStyle textFieldStyle;
    private SelectBoxStyle listStyle;
    private LabelStyle labelStyleLarge;
    private LabelStyle labelStyleSmall;
    private ButtonStyle checkboxStyle;

    public StyleSet() {}

    public StyleSet(TextButtonStyle buttonStyle, TextFieldStyle textFieldStyle, SelectBoxStyle listStyle, LabelStyle labelStyleLarge, LabelStyle labelStyleSmall, ButtonStyle checkboxStyle) {
        this.buttonStyle = buttonStyle;
        this.textFieldStyle = textFieldStyle;
        this.listStyle = listStyle;
        this.labelStyleLarge = labelStyleLarge;
        this.labelStyleSmall = labelStyleSmall;
        this.checkboxStyle = checkboxStyle;
    }

    public void setButtonStyle(TextButtonStyle buttonStyle) {
        this.buttonStyle = buttonStyle;
    }

    public void setTextFieldStyle(TextFieldStyle textFieldStyle) {
        this.textFieldStyle = textFieldStyle;
    }

    public void setListStyle(SelectBoxStyle listStyle) {
        this.listStyle = listStyle;
    }

    public void setLabelStyleLarge(LabelStyle labelStyle) {
        this.labelStyleLarge = labelStyle;
    }

    public void setLabelStyleSmall(LabelStyle labelStyle) {
        this.labelStyleSmall = labelStyle;
    }

    public void setCheckboxStyle(ButtonStyle checkboxStyle) {
        this.checkboxStyle = checkboxStyle;
    }

    public TextButtonStyle getButtonStyle() {
        return buttonStyle;
    }

    public TextFieldStyle getTextFieldStyle() {
        return textFieldStyle;
    }

    public SelectBoxStyle getListStyle() {
        return listStyle;
    }

    public LabelStyle getLabelStyleLarge() {
        return labelStyleLarge;
    }

    public LabelStyle getLabelStyleSmall() {
        return labelStyleSmall;
    }

    public ButtonStyle getCheckboxStyle() {
        return checkboxStyle;
    }
}
