package net.thewinnt.planimetry.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Null;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.platform.NativeIO;
import net.thewinnt.planimetry.ui.Notifications;

import javax.swing.*;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.nfd.NFDFilterItem;
import org.lwjgl.util.nfd.NFDPathSetEnum;
import org.lwjgl.util.nfd.NativeFileDialog;

import java.io.File;
import java.lang.management.MemoryUsage;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

    @Null
    @Override
    public File suggestSave() {
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
                    // TODO see https://github.com/LWJGL/lwjgl3/blob/3.3.3/modules/samples/src/test/java/org/lwjgl/demo/util/nfd/HelloNFD.java
                    return output;
                case NativeFileDialog.NFD_ERROR:
                    Notifications.addNotification(DynamicPlanimetry.translate("error.file_open", NativeFileDialog.NFD_GetError()), 7500);
                default:
                    return null;
            }
        }
    }

    @Override
    public List<File> suggestOpen() {
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
                    return output;
                case NativeFileDialog.NFD_ERROR:
                    Notifications.addNotification(DynamicPlanimetry.translate("error.file_open", NativeFileDialog.NFD_GetError()), 7500);
                default:
                    return null;
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
}
