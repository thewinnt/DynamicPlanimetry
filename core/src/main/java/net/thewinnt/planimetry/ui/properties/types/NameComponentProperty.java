package net.thewinnt.planimetry.ui.properties.types;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.thewinnt.planimetry.ui.ComponentSelectBox;
import net.thewinnt.planimetry.ui.Notifications;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.NameComponent;

public class NameComponentProperty extends Property<NameComponent> {
    private final List<Consumer<NameComponent>> listeners = new ArrayList<>();
    private byte letter;
    private int index;
    private short dashes;
    private int prevIndex;
    private short prevDashes;

    public NameComponentProperty(Component name, NameComponent value) {
        super(name);
        if (value == null) {
            this.letter = 0;
            this.index = 0;
            this.dashes = 0;
        } else {
            this.letter = value.letter();
            this.index = value.index();
            this.dashes = value.dashes();
        }
    }

    @Override
    public void addValueChangeListener(Consumer<NameComponent> listener) {
        this.listeners.add(listener);
    }

    @Override
    public NameComponent getValue() {
        return new NameComponent(letter, index, dashes);
    }

    @Override
    public void setValue(NameComponent value) {
        this.letter = value.letter();
        this.index = value.index();
        this.dashes = value.dashes();
        for (Consumer<NameComponent> i : this.listeners) {
            i.accept(getValue());
        }
    }

    private void update() {
        NameComponent data = new NameComponent(letter, index, dashes);
        for (Consumer<NameComponent> i : this.listeners) {
            i.accept(data);
        }
    }

    @Override
    public WidgetGroup getActorSetup(StyleSet styles, Size size) {
        Table output = new Table();
        Table col1 = new Table();
        Table col2 = new Table();
        ComponentSelectBox<String> selector = new ComponentSelectBox<>(styles.getListStyle(size), List.of(NameComponent.ALLOWED_NAMES), Component::literal, size);
        selector.iconSizeOverride = Gdx.graphics.getHeight() / 30f;
        selector.setSelectedIndex(letter);
        selector.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                letter = (byte)selector.getSelectedIndex();
                update();
            }
        });
        TextField indexField = new TextField(Integer.toString(index), styles.getTextFieldStyle(size.smaller().smaller(), true)) {
            @Override
            public float getPrefWidth() {
                return Gdx.graphics.getHeight() / 30;
            }
        };
        indexField.setTextFieldFilter((textField, character) -> {
            try {
                String text = textField.getText();
                int cursor = textField.getCursorPosition();
                if (cursor > text.length()) cursor = text.length();
                Integer.parseInt(text.substring(0, cursor) + character + text.substring(cursor) + '0');
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                return false;
            }
            return true;
        });
        indexField.setTextFieldListener((textField, c) -> {
            try {
                prevIndex = index;
                index = Integer.parseInt(indexField.getText());
                update();
            } catch (NumberFormatException ignored) {}
        });
        indexField.addListener(new InputListener() {
            private void unfocus() {
                // indexField.getStage().setKeyboardFocus(null);
                try {
                    index = Integer.parseInt(indexField.getText());
                } catch (NumberFormatException e) {
                    index = prevIndex;
                    Notifications.addNotification("Invalid number: " + indexField.getText(), 1000);
                }
                indexField.setText(Integer.toString(index));
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
                if (x < indexField.getX() || y < indexField.getY() || x > indexField.getX() + indexField.getWidth() || y > indexField.getY() + indexField.getHeight()) {
                    unfocus();
                    return true;
                }
                return false;
            }
        });

        TextField dashesField = new TextField(Integer.toString(dashes), styles.getTextFieldStyle(size.smaller().smaller(), true)) {
            @Override
            public float getPrefWidth() {
                return Gdx.graphics.getHeight() / 30;
            }
        };
        dashesField.setTextFieldFilter((textField, character) -> {
            if (character == '-') return false;
            try {
                String text = textField.getText();
                int cursor = textField.getCursorPosition();
                if (cursor > text.length()) cursor = text.length();
                Short.parseShort(text.substring(0, cursor) + character + text.substring(cursor) + '0');
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                return false;
            }
            return true;
        });
        dashesField.setTextFieldListener((textField, c) -> {
            try {
                prevDashes = dashes;
                dashes = Short.parseShort(dashesField.getText());
                update();
            } catch (NumberFormatException e) {}
        });
        dashesField.addListener(new InputListener() {
            private void unfocus() {
                // dashesField.getStage().setKeyboardFocus(null);
                try {
                    dashes = Short.parseShort(dashesField.getText());
                } catch (NumberFormatException e) {
                    dashes = prevDashes;
                    Notifications.addNotification("Invalid number: " + dashesField.getText(), 1000);
                }
                dashesField.setText(Integer.toString(dashes));
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
                if (x < dashesField.getX() || y < dashesField.getY() || x > dashesField.getX() + dashesField.getWidth() || y > dashesField.getY() + dashesField.getHeight()) {
                    unfocus();
                    return true;
                }
                return false;
            }
        });

        col1.add(selector).expand().fillY().right();
        col2.add(dashesField).expand().fill();
        col2.row();
        col2.add(indexField).expand().fill();
        output.add(col1).expand().fill();
        output.add(col2).expand().fill();
        return output;
    }
}
