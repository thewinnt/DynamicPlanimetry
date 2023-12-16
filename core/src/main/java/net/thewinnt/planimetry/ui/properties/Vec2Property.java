package net.thewinnt.planimetry.ui.properties;

import java.util.function.Consumer;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.StyleSet;

public class Vec2Property extends Property<Vec2> {
    private final NumberProperty x;
    private final NumberProperty y;

    public Vec2Property(String name) {
        super(name);
        this.x = new NumberProperty("X");
        this.y = new NumberProperty("Y");
    }

    public Vec2Property(String name, Vec2 value) {
        super(name);
        this.x = new NumberProperty("X", value.x);
        this.y = new NumberProperty("Y", value.y);
    }

    @Override
    public Vec2 getValue() {
        return new Vec2(x.getValue(), y.getValue());
    }

    @Override
    public void setValue(Vec2 value) {
        x.setValue(value.x);
        y.setValue(value.y);
    }

    @Override
    public void addValueChangeListener(Consumer<Vec2> listener) {
        x.addValueChangeListener(ignore -> listener.accept(getValue()));
        y.addValueChangeListener(ignore -> listener.accept(getValue()));
    }

    @Override
    public WidgetGroup getActorSetup(StyleSet styles) {
        Table table = new Table();
        table.add(x.getActorSetup(styles)).expand().fill();
        table.add(y.getActorSetup(styles)).expand().fill().padLeft(5);
        return table;
    }
}
