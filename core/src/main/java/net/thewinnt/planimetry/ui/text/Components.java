package net.thewinnt.planimetry.ui.text;

import net.thewinnt.planimetry.data.registry.Identifier;
import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.data.registry.Registry;
import net.thewinnt.planimetry.ui.text.Component.ComponentDeserializer;
import net.thewinnt.planimetry.ui.text.Component.Empty;

public class Components {
    public static final ComponentDeserializer<Empty> EMPTY = register("empty", Empty::readNbt);
    public static final ComponentDeserializer<LiteralComponent> LITERAL = register("literal", LiteralComponent::readNbt);
    public static final ComponentDeserializer<SimpleTranslatableComponent> SIMPLE_TRANSLATABLE = register("simple_translatable", SimpleTranslatableComponent::readNbt);
    public static final ComponentDeserializer<ComplexTranslatableComponent> COMPLEX_TRANSLATABLE = register("complex_translatable", ComplexTranslatableComponent::readNbt);
    public static final ComponentDeserializer<NameComponent> NAME = register("name", NameComponent::readNbt);
    public static final ComponentDeserializer<MultiComponent> MULTIPLE = register("multiple", MultiComponent::readNbt);

    public static <T extends Component> ComponentDeserializer<T> register(String name, ComponentDeserializer<T> deserializer) {
        return Registry.register(Registries.COMPONENT_TYPE, deserializer, name);
    }

    public static ComponentDeserializer<?> getDeserializer(String name) {
        return Registries.COMPONENT_TYPE.get(new Identifier(name));
    }

    public static Identifier getComponentType(ComponentDeserializer<?> deserializer) {
        return Registries.COMPONENT_TYPE.getName(deserializer);
    }

    public static void init() {}
}
