package net.thewinnt.planimetry.ui.properties.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.OptionalDouble;
import java.util.function.Consumer;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.Notifications;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.text.Component;

public class RestrictableNumberProperty extends Property<Double> {
    private final List<Consumer<Double>> listeners = new ArrayList<>();
    private double prevSuccess;
    private DoubleSupplier value;
    private DoublePredicate setter;
    private OptionalDouble maxValue = OptionalDouble.empty();
    private OptionalDouble minValue = OptionalDouble.empty();
    private boolean isWhole;
    private boolean liveUpdates = true;

    public RestrictableNumberProperty() {
        super(Component.empty());
    }

    public RestrictableNumberProperty(DoubleSupplier value, DoublePredicate setter) {
        super(Component.empty());
        this.value = value;
        this.setter = setter;
    }

    public RestrictableNumberProperty(Component name, DoubleSupplier value, DoublePredicate setter) {
        super(name);
        this.value = value;
        this.setter = setter;
    }

    @Override
    public Double getValue() {
        return value.getAsDouble();
    }

    @Override
    public void setValue(Double value) {
        if (isWhole) {
            value = (double)Math.round(value);
        }
        if (maxValue.isPresent() && value > maxValue.getAsDouble()) {
            value = maxValue.getAsDouble();
        } else if (minValue.isPresent() && value < minValue.getAsDouble()) {
            value = minValue.getAsDouble();
        }
        if (setter.test(value)) {
            this.prevSuccess = value;
            if (liveUpdates) {
                realUpdate();
            }
        } else {
        }
    }

    private void realUpdate() {
        double val = value.getAsDouble();
        for (Consumer<Double> i : this.listeners) {
            i.accept(val);
        }
    }

    @Override
    @SuppressWarnings("CheckResult") // we need the parseDouble to
    public Table getActorSetup(StyleSet styles, Size size) {
        String fieldText;
        if (!isWhole) {
            fieldText = String.format((Locale)null, "%." + DynamicPlanimetry.SETTINGS.getDisplayPresicion() + "f", value);
        } else {
            fieldText = String.valueOf((long)value.getAsDouble());
        }
        TextField doubleField = new TextField(fieldText, styles.getTextFieldStyle(size, true)) {
            @Override
            public float getPrefWidth() {
                return Gdx.graphics.getHeight() / 30f;
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
                setValue(Double.valueOf(doubleField.getText()));
                prevSuccess = value.getAsDouble();
            } catch (NumberFormatException e) {}
        });
        doubleField.addListener(new InputListener() {
            private void unfocus() {
                doubleField.getStage().setKeyboardFocus(null);
                try {
                    setValue(Double.valueOf(doubleField.getText()));
                } catch (NumberFormatException e) {
                    setter.test(prevSuccess);
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

    public RestrictableNumberProperty withMax(double maxValue) {
        if (minValue.isPresent() && maxValue < minValue.getAsDouble()) {
            throw new IllegalArgumentException("Max value must not be smaller than min value");
        }
        this.maxValue = OptionalDouble.of(maxValue);
        return this;
    }

    public RestrictableNumberProperty withMin(double minValue) {
        if (maxValue.isPresent() && minValue > maxValue.getAsDouble()) {
            throw new IllegalArgumentException("Min value must not be larger than max value");
        }
        this.minValue = OptionalDouble.of(minValue);
        return this;
    }

    public RestrictableNumberProperty requireWholeNumbers(boolean require) {
        this.isWhole = require;
        return this;
    }

    public RestrictableNumberProperty noLiveUpdates() {
        this.liveUpdates = false;
        return this;
    }
}
