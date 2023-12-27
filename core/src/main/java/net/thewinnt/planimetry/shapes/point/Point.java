package net.thewinnt.planimetry.shapes.point;

import java.util.Collection;
import java.util.List;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
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
        this.property = new Vec2Property(Component.literal("Координаты"), position);
        this.property.addValueChangeListener(pos -> {
            Point.this.position = pos;
        });
        this.addMovementListener(delta -> property.setValue(Point.this.position));
        this.componentProperty = new NameComponentProperty(Component.literal("Имя"), this.name);
        this.componentProperty.addValueChangeListener(component -> setName(component));
    }

    public Point(Drawing drawing, Vec2 position, NameComponent name) {
        super(drawing, name);
        this.position = position;
        this.property = new Vec2Property(Component.literal("Координаты"), position);
        this.property.addValueChangeListener(pos -> Point.this.position = pos);
        this.addMovementListener(delta -> property.setValue(Point.this.position));
        this.componentProperty = new NameComponentProperty(Component.literal("Имя"), this.name);
        this.componentProperty.addValueChangeListener(component -> setName(component));
    }

    @Override
    public Vec2 getPosition() {
        return position;
    }

    @Override
    public boolean contains(Vec2 point) {
        return point.equals(position);
    }

    @Override
    public boolean contains(double x, double y) {
        return position.x == x && position.y == y;
    }

    @Override
    public double distanceToMouse(Vec2 point, DrawingBoard board) {
        return point.distanceToSqr(position);
    }

    @Override
    public double distanceToMouse(double x, double y, DrawingBoard board) {
        return position.distanceToSqr(x, y);
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

    @Override
    public Collection<Property<?>> getProperties() {
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
        return nbt;
    }

    public static Point readNbt(CompoundTag nbt, LoadingContext context) {
        double x = nbt.getDouble("x").getValue();
        double y = nbt.getDouble("y").getValue();
        if (nbt.containsCompound("name")) {
            NameComponent name = NameComponent.fromNbt(nbt.getCompound("name"));
            return new Point(context.getDrawing(), new Vec2(x, y), name);
        }
        return new Point(context.getDrawing(), new Vec2(x, y));
    }
}
