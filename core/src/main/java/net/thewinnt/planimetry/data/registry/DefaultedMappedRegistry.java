package net.thewinnt.planimetry.data.registry;

import java.util.function.Supplier;

public class DefaultedMappedRegistry<T> extends MappedRegistry<T> {
    private final Supplier<T> defaultElement;

    public DefaultedMappedRegistry(Identifier id, Supplier<T> defaultElement) {
        super(id);
        this.defaultElement = defaultElement;
    }

    @Override
    public T get(Identifier id) {
        return this.elementByName.getOrDefault(id, this.wrapAsHolder(defaultElement.get())).value();
    }

    @Override
    public T get(int id) {
        return this.elementById.getOrDefault(id, this.wrapAsHolder(defaultElement.get())).value();
    }
}
