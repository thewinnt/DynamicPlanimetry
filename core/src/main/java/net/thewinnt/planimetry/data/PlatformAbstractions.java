package net.thewinnt.planimetry.data;

import java.io.File;

public interface PlatformAbstractions {
    File getSettingsFile();
    boolean forceShowDebug();
    boolean canOpenDrawingFolder();
    void openDrawingFolder();
}
