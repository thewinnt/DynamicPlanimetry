package net.thewinnt.planimetry.data;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.math.Vec2;

public class NbtUtil {
    public static final byte TRUE = 1;
    public static final byte FALSE = 0;

    public static void writeVec2(CompoundTag nbt, Vec2 vec2, String name) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("x", vec2.x);
        tag.putDouble("y", vec2.y);
        nbt.put(name, tag);
    }

    public static Vec2 readVec2(CompoundTag nbt, String name) {
        CompoundTag tag = nbt.getCompoundTag(name);
        double x = tag.getDouble("x");
        double y = tag.getDouble("y");
        return new Vec2(x, y);
    }

    public static double getOptionalDouble(CompoundTag tag, String name, double fallback) {
        if (!tag.containsKey(name)) return fallback;
        return tag.getDouble(name);
    }

    public static int getOptionalInt(CompoundTag tag, String name, int fallback) {
        if (!tag.containsKey(name)) return fallback;
        return tag.getInt(name);
    }

    public static byte getOptionalByte(CompoundTag tag, String name, byte fallback) {
        if (!tag.containsKey(name)) return fallback;
        return tag.getByte(name);
    }

    public static String getOptionalString(CompoundTag tag, String name, String fallback) {
        if (!tag.containsKey(name)) return fallback;
        return tag.getString(name);
    }

    public static boolean getOptionalBoolean(CompoundTag tag, String name, boolean fallback) {
        if (!tag.containsKey(name)) return fallback;
        return tag.getByte(name) != 0;
    }

    public static boolean getBoolean(CompoundTag tag, String name) {
        return tag.getByte(name) != 0;
    }

    public static void writeBoolean(CompoundTag tag, String name, boolean value) {
        tag.putByte(name, value ? TRUE : FALSE);
    }
}
