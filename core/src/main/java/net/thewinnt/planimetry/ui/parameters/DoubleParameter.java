package net.thewinnt.planimetry.ui.parameters;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.Notifications;
import net.thewinnt.planimetry.ui.StyleSet;

public class DoubleParameter extends Parameter<Double> {
    private final Table setup;
    private final TextField doubleField;
    private double prevValue;
    private double value;

    public DoubleParameter(StyleSet styleSet) {
        super(styleSet);
        this.doubleField = new TextField("", styleSet.getTextFieldStyle());
        this.doubleField.setTextFieldFilter((textField, character) -> {
            if (character == 'e') return false;
            try {
                String text = textField.getText();
                int cursor = textField.getCursorPosition();
                if (cursor > text.length()) cursor = text.length();
                Double.parseDouble(text.substring(0, cursor) + character + text.substring(cursor) + '0');
            } catch (NumberFormatException e) {
                if (DynamicPlanimetry.DEBUG_MODE) Notifications.addNotification("Invalid number: " + e.getMessage(), 2500);
                return false;
            } catch (StringIndexOutOfBoundsException e) {
                if (DynamicPlanimetry.DEBUG_MODE) Notifications.addNotification(e.toString() + "; char = " + character, 2500);
                return false;
            }
            return true;
        });
        this.doubleField.setTextFieldListener((textField, c) -> {
            try {
                prevValue = value;
                value = Double.valueOf(textField.getText());
            } catch (NumberFormatException e) {
                if (DynamicPlanimetry.DEBUG_MODE) Notifications.addNotification("Couldn't filter number: " + e.getMessage(), 2500);
            }
        });
        this.doubleField.addListener(new InputListener() {
            private void unfocus() {
                doubleField.getStage().setKeyboardFocus(null);
                try {
                    value = Double.valueOf(doubleField.getText());
                } catch (NumberFormatException e) {
                    value = prevValue;
                    Notifications.addNotification("Invalid number: " + doubleField.getText(), 1000);
                }
                doubleField.setText(Double.toString(value));
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER) {
                    unfocus();
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (x < doubleField.getX() || y < doubleField.getY() || x > doubleField.getX() + doubleField.getWidth() || y > doubleField.getY() + doubleField.getHeight()) {
                    unfocus();
                    return true;
                }
                return false;
            }
        });
        this.setup = new Table();
        this.setup.add(doubleField).expand().fill();

    }

    public DoubleParameter(StyleSet styleSet, double value) {
        this(styleSet);
        this.value = value;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public Table getActorSetup() {
        return setup;
    }

    @Override
    public void updateStyles() {
        this.doubleField.setStyle(this.styleSet.getTextFieldStyle());
    }
}
