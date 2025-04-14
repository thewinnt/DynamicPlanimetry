package net.thewinnt.planimetry.value.type;

import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.PropertyHelper;
import net.thewinnt.planimetry.ui.properties.types.SelectionProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.value.DynamicValue;
import net.thewinnt.planimetry.value.DynamicValueType;
import net.thewinnt.planimetry.value.serializer.PointCoordinateValueType;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class PointCoordinateValue implements DynamicValue {
    private PointProvider point;
    private Coordinate coordinate;

    public PointCoordinateValue(PointProvider point, Coordinate coordinate) {
        this.point = point;
        this.coordinate = coordinate;
    }

    @Override
    public double get() {
        return coordinate == Coordinate.X ? point.getX() : point.getY();
    }

    @Override
    public @NotNull DynamicValueType<? extends DynamicValue> type() {
        return PointCoordinateValueType.INSTANCE;
    }

    @Override
    public Stream<PointProvider> dependencies() {
        return Stream.of(point);
    }

    @Override
    public DynamicValue clone() {
        try {
            return (DynamicValue) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("We're Cloneable, but don't support cloning", e);
        }
    }

    @Override
    public Collection<Property<?>> properties() {
        return List.of(
            PropertyHelper.swappablePoint(point, t -> point = t, List.of(), false, "value.dynamic_planimetry.coordinate.point"),
            PropertyHelper.setter(new SelectionProperty<>(coordinate, Component.translatable("value.dynamic_planimetry.coordinate.coordinate"), Coordinate.values()), t -> coordinate = t)
        );
    }


    public PointProvider getPoint() {
        return point;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public enum Coordinate {
        X,
        Y
    }
}
