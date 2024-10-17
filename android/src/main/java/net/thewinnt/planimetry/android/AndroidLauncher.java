package net.thewinnt.planimetry.android;

import android.content.Context;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.Notifications;

import java.io.File;
import java.io.IOException;

import dev.dewy.nbt.Nbt;
import dev.dewy.nbt.tags.collection.CompoundTag;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true; // Recommended, but not required.

        AndroidPlatform platform = new AndroidPlatform(this.getBaseContext());
        File settings = platform.getSettingsFile();
        CompoundTag nbt = null;
        if (settings.exists() && settings.canRead()) {
            try {
                nbt = new Nbt().fromFile(settings);
            } catch (IOException e) {
                Notifications.addNotification("Couldn't load settings: " + e.getMessage(), 5000);
                e.printStackTrace();
            }
        }

        // TODO android IO
        initialize(new DynamicPlanimetry(nbt, platform, new AndroidIO(), true), configuration);
    }
}
