package net.thewinnt.planimetry.lwjgl3;

import com.badlogic.gdx.Gdx;

import net.thewinnt.planimetry.data.PlatformAbstractions;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class DesktopPlatform implements PlatformAbstractions {
    private final File settings = new File("./settings.dat");

    @Override
    public File getSettingsFile() {
        return settings;
    }

    @Override
    public boolean forceShowDebug() {
        return false;
    }

    @Override
    public boolean canOpenDrawingFolder() {
        return true;
    }

    @Override
    public void openDrawingFolder() {
        try {
            Desktop.getDesktop().open(new File(Gdx.files.getLocalStoragePath() + "drawings"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
