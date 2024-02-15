package net.thewinnt.planimetry.shapes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.functions.Function;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.ComponentRepresentable;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class Shape implements ComponentRepresentable {
    /** A list of shapes that this shape depends on */
    protected final ArrayList<Shape> dependencies = new ArrayList<>();
    /** A list of shapes that depend on this shape */
    protected final ArrayList<Shape> dependents = new ArrayList<>();
    protected final Drawing drawing;
    private long id;
    protected Component nameOverride;

    public Shape(Drawing drawing) {
        this.drawing = drawing;
        this.id = drawing.getId(this);
    }

    public abstract boolean contains(Vec2 point);
    public abstract boolean contains(double x, double y);
    public abstract double distanceToMouse(Vec2 point, DrawingBoard board);
    public abstract double distanceToMouse(double x, double y, DrawingBoard board);

    /**
     * Renders the shape
     * @param drawer the {@link ShapeDrawer} to draw the shape with
     * @param selection whether the shape's selected
     * @param font the font to use for additional data
     * @param board the board to gather data from (and perform coordinate conversions)
     */
    public abstract void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board);

    public abstract Collection<Property<?>> getProperties();
    public Collection<Function<?>> getFunctions() {
        return Collections.emptyList();
    }

    public final Collection<Shape> getDependencies() {
        return Collections.unmodifiableCollection(dependencies);
    }

    public final Collection<Shape> getDependingShapes() {
        return Collections.unmodifiableCollection(dependents);
    }

    public final void addDependency(Shape shape) {
        this.dependencies.add(shape);
    }

    public final void addDepending(Shape shape) {
        this.dependents.add(shape);
    }

    public abstract Component getName();
    public abstract String getTypeName();

    /** @deprecated use {@link #toNbt(SavingContext)} for saving instead, as this provides incomplete data */
    @Deprecated public abstract CompoundTag writeNbt(SavingContext context);
    public abstract ShapeDeserializer<?> getDeserializer();

    public boolean shouldRender() {
        return true;
    }

    /**
     * Returns this shape's unique ID.
     */
    public final long getId() {
        return id;
    }

    public Drawing getDrawing() {
        return drawing;
    }

    protected float getThickness(double scale) {
        return (float)Math.min(Math.max(1, Math.cbrt(scale)), 4);
    }

    private final void setId(long id) {
        this.id = id;
    }

    public Component getNameOverride() {
        return nameOverride;
    }

    public void setNameOverride(Component nameOverride) {
        this.nameOverride = nameOverride;
    }

    public static Shape fromNbt(CompoundTag nbt, LoadingContext context) {
        String type = nbt.getString("type").getValue();
        long id = nbt.getLong("id").getValue();
        Shape shape = ShapeData.getDeserializer(type).deserialize(nbt, context);
        shape.setId(id);
        if (nbt.containsCompound("name_override")) {
            shape.setNameOverride(Component.fromNbt(nbt.getCompound("name_override")));
        }
        return shape;
    }

    public final CompoundTag toNbt(SavingContext context) {
        CompoundTag nbt = this.writeNbt(context);
        nbt.putLong("id", this.id);
        nbt.putString("type", ShapeData.getShapeType(this.getDeserializer()));
        if (nameOverride != null) {
            nbt.put("name_override", nameOverride.toNbt());
        }
        return nbt;
    }

    @Override
    public Component toComponent() {
        if (nameOverride != null) return getNameOverride();
        return getName();
    }

    public static enum SelectionStatus {
        NONE,
        HOVERED,
        SELECTED
    }

    @FunctionalInterface
    public static interface ShapeDeserializer<T extends Shape> {
        T deserialize(CompoundTag nbt, LoadingContext context);
    }
}
