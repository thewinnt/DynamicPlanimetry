package net.thewinnt.planimetry.ui.text;

import net.thewinnt.planimetry.ui.text.Component.ComponentDeserializer;
import net.thewinnt.planimetry.ui.text.Component.Empty;
import net.thewinnt.planimetry.util.HashBiMap;

public class ComponentRegistry {
    private static final HashBiMap<String, ComponentDeserializer<?>> REGISTRY = new HashBiMap<>();

    public static final ComponentDeserializer<Empty> EMPTY = register("empty", Empty::readNbt);
    public static final ComponentDeserializer<LiteralComponent> LITERAL = register("literal", LiteralComponent::readNbt);
    public static final ComponentDeserializer<NameComponent> NAME = register("name", NameComponent::readNbt);
    public static final ComponentDeserializer<MultiComponent> MULTIPLE = register("multiple", MultiComponent::readNbt);
    
    public static <T extends Component> ComponentDeserializer<T> register(String name, ComponentDeserializer<T> deserializer) {
        REGISTRY.put(name, deserializer);
        return deserializer;
    }

    public static ComponentDeserializer<?> getDeserializer(String name) {
        return REGISTRY.get(name);
    }

    public static String getComponentType(ComponentDeserializer<?> deserializer) {
        return REGISTRY.getKey(deserializer);
    }
}
