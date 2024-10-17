package net.thewinnt.planimetry.platform;

import java.io.File;

public interface PlatformAbstractions {
    File getSettingsFile();
    boolean forceShowDebug();
    boolean canOpenDrawingFolder();
    void openDrawingFolder();
    boolean isSmallScreen();
}
