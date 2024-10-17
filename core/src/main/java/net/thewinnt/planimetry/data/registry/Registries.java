package net.thewinnt.planimetry.data.registry;

import java.util.HashMap;
import java.util.Map;

import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.shapes.Shape;

public class Registries {
    private static final MappedRegistry<Registry<?>> REGISTRY = new MappedRegistry<>();
    private static final Map<Registry<?>, RegistryBootstrap<?>> BOOTSTRAPS = new HashMap<>();
    public static final Registry<Registry<?>> ROOT = REGISTRY;
    public static final Registry<Shape.ShapeDeserializer<?>> SHAPE_DESERIALIZER = registerMapped(new Identifier("shape_deserializer"), t -> ShapeData.init());

    public static <T> Registry<T> registerMapped(Identifier id, RegistryBootstrap<T> bootstrap) {
        return registerRegistry(new MappedRegistry<>(), id, bootstrap);
    }

    public static <T> Registry<T> registerDefaulted(Identifier id, T defaultElement, RegistryBootstrap<T> bootstrap) {
        return registerRegistry(new DefaultedMappedRegistry<>(defaultElement), id, bootstrap);
    }

    @SuppressWarnings("unchecked")
    public static <T> Registry<T> registerRegistry(Registry<T> registry, Identifier id, RegistryBootstrap<T> bootstrap) {
        BOOTSTRAPS.put(registry, bootstrap);
        return (Registry<T>) REGISTRY.register(id, registry);
    }

    public interface RegistryBootstrap<T> {
        void accept(Registry<T> registry);
    }
}
