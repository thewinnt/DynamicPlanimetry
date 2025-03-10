package net.thewinnt.planimetry.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.shapes.Shape;

public class SavingContext {
    private final Map<Long, CompoundTag> saveData = new HashMap<>();
    private final Collection<Shape> initialShapes;

    public SavingContext(Collection<Shape> participants) {
        initialShapes = participants;
    }

    public long addShape(Shape shape) {
        if (!saveData.containsKey(shape.getId())) {
            saveData.put(shape.getId(), shape.toNbt(this));
        }
        return shape.getId();
    }

    public Collection<CompoundTag> save() {
        for (Shape i : initialShapes) {
            saveData.put(i.getId(), i.toNbt(this));
        }
        return saveData.values();
    }
}
