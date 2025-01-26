package net.thewinnt.planimetry.android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.Notifications;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    private final List<Consumer<List<File>>> fileInputs = new ArrayList<>();
    private final List<Consumer<FileOutputStream>> fileOutputs = new ArrayList<>();

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
                nbt = ((CompoundTag) NBTUtil.read(settings).getTag());
            } catch (IOException e) {
                Notifications.addNotification("Couldn't load settings: " + e.getMessage(), 5000);
                e.printStackTrace();
            }
        }

        initialize(new DynamicPlanimetry(nbt, platform, new AndroidIO(this, this.getBaseContext()), true), configuration);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                try {
                    ParcelFileDescriptor descriptor = descriptor = this.getContentResolver().openFileDescriptor(uri, "w");
                    FileOutputStream stream = new FileOutputStream(descriptor.getFileDescriptor());
                    fileOutputs.forEach(consumer -> consumer.accept(stream));
                    fileOutputs.clear();
                    stream.close();
                    descriptor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                File file = new File(uri.getPath());
                fileInputs.forEach(consumer -> consumer.accept(List.of(file)));
                fileInputs.clear();
            }
        }
    }

    public void addFileOutput(Consumer<FileOutputStream> output) {
        fileOutputs.add(output);
    }

    public void addFileInput(Consumer<List<File>> input) {
        fileInputs.add(input);
    }
}
