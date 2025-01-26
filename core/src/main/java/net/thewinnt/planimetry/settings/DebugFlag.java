package net.thewinnt.planimetry.settings;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.properties.types.BooleanProperty;
import net.thewinnt.planimetry.ui.text.Component;

import java.util.EnumMap;
import java.util.Locale;

public enum DebugFlag {
    DEBUG_ANGLE,
    SHOW_FPS,
    MOUSE_INFO,
    SELECTION_INFO,
    NOTIFICATION_DEBUG;

    private static final EnumMap<DebugFlag, BooleanProperty> PROPERTIES = new EnumMap<>(DebugFlag.class);

    public static BooleanProperty getOrCreateFlag(DebugFlag flag) {
        return PROPERTIES.computeIfAbsent(flag, f -> new BooleanProperty(Component.translatable("settings.debug." + f.name().toLowerCase(Locale.ROOT))));
    }

    public boolean get() {
        return DynamicPlanimetry.isDebug() && getOrCreateFlag(this).getValue();
    }

    public void set(boolean value) {
        getOrCreateFlag(this).setValue(value);
    }
}
