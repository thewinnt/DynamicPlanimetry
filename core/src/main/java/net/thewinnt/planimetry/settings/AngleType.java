package net.thewinnt.planimetry.settings;

import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.ComponentRepresentable;

import java.util.function.DoubleFunction;

public enum AngleType implements ComponentRepresentable {
    GRADIANS(Component.translatable("angle.gradians"), t -> t * MathHelper.RADIANS_TO_GRADIANS, t -> t * MathHelper.GRADIANS_TO_RADIANS, Component.translatable("angle.gradians.unit"), 400),
    RADIANS(Component.translatable("angle.radians"), t -> t, t -> t, Component.translatable("angle.radians.unit"), Math.PI * 2),
    DEGREES(Component.translatable("angle.degrees"), Math::toDegrees, Math::toRadians, Component.translatable("angle.degrees.unit"), 360);

    private final DoubleFunction<Double> toUnit;
    private final DoubleFunction<Double> toRadians;
    public final Component name;
    public final CharSequence unit;
    public final double max;

    private AngleType(Component name, DoubleFunction<Double> toUnit, DoubleFunction<Double> toRadians, CharSequence unit, double max) {
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

    public CharSequence getUnit() {
        return unit;
    }

    public double getMax() {
        return max;
    }
}
