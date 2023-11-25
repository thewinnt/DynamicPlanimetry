package net.thewinnt.planimetry.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.shapes.Shape;

public class SavingContext {
    private final Map<Long, CompoundTag> saveData = new HashMap<>();
    private final Collection<Shape> initialShapes;

    public SavingContext(Collection<Shape> participants) {
        initialShapes = participants;
    }

    public void addShape(Shape shape) {
        if (!saveData.containsKey(shape.getId())) {
            saveData.put(shape.getId(), shape.toNbt(this));
        }
    }

    public Collection<CompoundTag> save() {
        for (Shape i : initialShapes) {
            saveData.put(i.getId(), i.toNbt(this));
        }
        return saveData.values();
    }
}
