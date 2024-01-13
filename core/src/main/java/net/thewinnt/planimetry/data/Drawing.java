package net.thewinnt.planimetry.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import dev.dewy.nbt.io.CompressionType;
import dev.dewy.nbt.tags.collection.CompoundTag;
import dev.dewy.nbt.tags.collection.ListTag;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.shapes.point.PointReference;
import net.thewinnt.planimetry.ui.Notifications;
import net.thewinnt.planimetry.ui.text.NameComponent;
import net.thewinnt.planimetry.util.HashBiMap;

public class Drawing {
    public final List<BiConsumer<Shape, Shape>> swapListeners = new ArrayList<>();
    public final HashBiMap<Long, Shape> shapeIds = new HashBiMap<>();
    public final List<Shape> shapes;
    public final List<PointProvider> points;
    private long shapeIdCounter;
    private int pointNameCounter;
    private long minIdUponLoad;
    private long maxIdUponLoad;
    private String filename;
    private boolean isFileAbsolute;
    private String name;
    private boolean useDashesForNaming;
    private long creationTime;
    private long lastEditTime;
    private boolean changed = false;
    boolean isLoading = false;

    public Drawing() {
        this.shapes = new ArrayList<>();
        this.points = new ArrayList<>();
        this.name = "Безымянный";
        this.creationTime = System.currentTimeMillis();
        this.lastEditTime = creationTime;
    }

    public Drawing(Collection<Shape> shapes, Collection<PointProvider> points, String name, long creationTime, long lastEditTime) {
        this.shapes = new ArrayList<>(shapes);
        this.points = new ArrayList<>(points);
        this.name = name;
        this.creationTime = creationTime;
        this.lastEditTime = lastEditTime;
        for (Shape i : shapes) {
            updateIdRange(i.getId());
        }
        for (PointProvider i : points) {
            updateIdRange(i.getId());
        }
        if (maxIdUponLoad - minIdUponLoad < 1000000) {
            shapeIdCounter = maxIdUponLoad + 1;
        }
    }

    public Drawing(Collection<Shape> shapes, String name, long creationTime, long lastEditTime) {
        this.shapes = new ArrayList<>();
        this.points = new ArrayList<>();
        this.name = name;
        this.creationTime = creationTime;
        this.lastEditTime = lastEditTime;
        for (Shape i : shapes) {
            this.addShapeQuick(i);
        }
        if (maxIdUponLoad - minIdUponLoad < 1000000) {
            shapeIdCounter = maxIdUponLoad + 1;
        }
    }
    
    public void addShape(Shape shape) {
        update();
        if (shape instanceof PointProvider point) {
            if (!this.points.contains(point)) {
                this.points.add(point);
            }
        } else if (!this.shapes.contains(shape)) {
            this.shapes.add(shape);
        }
        this.shapeIds.put(shape.getId(), shape);
    }

    private void addShapeQuick(Shape shape) {
        if (shape instanceof PointProvider point) {
            this.points.add(point);
        } else {
            this.shapes.add(shape);
        }
        this.shapeIds.put(shape.getId(), shape);
        updateIdRange(shape.getId());
    }

    public boolean hasShape(Shape shape) {
        for (Shape i : this.shapes) {
            if (i == shape) {
                return true;
            }
        }
        if (shape instanceof PointProvider) {
            for (Shape i : this.points) {
                if (i instanceof PointReference point && point.getPoint() == shape) {
                    return true;
                } else if (i == shape) {
                    return true;
                }
            }
        }
        return false;
    }

    public void replaceShape(Shape old, Shape neo) {
        update();
        this.shapes.set(this.shapes.indexOf(old), neo);
        this.swapListeners.forEach(listener -> listener.accept(old, neo));
    }

    public void removeShape(Shape shape) {
        update();
        this.shapes.remove(shape);
        this.points.remove(shape);
    }

    public long getId(Shape shape) {
        if (isLoading) return 0;
        if (this.shapeIds.containsValue(shape)) {
            return this.shapeIds.getKey(shape);
        } else {
            // this is the first time ever i use the do-while loop
            do {
                shapeIdCounter++;
            } while (this.shapeIds.containsKey(shapeIdCounter));
            return shapeIdCounter;
        }
    }

