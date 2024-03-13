package net.thewinnt.planimetry.shapes.point.relative;

import java.util.Collection;
import java.util.List;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.Settings;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.NbtUtil;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Circle;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.types.NameComponentProperty;
import net.thewinnt.planimetry.ui.properties.types.NumberProperty;
import net.thewinnt.planimetry.ui.properties.types.ShapeProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.NameComponent;

public class CirclePoint extends PointProvider {
    private final ShapeProperty circleProperty;
    private final NumberProperty angleProperty;
    private final NameComponentProperty nameProperty;
    private Circle circle;
    private double angle;

    public CirclePoint(Drawing drawing, Circle circle, double angleRad) {
        super(drawing);
        this.circle = circle;
        this.circleProperty = new ShapeProperty(Component.translatable("property.circle_point.source"), drawing, this.circle, shape -> shape instanceof Circle);
        this.circleProperty.addValueChangeListener(shape -> setCircle((Circle) shape));
        this.angle = angleRad;
        this.angleProperty = new NumberProperty(Component.translatable("property.circle_point.angle"), angleRad);
        this.angleProperty.addValueChangeListener(newAngle -> setAngle(Settings.get().toRadians(newAngle)));
        this.nameProperty = new NameComponentProperty(Component.translatable("property.circle_point.name"), this.name);
        this.nameProperty.addValueChangeListener(this::setName);
    }

    public CirclePoint(Drawing drawing, Circle circle, double angleRad, NameComponent name) {
        super(drawing, name);
        this.circle = circle;
        this.circleProperty = new ShapeProperty(Component.translatable("property.circle_point.source"), drawing, this.circle, shape -> shape instanceof Circle);
        this.circleProperty.addValueChangeListener(shape -> setCircle((Circle) shape));
        this.angle = angleRad;
        this.angleProperty = new NumberProperty(Component.translatable("property.circle_point.angle"), angleRad);
        this.angleProperty.addValueChangeListener(newAngle -> setAngle(Settings.get().toRadians(newAngle)));
        this.nameProperty = new NameComponentProperty(Component.translatable("property.circle_point.name"), this.name);
        this.nameProperty.addValueChangeListener(this::setName);
        this.addDependency(circle);
        circle.addDepending(this);
    }

    @Override
    public Vec2 getPosition() {
        double radius = circle.getRadius();
        return circle.center.getPosition().add(Math.cos(angle) * radius, Math.sin(angle) * radius);
    }

    @Override
    public void move(Vec2 delta) {
        Vec2 oldPos = this.getPosition();
        Vec2 newPos = this.getPosition().add(delta);
        this.angle = MathHelper.polarAngle(this.circle.center.getPosition(), newPos);
        this.movementListeners.forEach(consumer -> consumer.accept(this.getPosition().subtract(oldPos)));
    }

    @Override
    public void move(double dx, double dy) {
        Vec2 oldPos = this.getPosition();
        Vec2 newPos = this.getPosition().add(dx, dy);
        this.angle = MathHelper.polarAngle(this.circle.center.getPosition(), newPos);
        this.movementListeners.forEach(consumer -> consumer.accept(this.getPosition().subtract(oldPos)));
    }

    @Override
    public boolean canMove() {
        return true;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(Circle circle) {
        this.circle.removeDepending(this);
        this.removeDependency(this.circle);
        this.circle = circle;
        circle.addDepending(this);
        this.addDepending(circle);
    }

    @Override
    protected boolean shouldAutoAssingnName() {
        return true;
    }

    @Override
    public Collection<Property<?>> getProperties() {
        angleProperty.setValue(Settings.get().toUnit(angle));
        return List.of(circleProperty, angleProperty, nameProperty);
    }

    @Override
    public CompoundTag writeNbt(SavingContext context) {
        CompoundTag nbt = new CompoundTag();
        context.addShape(circle);
        nbt.putLong("circle", circle.getId());
        nbt.putDouble("angle", angle);
        nbt.put("name", this.name.toNbt());
        return nbt;
    }

    @Override
    public ShapeDeserializer<?> getDeserializer() {
        return ShapeData.CIRCLE_POINT;
    }

    public static CirclePoint readNbt(CompoundTag nbt, LoadingContext context) {
        Circle circle = (Circle) context.resolveShape(nbt.getLong("circle").longValue());
        double angle = nbt.getDouble("angle").doubleValue();
        boolean shouldRender = NbtUtil.getOptionalBoolean(nbt, "should_render", true);
        if (nbt.containsCompound("name")) {
            NameComponent name = NameComponent.readNbt(nbt.getCompound("name"));
            CirclePoint output = new CirclePoint(context.getDrawing(), circle, angle, name);
            output.shouldRender = shouldRender;
            return output;
        }
        CirclePoint output = new CirclePoint(context.getDrawing(), circle, angle);
        output.shouldRender = shouldRender;
        return output;
    }
}
