package net.thewinnt.planimetry.shapes.point;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.NameComponent;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class PointReference extends PointProvider {
    private PointProvider point;

    public PointReference(PointProvider point) {
        super(point.getDrawing());
        if (point instanceof PointReference ref) {
            point = ref.getPoint(); // prevent referring to references at all cost
        }
        this.point = point;
        this.addDependency(point);
    }

    @Override
    public boolean contains(Vec2 point) {
        return this.point.contains(point);
    }

    @Override
    public boolean contains(double x, double y) {
        return this.point.contains(x, y);
    }

    @Override
    public double distanceToMouse(Vec2 point, DrawingBoard board) {
        return this.point.distanceToMouse(point, board);
    }

    @Override
    public double distanceToMouse(double x, double y, DrawingBoard board) {
        return this.point.distanceToMouse(x, y, board);
    }

    @Override
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        this.point.render(drawer, selection, font, board);
    }

    @Override
    public Vec2 getPosition() {
        return this.point.getPosition();
    }

    @Override
    public boolean canMove() {
        return this.point.canMove();
    }

    @Override
    public void move(Vec2 delta) {
        this.point.move(delta);
        this.movementListeners.forEach(listener -> listener.accept(delta));
    }

    @Override
    public void move(double dx, double dy) {
        this.point.move(dx, dy);
        this.movementListeners.forEach(listener -> listener.accept(new Vec2(dx, dy)));
    }

    public void setPoint(PointProvider point) {
        this.removeDependency(this.point);
        if (point instanceof PointReference ref) {
            this.point = ref.getPoint();
        } else {
            this.point = Objects.requireNonNull(point);
        }
        this.addDependency(this.point);
    }

    public PointProvider getPoint() {
        return point;
    }
    
    @Override
    public void rebuildProperties() {
        this.point.rebuildProperties();
        this.properties.clear();
        this.properties.addAll(this.point.getProperties());
    }

    @Override
    protected Collection<Property<?>> moreProperties() {
        return List.of();
    }

    @Override
    public Component getName() {
        return this.point.getName();
    }

    @Override
    public void setName(NameComponent name) {
        this.point.setName(name);
    }

    @Override
    public void setNameOverride(Component nameOverride) {
        this.point.setNameOverride(nameOverride);
    }

    @Override
    public Component getNameOverride() {
        return this.point.getNameOverride();
    }

    @Override
    protected boolean shouldAutoAssingnName() {
        return false;
    }

    @Override
    public Component getNameComponent() {
        return this.point.getNameComponent();
    }

    @Override
    public boolean delete(boolean includeDependencies, boolean force) {
        super.delete(includeDependencies, force);
        return point.delete(includeDependencies, force);
    }

    @Override
    public boolean shouldRender() {
        return point.shouldRender() && this.shouldRender.getValue();
    }

    @Override
    public ShapeDeserializer<PointReference> type() {
        return ShapeData.POINT_REFERENCE;
    }

    @Override
    public void replaceShape(Shape old, Shape neo) {
        if (old == this.point) {
            this.setPoint((PointProvider)neo);
        }
    }

    @Override
    public CompoundTag writeNbt(SavingContext context) {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("point", this.point.getId());
        context.addShape(this.point);
        return nbt;
    }

    public static PointReference readNbt(CompoundTag nbt, LoadingContext context) {
        return new PointReference((PointProvider)context.resolveShape(nbt.getLong("point")));
    }

    @Override
    public String toString() {
        return super.toString() + " -> " + this.point.toString();
    }
}
