package net.thewinnt.planimetry.lwjgl3;

import java.io.File;
import java.io.IOException;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.data.NbtUtil;
import net.thewinnt.planimetry.settings.AntialiasingType;
import net.thewinnt.planimetry.ui.Notifications;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.

        boolean debug = false;
        for (String i : args) {
            if (i.equals("--debug")) {
                debug = true;
                break;
            }
        }

        // load settings (it's easier on native platforms)
        File settingsFile = new File("./settings.dat");
        CompoundTag nbt = null;
        if (settingsFile.exists() && settingsFile.canRead()) {
            try {
                nbt = ((CompoundTag) NBTUtil.read(settingsFile).getTag());
            } catch (IOException e) {
                Notifications.addNotification("Couldn't load settings: " + e.getMessage(), 5000);
                e.printStackTrace();
            }
        }

        createApplication(nbt, debug);
    }

    private static Lwjgl3Application createApplication(CompoundTag settings, boolean debug) {
        DragAndDropWrapper dragAndDrop = new DragAndDropWrapper();
        byte samples = NbtUtil.getOptionalByte(settings, "antialiasing", (byte)4);
        return new Lwjgl3Application(new DynamicPlanimetry(settings, new DesktopPlatform(), new DesktopIO(dragAndDrop), debug), getDefaultConfiguration(dragAndDrop, samples));
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration(DragAndDropWrapper dragAndDrop, int msaaSamples) {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Dynamic Planimetry");
        configuration.useVsync(false);
        configuration.setBackBufferConfig(8, 8, 8, 8, 16, 0, msaaSamples); // TODO make this a settings
        //// Limits FPS to the refresh rate of the currently active monitor.
        // configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.
        configuration.setWindowedMode(1280, 720);
        configuration.setWindowIcon("logo.png");
        configuration.setWindowListener(dragAndDrop);
        return configuration;
    }
}
