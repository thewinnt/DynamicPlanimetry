package net.thewinnt.planimetry.platform;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.function.Consumer;

public interface NativeIO {
    File dataFile(String filename);
    File[] listFiles(String dir);
    void suggestSave(Consumer<FileOutputStream> onFileAvailable);
    void suggestOpen(Consumer<List<File>> onOpen);
    void allowDragAndDrop(Consumer<List<File>> listener);
    void removeDragAndDrop();
    void deleteFile(File file);
}
