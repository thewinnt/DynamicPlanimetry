package net.thewinnt.planimetry.ui.properties.types;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.Locale;
import java.util.OptionalDouble;
import java.util.function.Predicate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.Notifications;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.text.Component;

public class NumberProperty extends Property<Double> {
    private final List<Consumer<Double>> listeners = new ArrayList<>();
    private double prevValue;
    private double value;
    private OptionalDouble maxValue = OptionalDouble.empty();
    private OptionalDouble minValue = OptionalDouble.empty();
    private Predicate<Double> filter;
    private boolean isWhole;
    private boolean liveUpdates = true;

    public NumberProperty() {
        super(Component.empty());
    }

    public NumberProperty(double value) {
        super(Component.empty());
        this.value = value;
    }

    public NumberProperty(Component name) {
        super(name);
    }

    public NumberProperty(Component name, double value) {
        super(name);
        this.value = value;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public void setValue(Double value) {
        if (filter == null || filter.test(value)) {
            if (isWhole) {
                value = (double)Math.round(value);
            }
            if (maxValue.isPresent() && value > maxValue.getAsDouble()) {
                value = maxValue.getAsDouble();
            } else if (minValue.isPresent() && value < minValue.getAsDouble()) {
                value = minValue.getAsDouble();
            }
            this.value = value;
            if (liveUpdates) {
                realUpdate();
            }
        }
    }

    private void realUpdate() {
        for (Consumer<Double> i : this.listeners) {
            i.accept(value);
        }
    }

    @Override
    public Table getActorSetup(StyleSet styles, Size size) {
        String fieldText;
        if (!isWhole) {
            fieldText = String.format((Locale)null, "%." + DynamicPlanimetry.SETTINGS.getDisplayPresicion() + "f", value);
        } else {
            fieldText = String.valueOf((long)value);
        }
        TextField doubleField = new TextField(fieldText, styles.getTextFieldStyle(size, true)) {
            @Override
            public float getPrefWidth() {
                return Gdx.graphics.getHeight() / 30;
            }
        };
        doubleField.setTextFieldFilter((textField, character) -> {
            if (character == 'e') return false;
            try {
                String text = textField.getText();
                int cursor = textField.getCursorPosition();
                if (cursor > text.length()) cursor = text.length();
                Double.parseDouble(text.substring(0, cursor) + character + text.substring(cursor) + '0');
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                return false;
            }
            return true;
        });
        doubleField.setTextFieldListener((textField, c) -> {
            try {
                prevValue = value;
                setValue(Double.valueOf(doubleField.getText()));
            } catch (NumberFormatException e) {}
        });
        doubleField.addListener(new InputListener() {
            private void unfocus() {
                doubleField.getStage().setKeyboardFocus(null);
                try {
                    setValue(Double.valueOf(doubleField.getText()));
                } catch (NumberFormatException e) {
                    value = prevValue;
                    Notifications.addNotification("Invalid number: " + doubleField.getText(), 1000);
                }
                doubleField.setText(String.format((Locale)null, "%." + DynamicPlanimetry.SETTINGS.getDisplayPresicion() + "f", value));
                realUpdate();
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

    public NumberProperty withMax(double maxValue) {
        if (minValue.isPresent() && maxValue < minValue.getAsDouble()) {
            throw new IllegalArgumentException("Max value must not be smaller than min value");
        }
        this.maxValue = OptionalDouble.of(maxValue);
        return this;
    }

    public NumberProperty withMin(double minValue) {
        if (maxValue.isPresent() && minValue > maxValue.getAsDouble()) {
            throw new IllegalArgumentException("Min value must not be larger than max value");
        }
        this.minValue = OptionalDouble.of(minValue);
        return this;
    }

    public NumberProperty requireWholeNumbers(boolean require) {
        this.isWhole = require;
        return this;
    }

    public NumberProperty filtered(Predicate<Double> filter) {
        this.filter = filter;
        return this;
    }

    public NumberProperty noLiveUpdates() {
        this.liveUpdates = false;
        return this;
    }
}
