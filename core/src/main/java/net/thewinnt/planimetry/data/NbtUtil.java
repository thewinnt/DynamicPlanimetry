package net.thewinnt.planimetry.data;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.math.Vec2;

public class NbtUtil {
    public static void writeVec2(CompoundTag nbt, Vec2 vec2, String name) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("x", vec2.x);
        tag.putDouble("y", vec2.y);
        nbt.put(name, tag);
    }

    public static Vec2 readVec2(CompoundTag nbt, String name) {
        CompoundTag tag = nbt.getCompound(name);
        double x = tag.getDouble("x").getValue();
        double y = tag.getDouble("y").getValue();
        return new Vec2(x, y);
    }
}
