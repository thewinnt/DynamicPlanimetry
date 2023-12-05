package net.thewinnt.planimetry.ui.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.Notifications;
import net.thewinnt.planimetry.ui.StyleSet;

public class DoubleProperty extends Property<Double> {
    private final List<Consumer<Double>> listeners = new ArrayList<>();
    private double prevValue;
    private double value;

    public DoubleProperty() {
        super("");
    }

    public DoubleProperty(double value) {
        super("");
        this.value = value;
    }

    public DoubleProperty(String name) {
        super(name);
    }

    public DoubleProperty(String name, double value) {
        super(name);
        this.value = value;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public Table getActorSetup(StyleSet styles) {
        TextField doubleField = new TextField(Double.toString(value), styles.getTextFieldStyle()) {
            @Override
            public float getPrefWidth() {
                // return Gdx.graphics.getHeight() / 30;
                return super.getPrefWidth();
                // TODO proper UI
            }
        };
        doubleField.setTextFieldFilter((textField, character) -> {
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
        doubleField.setTextFieldListener((textField, c) -> {
            try {
                prevValue = value;
                value = Double.valueOf(textField.getText());
                for (Consumer<Double> i : this.listeners) {
                    i.accept(value);
                }
            } catch (NumberFormatException e) {
                if (DynamicPlanimetry.DEBUG_MODE) Notifications.addNotification("Couldn't filter number: " + e.getMessage(), 2500);
            }
        });
        doubleField.addListener(new InputListener() {
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
        Table setup = new Table();
        setup.add(doubleField).expand().fill();
        return setup;
    }

    @Override
    public void addValueChangeListener(Consumer<Double> listener) {
        this.listeners.add(listener);
    }
}
