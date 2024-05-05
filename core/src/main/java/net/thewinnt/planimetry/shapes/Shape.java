package net.thewinnt.planimetry.shapes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.AABB;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.functions.BasicNamedFunction;
import net.thewinnt.planimetry.ui.functions.Function;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.ComponentRepresentable;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class Shape implements ComponentRepresentable {
    /** A dummy drawing for any shapes used for math */
    public static final Drawing DUMMY_DRAWING = new Drawing().setName("$DP_DUMMY");
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
    public abstract boolean intersects(AABB aabb);
    //  {
    //     // TODO implement sometime
    //     return false;
    // }

    /**
     * Renders the shape
     * @param drawer the {@link ShapeDrawer} to draw the shape with
     * @param selection whether the shape's selected
     * @param font the font to use for additional data
     * @param board the board to gather data from (and perform coordinate conversions)
     */
    public abstract void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board);

    public Collection<Property<?>> getProperties() {
        return new ArrayList<>();
    }

    public Collection<Function<?>> getFunctions() {
        ArrayList<Function<?>> output = new ArrayList<>();
        output.add(new BasicNamedFunction<>(drawing, this, shape -> shape.delete(defaultIgnoreDependencies(), false), Component.translatable("function.generic.delete"), Component.translatable("function.generic.delete.action")));
        return output;
    }

    public final Collection<Shape> getDependencies() {
        return Collections.unmodifiableCollection(dependencies);
    }

    public final Collection<Shape> getDependingShapes() {
        return Collections.unmodifiableCollection(dependents);
    }

    /** Adds a shape that this shape depends on */
    public final void addDependency(Shape shape) {
        if (shape != this) this.dependencies.add(shape);
    }

    /** Adds a shape that depends on this shape */
    public final void addDepending(Shape shape) {
        if (shape != this) this.dependents.add(shape);
    }

    /** Removes the given shape from the dependencies list */
    public final void removeDependency(Shape shape) {
        this.dependencies.remove(shape);
    }

    /** Removes the given shape from the dependents list */
    public final void removeDepending(Shape shape) {
        this.dependents.remove(shape);
    }

    /**
     * Called when a shape related to this shape is being replaced by another one. <b>DO NOT</b> change the
     * shapes' dependency data here. This has already been done before this method was called. If the shape
     * provided is somehow unrelated to this, don't do anything.
     * @param old a shape related to this shape. Never equals to {@code this}. Always present in either
     * {@code dependencies} or {@code dependents}. When checking, which exactly shape is this, use {@code ==}
     * instead of {@code .equals()}.
     * @param neo the shape that the old one is being replaced by
     * @throws IllegalArgumentException if anything's wrong with the provided shape
     * @throws ClassCastException if the new shape's class does not match what is needed for this shape. (e.g.
     * a point being replaced by a line)
     */
    public abstract void replaceShape(Shape old, Shape neo);

    public abstract void move(Vec2 delta);
    public abstract void move(double dx, double dy);
    public abstract boolean canMove();

    /**
     * Tries to delete this shape from its drawing
     * @param includeDependencies whether to also delete dependencies that would be left without ones
     * @param force whether to delete this shape regardless of whether there are shapes depending on it
     * @return whether this shape got deleted
     */
    public boolean delete(boolean includeDependencies, boolean force) {
        if (!force && !this.dependents.isEmpty()) return false;
        drawing.removeShape(this);
        for (Shape i : this.dependencies) {
            i.removeDepending(this);
        }
        if (includeDependencies) {
            for (Shape i : this.dependencies) {
                if (i.dependents.isEmpty()) i.delete(false, false);
            }
        }
        if (force && !this.dependents.isEmpty()) {
            for (Shape i : this.dependents.toArray(new Shape[0])) { // prevent concurrent modification
                i.delete(false, true);
            }
        }
        DrawingBoard board = DynamicPlanimetry.getInstance().editorScreen.getBoard();
        if (board != null) {
            board.setSelection(null);
        }
        return true;
    }

    /** Returns the default value for {@link #delete(boolean, boolean)}'s ignoreDependencies */
    public boolean defaultIgnoreDependencies() {
        return true;
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
