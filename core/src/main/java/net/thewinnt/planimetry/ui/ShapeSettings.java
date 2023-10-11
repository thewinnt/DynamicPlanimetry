package net.thewinnt.planimetry.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class ShapeSettings extends WidgetGroup {
    public final DrawingBoard board;
    private TextButtonStyle buttonStyle;
    private TextFieldStyle textFieldStyle;
    private SelectBoxStyle listStyle;
    private LabelStyle labelStyle;

    public ShapeSettings(DrawingBoard board) {
        this.board = board;
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
}
