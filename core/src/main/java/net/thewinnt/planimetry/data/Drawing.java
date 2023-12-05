package net.thewinnt.planimetry.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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

public class Drawing {
    public final List<Shape> shapes;
    public final List<PointProvider> points;
    private String filename;
    private boolean isFileAbsolute;
    private String name;
    private long creationTime;
    private long lastEditTime;
    private boolean changed = false;

    public Drawing() {
        this.shapes = new ArrayList<>();
        this.points = new ArrayList<>();
        this.name = "Новый чертёж";
        this.creationTime = System.currentTimeMillis();
        this.lastEditTime = creationTime;
    }

    public Drawing(Collection<Shape> shapes, Collection<PointProvider> points, String name, long creationTime, long lastEditTime) {
        this.shapes = new ArrayList<>(shapes);
        this.points = new ArrayList<>(points);
        this.name = name;
        this.creationTime = creationTime;
        this.lastEditTime = lastEditTime;
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
    }

    private void addShapeQuick(Shape shape) {
        if (shape instanceof PointProvider point) {
            this.points.add(point);
        } else {
            this.shapes.add(shape);
        }
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
    }

    public void removeShape(Shape shape) {
        update();
        this.shapes.remove(shape);
        this.points.remove(shape);
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

    public long getCreationTime() {
        return creationTime;
    }

    public long getLastEditTime() {
        return lastEditTime;
    }

    public String getFilename() {
        if (isFileAbsolute) {
            return filename;
        } else {
            return Gdx.files.getLocalStoragePath() + filename + ".dpd";
        }
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
        return new Drawing(context.load(), name, creationTime, lastEditTime);
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
            CompoundTag nbt = DynamicPlanimetry.NBT.fromFile(Gdx.files.absolute(filenameAbsolute).file());
            return Drawing.fromNbt(nbt).withFilename(filenameAbsolute, true);
        } catch (IOException e) {
            Notifications.addNotification("Error loading file: " + e.getMessage(), 5000);
            return null;
        }
    }
}
