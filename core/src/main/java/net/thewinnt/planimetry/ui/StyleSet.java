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
    private LabelStyle labelStyle;
    private ButtonStyle checkboxStyle;

    public StyleSet() {}

    public StyleSet(TextButtonStyle buttonStyle, TextFieldStyle textFieldStyle, SelectBoxStyle listStyle, LabelStyle labelStyle, ButtonStyle checkboxStyle) {
        this.buttonStyle = buttonStyle;
        this.textFieldStyle = textFieldStyle;
        this.listStyle = listStyle;
        this.labelStyle = labelStyle;
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

    public void setLabelStyle(LabelStyle labelStyle) {
        this.labelStyle = labelStyle;
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

    public LabelStyle getLabelStyle() {
        return labelStyle;
    }

    public ButtonStyle getCheckboxStyle() {
        return checkboxStyle;
    }
}
