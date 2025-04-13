package net.thewinnt.planimetry.ui.properties;

import net.thewinnt.planimetry.Settings;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.types.NumberProperty;
import net.thewinnt.planimetry.ui.properties.types.PropertyGroup;
import net.thewinnt.planimetry.ui.properties.types.ShapeProperty;
import net.thewinnt.planimetry.ui.text.Component;

import java.util.function.Consumer;

public class PropertyHelper {
    public static PropertyGroup swappablePoint(PointProvider point, Consumer<PointProvider> setter, String nameKey, Object... textArgs) {
        point.rebuildProperties();
        PropertyGroup output = new PropertyGroup(point.getName());
        ShapeProperty selector = new ShapeProperty(Component.translatable(nameKey, textArgs), point.getDrawing(), point, shape -> shape instanceof PointProvider);
        selector.addValueChangeListener(shape -> {
            setter.accept((PointProvider) shape);
            shape.rebuildProperties();
        });
        output.addProperty(selector);
        output.addProperties(point.getProperties());
        return output;
    }

    public static <E, T extends Property<E>> T setter(T property, Consumer<E> setter) {
        property.addValueChangeListener(setter);
        return property;
    }

    public static NumberProperty angle(String nameKey, double currentRad, Consumer<Double> setterRad) {
        NumberProperty output = new NumberProperty(Component.translatable(nameKey), Settings.get().toUnit(currentRad));
        output.addValueChangeListener(unit -> setterRad.accept(Settings.get().toRadians(unit)));
        return output;
    }
}
