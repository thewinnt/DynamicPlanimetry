
package net.thewinnt.planimetry.android;

import android.content.Context;
import android.content.Intent;

import net.thewinnt.planimetry.platform.NativeIO;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class AndroidIO implements NativeIO {
    private final AndroidLauncher activity;
    private final Context context;

    public AndroidIO(AndroidLauncher activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    // TODO android io
    @Override
    public File dataFile(String filename) {
        return new File(context.getFilesDir(), filename);
    }

    @Override
    public File[] listFiles(String dir) {
        File[] output = dataFile(dir).listFiles();
        return output == null ? new File[0] : output;
    }

    @Override
    public void suggestSave(Consumer<FileOutputStream> onFileAvailable) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        activity.startActivityForResult(intent, 1);
        activity.addFileOutput(onFileAvailable);
    }

    @Override
    public void suggestOpen(Consumer<List<File>> onOpen) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        activity.startActivityForResult(intent, 2);
    }

    @Override
    public void allowDragAndDrop(Consumer<List<File>> listener) {} // there's no drag and drop on mobile

    @Override
    public void removeDragAndDrop() {}

    @Override
    public void deleteFile(File file) {
        file.delete();
    }
}
