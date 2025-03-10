package net.thewinnt.planimetry.data.update;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.Notifications;

public class DataUpdater {
    public static CompoundTag updateDrawing(CompoundTag input) {
        int version = input.getInt("data_version");
        // if (version != DynamicPlanimetry.DATA_VERSION) {
        //     Notifications.addNotification("Skipping drawing with incompatible version: " + version, 2000);
        //     return null;
        // }
        // TODO actual data upgrade
        // TODO error checking
        return input;
    }
}
