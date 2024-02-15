package net.thewinnt.planimetry.settings;

import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.ComponentRepresentable;

import java.util.function.DoubleFunction;

public enum AngleType implements ComponentRepresentable {
    GRADIANS(Component.literal("Градианы"), t -> t * MathHelper.RADIANS_TO_GRADIANS, t -> t * MathHelper.GRADIANS_TO_RADIANS, " град", 400),
    RADIANS(Component.literal("Радианы"), t -> t, t -> t, " рад", Math.PI * 2),
    DEGREES(Component.literal("Градусы"), t -> Math.toDegrees(t), t -> Math.toRadians(t), "°", 360); // TODO set angles in custom units too

    private final DoubleFunction<Double> toUnit;
    private final DoubleFunction<Double> toRadians;
    public final Component name;
    public final String unit;
    public final double max;

    private AngleType(Component name, DoubleFunction<Double> toUnit, DoubleFunction<Double> toRadians, String unit, double max) {
        this.name = name;
        this.toUnit = toUnit;
        this.toRadians = toRadians;
        this.unit = unit;
        this.max = max;
    }

    @Override
    public Component toComponent() {
        return name;
    }

    public double toUnit(double radians) {
        return toUnit.apply(radians);
    }

    public double toRadians(double unitval) {
        return toRadians.apply(unitval);
    }

    public String getUnit() {
        return unit;
    }

    public double getMax() {
        return max;
    }
}
