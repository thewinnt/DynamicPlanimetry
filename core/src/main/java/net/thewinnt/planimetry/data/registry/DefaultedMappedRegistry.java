package net.thewinnt.planimetry.data.registry;

public class DefaultedMappedRegistry<T> extends MappedRegistry<T> {
    private final T defaultElement;

    public DefaultedMappedRegistry(T defaultElement) {
        this.defaultElement = defaultElement;
    }

    @Override
    public T byName(Identifier id) {
        return this.elementByName.getOrDefault(id, defaultElement);
    }

    @Override
    public T byId(int id) {
        return this.elementById.getOrDefault(id, defaultElement);
    }
}
