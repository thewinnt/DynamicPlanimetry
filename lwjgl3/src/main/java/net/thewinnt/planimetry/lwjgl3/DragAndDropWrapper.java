package net.thewinnt.planimetry.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class DragAndDropWrapper extends Lwjgl3WindowAdapter {
    private Consumer<List<File>> listener;

    @Override
    public void filesDropped(String[] files) {
        if (listener != null) {
            List<File> input = Arrays.stream(files).map(File::new).toList();
            listener.accept(input);
        }
    }

    public void setListener(Consumer<List<File>> listener) {
        this.listener = listener;
    }
}
