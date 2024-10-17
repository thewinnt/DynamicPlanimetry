package net.thewinnt.planimetry.android;

import net.thewinnt.planimetry.platform.NativeIO;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class AndroidIO implements NativeIO {
    // TODO android io
    @Override
    public File dataFile(String filename) {
        return null;
    }

    @Override
    public File[] listFiles(String dir) {
        return new File[0];
    }

    @Override
    public File suggestSave() {
        return null;
    }

    @Override
    public List<File> suggestOpen() {
        return Collections.emptyList();
    }

    @Override
    public void allowDragAndDrop(Consumer<List<File>> listener) {

    }

    @Override
    public void removeDragAndDrop() {

    }
}
