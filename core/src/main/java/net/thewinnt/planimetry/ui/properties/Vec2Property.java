package net.thewinnt.planimetry.ui.properties;

import java.util.Map;

import net.thewinnt.planimetry.math.Vec2;

public class Vec2Property extends Property<Vec2> {
    private final DoubleProperty x;
    private final DoubleProperty y;
    private final Map<Parameter<?>, String> parameters;

    public Vec2Property(String name) {
        super(name);
        this.x = new DoubleProperty();
        this.y = new DoubleProperty();
        this.parameters = Map.of(x, "X coordinate", y, "Y coordinate");
    }

    public Vec2Property(String name, Vec2 value) {
        super(name);
        this.x = new DoubleProperty(value.x);
        this.y = new DoubleProperty(value.y);
        this.parameters = Map.of(x, "X coordinate", y, "Y coordinate");
    }

    @Override
    public Vec2 buildResult() {
        return new Vec2(x.getValue(), y.getValue());
    }
    
    @Override
    public Map<Parameter<?>, String> getParameters() {
        return this.parameters;
    }

    @Override
    public void addValueChangeListener(Runnable listener) {
        x.addValueChangeListener(ignore -> listener.run());
        y.addValueChangeListener(ignore -> listener.run());
    }
}
