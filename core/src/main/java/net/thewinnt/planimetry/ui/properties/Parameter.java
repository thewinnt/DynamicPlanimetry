package net.thewinnt.planimetry.ui.properties;

import java.util.function.Consumer;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.thewinnt.planimetry.ui.StyleSet;

@Deprecated(forRemoval = true)
public interface Parameter<T> {
    public abstract Table getActorSetup(StyleSet styles);
    public abstract T getValue();
    public abstract void addValueChangeListener(Consumer<T> listener);
}
