package net.thewinnt.planimetry.data.update;

import dev.dewy.nbt.tags.collection.CompoundTag;

public interface Updater {
    CompoundTag update(CompoundTag data);
}
