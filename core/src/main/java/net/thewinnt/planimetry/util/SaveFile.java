package net.thewinnt.planimetry.util;

import java.util.List;

import dev.dewy.nbt.tags.collection.CompoundTag;

public record SaveFile(String name, long creationTime, long lastEditTime, List<CompoundTag> shapes) {
    
}
