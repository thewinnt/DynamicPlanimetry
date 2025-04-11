package net.thewinnt.planimetry.ui.properties;

import net.thewinnt.planimetry.shapes.point.PointProvider;
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
}
