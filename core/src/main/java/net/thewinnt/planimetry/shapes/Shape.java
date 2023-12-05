package net.thewinnt.planimetry.shapes;

import java.util.Collection;
import java.util.List;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.NameComponent;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class Shape {
    private static long idCounter;
    private long id = idCounter++;

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

    public abstract String getName();
    public List<NameComponent> getFullName() {
        return List.of();
    }
    public abstract String getTypeName();

    /** @deprecated use {@link #toNbt()} instead, as this provides incomplete data */
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

    protected float getThickness(double scale) {
        return (float)Math.min(Math.max(1, Math.cbrt(scale)), 4);
    }

    private final void setId(long id) {
        this.id = id;
    }

    public static Shape fromNbt(CompoundTag nbt, LoadingContext context) {
        String type = nbt.getString("type").getValue();
        long id = nbt.getLong("id").getValue();
        Shape shape = ShapeData.getDeserializer(type).deserialize(nbt, context);
        shape.setId(id);
        return shape;
    }

    public final CompoundTag toNbt(SavingContext context) {
        CompoundTag nbt = this.writeNbt(context);
        nbt.putLong("id", this.id);
        nbt.putString("type", ShapeData.getShapeType(this.getDeserializer()));
        return nbt;
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
