package net.thewinnt.planimetry.lwjgl3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.nfd.NFDFilterItem;
import org.lwjgl.util.nfd.NFDPathSetEnum;
import org.lwjgl.util.nfd.NativeFileDialog;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.platform.NativeIO;
import net.thewinnt.planimetry.ui.Notifications;

public class DesktopIO implements NativeIO {
    private final String workDir;
    private final DragAndDropWrapper dragAndDrop;

    public DesktopIO(DragAndDropWrapper dragAndDrop) {
        this.dragAndDrop = dragAndDrop;
        this.workDir = System.getProperty("user.dir") + "/";
    }

    @Override
    public File dataFile(String filename) {
        return Path.of(workDir, filename).toFile();
    }

    @Override
    public File[] listFiles(String dir) {
        return Path.of(workDir, dir).toFile().listFiles();
    }

    @Override
    public void suggestSave(Consumer<FileOutputStream> consumer) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            NFDFilterItem.Buffer filters = NFDFilterItem.malloc(1);
            filters.get(0)
                   .name(stack.UTF8(DynamicPlanimetry.translate("file.type")))
                   .spec(stack.UTF8("dpd"));

            PointerBuffer path = stack.mallocPointer(1);

            int result = NativeFileDialog.NFD_SaveDialog(path, filters, null, "");

            switch (result) {
                case NativeFileDialog.NFD_OKAY:
                    File output = new File(path.getStringUTF8(0));
                    NativeFileDialog.NFD_FreePath(path.get(0));
                    try (FileOutputStream stream = new FileOutputStream(output)) {
                        consumer.accept(stream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case NativeFileDialog.NFD_ERROR:
                    Notifications.addNotification(DynamicPlanimetry.translate("error.file_open", NativeFileDialog.NFD_GetError()), 7500);
            }
        }
    }

    @Override
    public void suggestOpen(Consumer<List<File>> consumer) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            NFDFilterItem.Buffer filters = NFDFilterItem.malloc(2);
            filters.get(0)
                   .name(stack.UTF8(DynamicPlanimetry.translate("file.type")))
                   .spec(stack.UTF8("dpd"));
            filters.get(1)
                .name(stack.UTF8(DynamicPlanimetry.translate("file.type")))
                .spec(stack.UTF8("nbt"));

            PointerBuffer path = stack.mallocPointer(1);

            int result = NativeFileDialog.NFD_OpenDialogMultiple(path, filters, (ByteBuffer) null);

            switch (result) {
                case NativeFileDialog.NFD_OKAY:
                    long pathSet = path.get(0);
                    NFDPathSetEnum pathSetEnum = NFDPathSetEnum.calloc(stack);
                    NativeFileDialog.NFD_PathSet_GetEnum(pathSet, pathSetEnum);

                    ArrayList<File> output = new ArrayList<>();
                    while (NativeFileDialog.NFD_PathSet_EnumNext(pathSetEnum, path) == NativeFileDialog.NFD_OKAY && path.get(0) != MemoryUtil.NULL) {
                        output.add(new File(path.getStringUTF8(0)));
                        NativeFileDialog.NFD_PathSet_FreePath(path.get(0));
                    }
                    NativeFileDialog.NFD_PathSet_FreeEnum(pathSetEnum);
                    NativeFileDialog.NFD_PathSet_Free(pathSet);
                    consumer.accept(output);
                    break;
                case NativeFileDialog.NFD_ERROR:
                    Notifications.addNotification(DynamicPlanimetry.translate("error.file_open", NativeFileDialog.NFD_GetError()), 7500);
            }
        }
    }

    @Override
    public void allowDragAndDrop(Consumer<List<File>> listener) {
        this.dragAndDrop.setListener(listener);
    }

    @Override
    public void removeDragAndDrop() {
        this.dragAndDrop.setListener(null);
    }

    @Override
    public void deleteFile(File file) {
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            Notifications.addNotification("Error deleting file: " + e.getMessage(), 5000);
            e.printStackTrace();
        }
    }
}
