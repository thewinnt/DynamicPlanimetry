package net.thewinnt.planimetry.android;

import android.content.Context;

import net.thewinnt.planimetry.platform.PlatformAbstractions;

import java.io.File;

public class AndroidPlatform implements PlatformAbstractions {
    private final Context context;

    public AndroidPlatform(Context context) {
        this.context = context;
    }

    @Override
    public File getSettingsFile() {
        return new File(context.getFilesDir(), "settings.nbt");
    }

    @Override
    public boolean forceShowDebug() {
        return true;
    }

    @Override
    public boolean canOpenDrawingFolder() {
        return false;
    }

    @Override
    public void openDrawingFolder() {}

    @Override
    public boolean isSmallScreen() {
        return true;
    }
}
