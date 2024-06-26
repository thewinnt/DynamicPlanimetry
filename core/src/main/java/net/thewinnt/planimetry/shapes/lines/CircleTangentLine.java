package net.thewinnt.planimetry.shapes.lines;

import java.util.Collection;
import java.util.List;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.Settings;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Circle;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.point.relative.AngleOffsetPoint;
import net.thewinnt.planimetry.shapes.point.relative.CirclePoint;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.types.NumberProperty;
import net.thewinnt.planimetry.ui.properties.types.ShapeProperty;
import net.thewinnt.planimetry.ui.text.Component;

public class CircleTangentLine extends InfiniteLine {
    private final NumberProperty angleProperty;
    private final ShapeProperty circleProperty;
    private Circle circle;
    private double angle;

    public CircleTangentLine(Drawing drawing, Circle circle, double angle) {
        super(drawing, new CirclePoint(drawing, circle, angle), new AngleOffsetPoint(drawing, circle.center, angle, angle));
        this.circle = circle;
        this.angle = angle;
        CirclePoint a = (CirclePoint)this.a.getPoint();
        AngleOffsetPoint b = (AngleOffsetPoint)this.b.getPoint();
        a.setAngle(angle);
        b.setAngle(angle + MathHelper.HALF_PI);
        a.addMovementListener(delta -> setAngleFromPoint(a.getAngle()));
        a.setShouldRender(false);
        b.setStart(this.a.getPoint());
        b.setOffset(100);
        b.setShouldRender(false);
        this.angleProperty = new NumberProperty(Component.translatable("property.circle_tangent.angle"), angle);
        this.angleProperty.addValueChangeListener(newAngle -> setAngle(Settings.get().toRadians(newAngle)));
        this.circleProperty = new ShapeProperty(Component.translatable("property.circle_tangent.source"), drawing, circle, shape -> shape instanceof Circle);
        this.circleProperty.addValueChangeListener(shape -> setCircle((Circle) shape));
        this.addDependency(circle);
        circle.addDepending(this);
    }

    public void setAngle(double angle) {
        this.angle = angle;
        ((CirclePoint)this.a.getPoint()).setAngle(angle);
        ((AngleOffsetPoint)this.b.getPoint()).setAngle(angle + MathHelper.HALF_PI);
    }

    private void setAngleFromPoint(double angle) {
        this.angle = angle;
        ((AngleOffsetPoint)this.b.getPoint()).setAngle(angle + MathHelper.HALF_PI);
    }

    public void setCircle(Circle circle) {
        this.circle.removeDepending(this);
        this.removeDependency(this.circle);
        this.circle = circle;
        circle.addDepending(this);
        this.addDepending(circle);
        ((CirclePoint)this.a.getPoint()).setCircle(this.circle);
    }

    @Override
    public boolean defaultIgnoreDependencies() {
        return false;
    }

    @Override
    public ShapeDeserializer<? extends InfiniteLine> getDeserializer() {
        return ShapeData.CIRCLE_TANGENT;
    }

    @Override
    public String getTypeName() {
        return "shape.circle_tangent";
    }

    @Override
    public Component getName() {
        return Component.translatable(getTypeName(), circle.center.getNameComponent(), this.a.getNameComponent());
    }

    @Override
    public Collection<Property<?>> getProperties() {
        this.angleProperty.setValue(Settings.get().toUnit(this.angle));
        return List.of(angleProperty, circleProperty);
    }

    public static CircleTangentLine readNbt(CompoundTag nbt, LoadingContext context) {
        Circle a = (Circle)context.resolveShape(nbt.getLong("circle").getValue());
        double angle = nbt.getDouble("angle").doubleValue();
        return new CircleTangentLine(context.getDrawing(), a, angle);
    }

    @Override
    public CompoundTag writeNbt(SavingContext context) {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("circle", circle.getId());
        nbt.putDouble("angle", angle);
        return nbt;
    }

    @Override
    public void move(Vec2 delta) {
        this.a.move(delta);
    }

    @Override
    public void move(double dx, double dy) {
        this.a.move(dx, dy);
    }

    @Override
    public void replaceShape(Shape old, Shape neo) {
        super.replaceShape(old, neo);
        if (old == circle) {
            this.setCircle((Circle)neo);
        }
    }
}
