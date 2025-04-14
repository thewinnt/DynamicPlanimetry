package net.thewinnt.planimetry.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.badlogic.gdx.Gdx;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.data.update.DataUpdater;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.Notifications;
import net.thewinnt.planimetry.ui.text.NameComponent;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class Drawing {
    public final List<BiConsumer<Shape, Shape>> swapListeners = new ArrayList<>();
    public final Long2ObjectMap<Shape> shapeIds = new Long2ObjectOpenHashMap<>();
    public final List<Shape> allShapes;
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
    private boolean isUnsaved = false;
    boolean isLoading = false;

    public Drawing() {
        this.allShapes = new ArrayList<>();
        this.shapes = new ArrayList<>();
        this.points = new ArrayList<>();
        this.name = "Безымянный";
        this.creationTime = System.currentTimeMillis();
        this.lastEditTime = creationTime;
    }

    public Drawing(Collection<Shape> shapes, Collection<PointProvider> points, String name, long creationTime, long lastEditTime) {
        this.allShapes = new ArrayList<>(shapes);
        this.allShapes.addAll(points);
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
        this.allShapes = new ArrayList<>();
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
        if (this.allShapes.contains(shape)) return;
        update();
        this.allShapes.add(shape);
        if (shape instanceof PointProvider point) {
            if (!this.points.contains(point)) {
                this.points.add(point);
            }
        } else if (!this.shapes.contains(shape)) {
            this.shapes.add(shape);
        }
        shape.onAdded();
        this.shapeIds.put(shape.getId(), shape);
    }

    private void addShapeQuick(Shape shape) {
        this.allShapes.add(shape);
        if (shape instanceof PointProvider point) {
            this.points.add(point);
        } else {
            this.shapes.add(shape);
        }
        this.shapeIds.put(shape.getId(), shape);
        shape.onAdded();
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
                if (i == shape) {
                    return true;
                }
            }
        }
        return false;
    }

    public void replaceShape(Shape old, Shape neo) {
        if (neo == null) return;
        update();
        // swap dependencies
        for (Shape i : old.getDependencies()) {
            neo.addDependency(i);
            i.removeDepending(old);
            i.replaceShape(old, neo);
        }
        for (Shape i : old.getDependingShapes()) {
            neo.addDepending(i);
            i.removeDependency(old);
            i.replaceShape(old, neo);
        }
        // both must either be both points, or both not points since they go to different lists
        if (old instanceof PointProvider != neo instanceof PointProvider) {
            throw new IllegalArgumentException("Cannot replace a point with a non-point or vice-versa");
        }
        boolean call = !this.allShapes.contains(neo);
        // actually swap them
        if (neo instanceof PointProvider point) {
            if (this.points.contains(old)) {
                this.points.set(this.points.indexOf(old), point);
            } else if (!this.points.contains(point)) {
                this.points.add(point);
            }
        } else {
            if (this.shapes.contains(old)) {
                this.shapes.set(this.shapes.indexOf(old), neo);
            } else if (!this.shapes.contains(neo)) {
                this.shapes.add(neo);
            }
        }
        if (this.allShapes.contains(old)) {
            this.allShapes.set(this.allShapes.indexOf(old), neo);
        } else if (!this.allShapes.contains(neo)) {
            this.allShapes.add(neo);
        }
        this.swapListeners.forEach(listener -> listener.accept(old, neo));
        if (call) {
            neo.onAdded();
        }
    }

    public void removeShape(Shape shape) {
        update();
        this.allShapes.remove(shape);
        this.shapes.remove(shape);
        this.points.remove(shape);
        shape.onRemoved();
    }

    public long getId(Shape shape) {
        if (isLoading) return 0;
        if (this.shapeIds.containsValue(shape)) {
            return shape.getId();
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
        isUnsaved = true;
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

    public Drawing setName(String name) {
        this.name = name;
        return this;
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

    public boolean isUnsaved() {
        return isUnsaved;
    }

    public void addSwapListener(BiConsumer<Shape, Shape> listener) {
        this.swapListeners.add(listener);
    }

    public void clearSwapListeners() {
        this.swapListeners.clear();
    }

    public PointProvider getNearestPoint(double x, double y) {
        PointProvider hovered = null;
        double minDistance = Double.MAX_VALUE;
        for (Shape i : this.points) {
            if (i instanceof PointProvider point) {
                double distance = point.distanceToMouse(x, y, null);
                if (distance <= minDistance) {
                    hovered = point;
                    minDistance = distance;
                }
            }
        }
        return hovered;
    }

    public PointProvider getNearestPoint(Vec2 point) {
        return getNearestPoint(point.x, point.y);
    }

    public CompoundTag toNbt() {
        CompoundTag output = new CompoundTag();
        SavingContext context = new SavingContext(Stream.concat(this.shapes.stream(), this.points.stream()).toList());
        ListTag<CompoundTag> shapes = new ListTag<>(CompoundTag.class);
        shapes.addAll(context.save());
        output.put("shapes", shapes);
        output.putString("name", name);
        output.putLong("creation_time", creationTime);
        output.putLong("last_edit_time", lastEditTime);
        output.putInt("data_version", DynamicPlanimetry.DATA_VERSION);
        return output;
    }

    public static Drawing fromNbt(CompoundTag nbt) {
        nbt = DataUpdater.updateDrawing(nbt); // will do stuff in the future
        if (nbt == null) return null;
        String name = nbt.getString("name");
        long creationTime = nbt.getLong("creation_time");
        long lastEditTime = nbt.getLong("last_edit_time");
        ListTag<CompoundTag> shapes = nbt.getListTag("shapes").asCompoundTagList();
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

    public Shape getRandom(Predicate<Shape> predicate) {
        Shape[] shapes = this.allShapes.stream().filter(predicate).toArray(Shape[]::new);
        return shapes[DynamicPlanimetry.RANDOM.nextInt(shapes.length)];
    }

    public void save(String filename, boolean isAbsolute) {
        this.withFilename(filename, isAbsolute).save();
    }

    public void saveAs(File file) {
        try {
            while (file.exists()) {
                file = new File(randomizeName(file.getPath(), DynamicPlanimetry.RANDOM));
            }
            NBTUtil.write(this.toNbt(), file);
        } catch (IOException e) {
            Notifications.addNotification("Error saving file: " + e.getMessage(), 5000);
            e.printStackTrace();
        }
    }

    private static String randomizeName(String path, Random random) {
        if (path.endsWith(".dpd")) {
            path = path.substring(0, path.length() - 4) + random.nextInt() + ".dpd";
            return path;
        }
        return path + random.nextInt();
    }

    public void save() {
        CompoundTag nbt = this.toNbt();
        File file;
        if (filename == null) {
            filename = "autosave " + DynamicPlanimetry.AUTOSAVE_DATE_FORMAT.format(new Date(System.currentTimeMillis()));
        }
        if (isFileAbsolute) {
            file = new File(filename);
        } else {
            file = DynamicPlanimetry.io().dataFile("drawings/" + filename + ".dpd");
        }
        try {
            NBTUtil.write(nbt, file);
            isUnsaved = false;
        } catch (IOException e) {
            Notifications.addNotification("Error saving file: " + e.getMessage(), 5000);
            e.printStackTrace();
        }
    }

    public static Drawing load(String filenameAbsolute) {
        try {
            CompoundTag nbt = ((CompoundTag) NBTUtil.read(filenameAbsolute).getTag());
            return Drawing.fromNbt(nbt).withFilename(filenameAbsolute, true);
        } catch (IOException e) {
            Notifications.addNotification("Error loading file: " + e.getMessage(), 5000);
            return null;
        }
    }

    public static Drawing load(File file) {
        try {
            CompoundTag nbt = ((CompoundTag) NBTUtil.read(file).getTag());
            return Drawing.fromNbt(nbt).withFilename(file.getAbsolutePath(), true);
        } catch (IOException e) {
            Notifications.addNotification("Error loading file: " + e.getMessage(), 5000);
            e.printStackTrace();
            return null;
        }
    }
}
