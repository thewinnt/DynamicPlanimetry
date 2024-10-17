package net.thewinnt.planimetry.platform;

import com.badlogic.gdx.utils.Null;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface NativeIO {
    File dataFile(String filename);
    File[] listFiles(String dir);
    @Null File suggestSave();
    @Null List<File> suggestOpen();
    void allowDragAndDrop(Consumer<List<File>> listener);
    void removeDragAndDrop();
}
