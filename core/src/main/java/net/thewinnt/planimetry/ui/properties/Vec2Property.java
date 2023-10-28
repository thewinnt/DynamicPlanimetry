package net.thewinnt.planimetry.ui.properties;

import java.util.Map;

import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.parameters.DoubleParameter;
import net.thewinnt.planimetry.ui.parameters.Parameter;

public class Vec2Property extends Property<Vec2> {
    private final DoubleParameter x;
    private final DoubleParameter y;
    private final Map<Parameter<?>, String> parameters;

    public Vec2Property(String name, StyleSet styles) {
        super(name);
        this.x = new DoubleParameter(styles);
        this.y = new DoubleParameter(styles);
        this.parameters = Map.of(x, "X coordinate", y, "Y coordinate");
    }

    public Vec2Property(String name, StyleSet styles, Vec2 value) {
        super(name);
        this.x = new DoubleParameter(styles, value.x);
        this.y = new DoubleParameter(styles, value.y);
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
}
