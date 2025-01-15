package net.thewinnt.planimetry.data.update;

import net.querz.nbt.tag.CompoundTag;

public interface Updater {
    CompoundTag update(CompoundTag data);
}
