package net.thewinnt.planimetry.lwjgl3;

import java.io.File;
import java.io.IOException;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import dev.dewy.nbt.Nbt;
import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.Notifications;
import org.lwjgl.glfw.GLFWDropCallback;

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
                nbt = new Nbt().fromFile(settingsFile);
            } catch (IOException e) {
                Notifications.addNotification("Couldn't load settings: " + e.getMessage(), 5000);
                e.printStackTrace();
            }
        }

        createApplication(nbt, debug);
    }

    private static Lwjgl3Application createApplication(CompoundTag settings, boolean debug) {
        DragAndDropWrapper dragAndDrop = new DragAndDropWrapper();
        return new Lwjgl3Application(new DynamicPlanimetry(settings, new DesktopPlatform(), new DesktopIO(dragAndDrop), debug), getDefaultConfiguration(dragAndDrop));
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration(DragAndDropWrapper dragAndDrop) {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Dynamic Planimetry");
        configuration.useVsync(true);
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
