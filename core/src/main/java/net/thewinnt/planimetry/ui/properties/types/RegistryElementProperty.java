package net.thewinnt.planimetry.ui.properties.types;

import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.thewinnt.planimetry.data.registry.Registry;
import net.thewinnt.planimetry.data.registry.TagKey;
import net.thewinnt.planimetry.ui.text.Component;

public class RegistryElementProperty<T> extends SelectionProperty<T> {
    public RegistryElementProperty(T selected, Component name, Registry<T> registry) {
        super(selected, name, registry.stream().collect(Collectors.toList()));
        this.setComponentProvider(t -> t == null ? Component.translatable("null") : Component.translatable(registry.getName(t).toLanguageKey(registry.id().path).toString()));
    }

    public RegistryElementProperty(T selected, Component name, Registry<T> registry, Predicate<T> filter) {
        super(selected, name, registry.stream().filter(filter).collect(Collectors.toList()));
        this.setComponentProvider(t -> t == null ? Component.translatable("null") : Component.translatable(registry.getName(t).toLanguageKey(registry.id().path).toString()));
    }

    public RegistryElementProperty(T selected, Component name, Registry<T> registry, TagKey<T> tag) {
        this(selected, name, registry, t -> registry.wrapAsHolder(t).is(tag));
    }
}
