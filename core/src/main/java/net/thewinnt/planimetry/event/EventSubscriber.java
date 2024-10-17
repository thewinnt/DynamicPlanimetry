package net.thewinnt.planimetry.event;

@FunctionalInterface
public interface EventSubscriber<T extends Event> {
    void accept(T event);
}
