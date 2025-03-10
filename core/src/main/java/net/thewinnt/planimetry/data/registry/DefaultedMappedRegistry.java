package net.thewinnt.planimetry.data.registry;

public class DefaultedMappedRegistry<T> extends MappedRegistry<T> {
    private final T defaultElement;

    public DefaultedMappedRegistry(Identifier id, T defaultElement) {
        super(id);
        this.defaultElement = defaultElement;
    }

    @Override
    public T get(Identifier id) {
        return this.elementByName.getOrDefault(id, this.wrapAsHolder(defaultElement)).value();
    }

    @Override
    public T get(int id) {
        return this.elementById.getOrDefault(id, this.wrapAsHolder(defaultElement)).value();
    }
}
