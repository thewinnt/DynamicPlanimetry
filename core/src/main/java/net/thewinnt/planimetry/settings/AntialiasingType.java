package net.thewinnt.planimetry.settings;

import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.ComponentRepresentable;

public enum AntialiasingType implements ComponentRepresentable {
    NONE(Component.translatable("settings.antialiasing.none"), 0),
    MSAA_2X(Component.translatable("settings.antialiasing.2x"), 2),
    MSAA_4X(Component.translatable("settings.antialiasing.4x"), 4),
    MSAA_8X(Component.translatable("settings.antialiasing.8x"), 8),
    MSAA_16X(Component.translatable("settings.antialiasing.16x"), 16);

    private final Component name;
    public final byte samples;

    AntialiasingType(Component name, int samples) {
        this.name = name;
        this.samples = (byte) samples;
    }

    public static AntialiasingType valueOf(byte samples) {
        return switch (samples) {
            case 0 -> NONE;
            case 2 -> MSAA_2X;
            case 8 -> MSAA_8X;
            case 16 -> MSAA_16X;
            default -> MSAA_4X;
        };
    }

    @Override
    public Component toComponent() {
        return name;
    }
}
