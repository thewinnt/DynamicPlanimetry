package net.thewinnt.planimetry.shapes.point;

import java.util.Collection;
import java.util.List;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.NbtUtil;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.types.NameComponentProperty;
import net.thewinnt.planimetry.ui.properties.types.Vec2Property;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.NameComponent;

public class Point extends PointProvider {
    private Vec2 position;
    private final Vec2Property property;
    private final NameComponentProperty componentProperty;

    public Point(Drawing drawing, Vec2 position) {
        super(drawing);
        this.position = position;
        this.property = new Vec2Property(Component.translatable("property.point.coordinates"), position);
        this.property.addValueChangeListener(pos -> Point.this.position = pos);
        this.addMovementListener(delta -> property.setValue(Point.this.position));
        this.componentProperty = new NameComponentProperty(Component.translatable("property.point.name"), this.name);
        this.componentProperty.addValueChangeListener(this::setName);
    }

    public Point(Drawing drawing, Vec2 position, NameComponent name) {
        super(drawing, name);
        this.position = position;
        this.property = new Vec2Property(Component.translatable("property.point.coordinates"), position);
        this.property.addValueChangeListener(pos -> Point.this.position = pos);
        this.addMovementListener(delta -> property.setValue(Point.this.position));
        this.componentProperty = new NameComponentProperty(Component.translatable("property.point.name"), this.name);
        this.componentProperty.addValueChangeListener(this::setName);
    }

    @Override
    public Vec2 getPosition() {
        return position;
    }

    @Override
    public boolean contains(Vec2 point) {
        return MathHelper.roughlyEquals(point, position);
    }

    @Override
    public boolean contains(double x, double y) {
        return MathHelper.roughlyEquals(position.x, x) && MathHelper.roughlyEquals(position.y, y);
    }

    @Override
    public double distanceToMouse(Vec2 point, DrawingBoard board) {
        return point.distanceTo(position);
    }

    @Override
    public double distanceToMouse(double x, double y, DrawingBoard board) {
        return position.distanceTo(x, y);
    }

    @Override
    public boolean canMove() {
        return true;
    }

    @Override
    public void move(Vec2 delta) {
        position = position.add(delta);
        this.movementListeners.forEach(i -> i.accept(delta));
    }

    @Override
    public void move(double dx, double dy) {
        position = position.add(dx, dy);
        this.movementListeners.forEach(i -> i.accept(new Vec2(dx, dy)));
    }

    public void setPosition(Vec2 position) {
        this.position = position;
    }

    @Override
    public Collection<Property<?>> moreProperties() {
        if (nameOverride != null) return List.of(property);
        return List.of(property, componentProperty);
    }

    @Override
    protected boolean shouldAutoAssingnName() {
        return true;
    }
    @Override
    public ShapeDeserializer<Point> getDeserializer() {
        return ShapeData.POINT_SIMPLE;
    }

    @Override
    public CompoundTag writeNbt(SavingContext context) {
        CompoundTag nbt = new CompoundTag();
        nbt.putDouble("x", this.position.x);
        nbt.putDouble("y", this.position.y);
        nbt.put("name", name.toNbt());
        NbtUtil.writeBoolean(nbt, "should_render", shouldRender());
        return nbt;
    }

    public static Point readNbt(CompoundTag nbt, LoadingContext context) {
        double x = nbt.getDouble("x").getValue();
        double y = nbt.getDouble("y").getValue();
        boolean shouldRender = NbtUtil.getOptionalBoolean(nbt, "should_render", true);
        if (nbt.containsCompound("name")) {
            NameComponent name = NameComponent.readNbt(nbt.getCompound("name"));
            Point output = new Point(context.getDrawing(), new Vec2(x, y), name);
            output.setShouldRender(shouldRender);
            return output;
        }
        Point output = new Point(context.getDrawing(), new Vec2(x, y));
        output.setShouldRender(shouldRender);
        return output;
    }
}