    public NameComponent generateName(boolean useDashesNotDigits) {
        if (useDashesNotDigits) {
            return new NameComponent((byte)(pointNameCounter % 26), 0, (short)(pointNameCounter++ / 26));
        } else {
            return new NameComponent((byte)(pointNameCounter % 26), pointNameCounter++ / 26, (short)0);
        }
    }

    private void updateIdRange(long newId) {
        if (newId < minIdUponLoad) {
            minIdUponLoad = newId;
        } else if (newId > maxIdUponLoad) {
            maxIdUponLoad = newId;
        }
    }

    public void update() {
        lastEditTime = System.currentTimeMillis();
        changed = true;
    }

    public boolean hasChanged() {
        return changed;
    }

    public Drawing withFilename(String filename, boolean isAbsolute) {
        this.filename = filename;
        this.isFileAbsolute = isAbsolute;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean shouldUseDashesForNaming() {
        return useDashesForNaming;
    }

    public void setUseDashesForNaming(boolean useDashesForNaming) {
        this.useDashesForNaming = useDashesForNaming;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getLastEditTime() {
        return lastEditTime;
    }

    public String getFilename() {
        if (filename == null) {
            filename = "autosave " + DynamicPlanimetry.AUTOSAVE_DATE_FORMAT.format(new Date(System.currentTimeMillis()));
        }
        if (isFileAbsolute) {
            return filename;
        } else {
            return Gdx.files.local("drawings/" + filename + ".dpd").path();
        }
    }

    public void addSwapListener(BiConsumer<Shape, Shape> listener) {
        this.swapListeners.add(listener);
    }

    public void clearSwapListeners() {
        this.swapListeners.clear();
    }

    public CompoundTag toNbt() {
        CompoundTag output = new CompoundTag();
        ListTag<CompoundTag> shapes = new ListTag<>();
        SavingContext context = new SavingContext(Stream.concat(this.shapes.stream(), this.points.stream()).toList());
        shapes = new ListTag<>("shapes", new ArrayList<>(context.save()));
        output.put("shapes", shapes);
        output.putString("name", name);
        output.putLong("creation_time", creationTime);
        output.putLong("last_edit_time", lastEditTime);
        return output;
    }
    
    public static Drawing fromNbt(CompoundTag nbt) {
        String name = nbt.getString("name").getValue();
        long creationTime = nbt.getLong("creation_time").getValue();
        long lastEditTime = nbt.getLong("last_edit_time").getValue();
        ListTag<CompoundTag> shapes = nbt.getList("shapes");
        LoadingContext context = new LoadingContext(shapes);
        Drawing output = context.getDrawing();
        output.setName(name);
        output.creationTime = creationTime;
        output.lastEditTime = lastEditTime;
        for (Shape i : context.load()) {
            output.addShapeQuick(i);
        }
        output.isLoading = false;
        return output;
    }

    public void save(String filename, boolean isAbsolute) {
        this.withFilename(filename, isAbsolute).save();
    }

    public void save() {
        CompoundTag nbt = this.toNbt();
        FileHandle file;
        if (filename == null) {
            filename = "autosave " + DynamicPlanimetry.AUTOSAVE_DATE_FORMAT.format(new Date(System.currentTimeMillis()));
        }
        if (isFileAbsolute) {
            file = Gdx.files.absolute(filename);
        } else {
            file = Gdx.files.local("drawings/" + filename + ".dpd");
        }
        try {
            DynamicPlanimetry.NBT.toFile(nbt, file.file(), CompressionType.GZIP);
        } catch (IOException e) {
            Notifications.addNotification("Error saving file: " + e.getMessage(), 5000);
        }
    }

    public static Drawing load(String filenameAbsolute) {
        try {
            CompoundTag nbt = DynamicPlanimetry.NBT.fromFile(new File(filenameAbsolute));
            return Drawing.fromNbt(nbt).withFilename(filenameAbsolute, true);
        } catch (IOException e) {
            Notifications.addNotification("Error loading file: " + e.getMessage(), 5000);
            return null;
        }
    }
}
