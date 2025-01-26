package net.thewinnt.planimetry.data.registry;

import java.util.HashMap;
import java.util.Map;

import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.Components;
import net.thewinnt.planimetry.value.DynamicValueType;

public class Registries {
    private static final MappedRegistry<Registry<?>> REGISTRY = new MappedRegistry<>(new Identifier("root"));
    private static final Map<Registry<?>, RegistryBootstrap> BOOTSTRAPS = new HashMap<>();
    public static final Registry<Registry<?>> ROOT = REGISTRY;
    public static final Registry<Shape.ShapeDeserializer<?>> SHAPE_TYPE = registerMapped(new Identifier("shape_type"), t -> ShapeData.init());
    public static final Registry<DynamicValueType<?>> DYNAMIC_VALUE_TYPES = registerMapped(new Identifier("dynamic_value"), t -> DynamicValueType.init());
    public static final Registry<Component.ComponentDeserializer<?>> COMPONENT_TYPE = registerMapped(new Identifier("component_type"), t -> Components.init());

    public static <T> Registry<T> registerMapped(Identifier id, RegistryBootstrap bootstrap) {
        return registerRegistry(new MappedRegistry<>(id), id, bootstrap);
    }

    public static <T> Registry<T> registerDefaulted(Identifier id, T defaultElement, RegistryBootstrap bootstrap) {
        return registerRegistry(new DefaultedMappedRegistry<>(id, defaultElement), id, bootstrap);
    }

    @SuppressWarnings("unchecked")
    public static <T> Registry<T> registerRegistry(Registry<T> registry, Identifier id, RegistryBootstrap bootstrap) {
        BOOTSTRAPS.put(registry, bootstrap);
        return (Registry<T>) REGISTRY.register(id, registry);
    }

    public static void init() {
        for (Map.Entry<Registry<?>, RegistryBootstrap> entry : BOOTSTRAPS.entrySet()) {
            entry.getValue().accept(entry.getKey());
            ((MutableRegistry<?>) entry.getKey()).freeze();
        }
    }

    public interface RegistryBootstrap {
        void accept(Registry<?> registry);
    }
}
