package net.thewinnt.planimetry.ui.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.ui.StyleSet;

public abstract class Function<T extends Shape> {
    private final List<Consumer<T>> useListeners = new ArrayList<>();
    protected final Drawing drawing;
    protected final T shape;

    public Function(Drawing drawing, T shape) {
        this.drawing = drawing;
        this.shape = shape;
    }

    public void addUseListener(Consumer<T> listener) {
        useListeners.add(listener);
    }

    protected final void use() {
        for (Consumer<T> i : useListeners) {
            i.accept(shape);
        }
    }

    public abstract WidgetGroup setupActors(StyleSet styles);
}
