package net.thewinnt.planimetry.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.shapes.Shape;

public class LoadingContext {
    private final Drawing drawing;
    private final Map<Long, CompoundTag> saveData = new HashMap<>();
    private final Map<Long, Shape> resolvedShapes = new HashMap<>();

    public LoadingContext(Iterable<CompoundTag> saveData) {
        this.drawing = new Drawing();
        this.drawing.isLoading = true;
        for (CompoundTag i : saveData) {
            this.saveData.put(i.getLong("id"), i);
        }
    }

    @SuppressWarnings("unchecked") // the caller is going to cast anyway
    public <T extends Shape> T resolveShape(long id) {
        if (resolvedShapes.containsKey(id)) {
            return (T) resolvedShapes.get(id);
        } else {
            Shape shape = Shape.fromNbt(saveData.get(id), this);
            resolvedShapes.put(id, shape);
            return (T) shape;
        }
    }

    public Collection<Shape> load() {
        for (var i : saveData.entrySet()) {
            if (!resolvedShapes.containsKey(i.getKey())) {
                resolveShape(i.getKey());
            }
        }
        return resolvedShapes.values();
    }

    public Drawing getDrawing() {
        return drawing;
    }
}
