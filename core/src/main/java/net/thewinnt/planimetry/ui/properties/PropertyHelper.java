package net.thewinnt.planimetry.ui.properties;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.Settings;
import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.data.registry.Registry;
import net.thewinnt.planimetry.data.registry.TagKey;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.types.InlineTypeProperty;
import net.thewinnt.planimetry.ui.properties.types.NumberProperty;
import net.thewinnt.planimetry.ui.properties.types.PropertyGroup;
import net.thewinnt.planimetry.ui.properties.types.RegistryElementProperty;
import net.thewinnt.planimetry.ui.properties.types.ShapeProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.value.DynamicValue;
import net.thewinnt.planimetry.value.DynamicValueType;
import net.thewinnt.planimetry.value.type.AngleValue;
import net.thewinnt.planimetry.value.type.ConstantValue;

import java.util.Collection;
import java.util.function.Consumer;

public class PropertyHelper {
    public static Property<?> swappablePoint(PointProvider point, Consumer<PointProvider> setter, Collection<PointProvider> except, boolean includePointProperties, String nameKey, Object... textArgs) {
        ShapeProperty selector = new ShapeProperty(Component.translatable("shape.generic.point"), point.getDrawing(), point, s -> s instanceof PointProvider p && !except.contains(p));
        selector.addValueChangeListener(shape -> {
            setter.accept((PointProvider) shape);
            DynamicPlanimetry.getInstance().editorScreen.show();
        });
        if (includePointProperties) {
            point.rebuildProperties();
            PropertyGroup output = new PropertyGroup(Component.translatable(nameKey, textArgs));
            output.addProperty(selector);
            output.addProperties(point.getProperties());
            return output;
        } else {
            return selector;
        }
    }

    public static <E, T extends Property<E>> T setter(T property, Consumer<E> setter) {
        property.addValueChangeListener(setter);
        return property;
    }

    public static <E, T extends Property<E>> T refreshingSetter(T property, Consumer<E> setter) {
        property.addValueChangeListener(e -> {
            setter.accept(e);
            DynamicPlanimetry.getInstance().editorScreen.show();
        });
        return property;
    }

    public static NumberProperty angle(String nameKey, double currentRad, Consumer<Double> setterRad) {
        NumberProperty output = new NumberProperty(Component.translatable(nameKey), Settings.get().toUnit(currentRad));
        output.addValueChangeListener(unit -> setterRad.accept(Settings.get().toRadians(unit)));
        return output;
    }

    public static <T, E extends PropertySupplier> PropertyGroup collect(Registry<T> registry, T type, Consumer<T> setter, E element, String typeKey, String nameKey, Object... textArgs) {
        PropertyGroup output = new PropertyGroup(Component.translatable(nameKey, textArgs));
        output.addProperty(refreshingSetter(new RegistryElementProperty<>(type, Component.translatable(typeKey), registry), setter));
        output.addProperties(element.properties());
        return output;
    }

    public static <T, E extends PropertySupplier> PropertyGroup collect(Registry<T> registry, T type, TagKey<T> tag, Consumer<T> setter, E element, String typeKey, String nameKey, Object... textArgs) {
        PropertyGroup output = new PropertyGroup(Component.translatable(nameKey, textArgs));
        output.addProperty(refreshingSetter(new RegistryElementProperty<>(type, Component.translatable(typeKey), registry, tag), setter));
        output.addProperties(element.properties());
        return output;
    }

    public static Property<?> dynamicValue(DynamicValue input, Consumer<DynamicValue> setter, String key, Object... textArgs) {
        if (input instanceof ConstantValue || input instanceof AngleValue) {
            RegistryElementProperty<DynamicValueType<?>> property = new RegistryElementProperty<>(input.type(), Component.translatable("value.type"), Registries.DYNAMIC_VALUE_TYPE);
            property.addValueChangeListener(type -> {
                setter.accept(type.create(DynamicPlanimetry.getInstance().getDrawing()));
                DynamicPlanimetry.getInstance().editorScreen.show();
            });
            return new InlineTypeProperty(
                Component.translatable(key, textArgs),
                property,
                input.properties().stream().findAny().orElseThrow()
            );
        }
        return collect(Registries.DYNAMIC_VALUE_TYPE, input.type(), type -> setter.accept(type.create(DynamicPlanimetry.getInstance().getDrawing())), input, "value.type", key, textArgs);
    }
}
