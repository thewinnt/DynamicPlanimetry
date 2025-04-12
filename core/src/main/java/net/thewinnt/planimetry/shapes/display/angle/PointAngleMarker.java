package net.thewinnt.planimetry.shapes.display.angle;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.text.Component;

public class PointAngleMarker extends AngleMarker {
    private PointProvider a, b, c;

    public PointAngleMarker(Drawing drawing, PointProvider a, PointProvider b, PointProvider c) {
        super(drawing);
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public double getAngle() {
        return MathHelper.angle(a.getPosition(), b.getPosition(), c.getPosition());
    }

    @Override
    public double getAngleA() {
        return MathHelper.angleTo(b.getPosition(), a.getPosition());
    }

    @Override
    public double getAngleB() {
        return MathHelper.angleTo(b.getPosition(), c.getPosition());
    }

    @Override
    public Vec2 getStartPoint() {
        return b.getPosition();
    }

    @Override
    public Component getName() {
        return Component.translatable(getTypeName(), a.getNameComponent(), b.getNameComponent(), c.getNameComponent());
    }

    @Override
    public String getTypeName() {
        return this.getPropertyName("point");
    }

    @Override
    public void replaceShape(Shape old, Shape neo) {
        if (old == this.a) {
            this.a = (PointProvider)neo;
        } else if (old == this.b) {
            this.b = (PointProvider)neo;
        } else if (old == this.c) {
            this.c = (PointProvider)neo;
        }
    }

    @Override
    public CompoundTag writeNbt(SavingContext context) {
        context.addShape(a);
        context.addShape(b);
        context.addShape(c);
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("a", a.getId());
        nbt.putLong("main", b.getId());
        nbt.putLong("b", c.getId());
        return nbt;
    }

    @Override
    public ShapeDeserializer<?> type() {
        return ShapeData.POINT_ANGLE_MARKER;
    }

    public static PointAngleMarker readNbt(CompoundTag nbt, LoadingContext context) {
        PointProvider a = (PointProvider)context.resolveShape(nbt.getLong("a"));
        PointProvider b = (PointProvider)context.resolveShape(nbt.getLong("main"));
        PointProvider c = (PointProvider)context.resolveShape(nbt.getLong("b"));
        return new PointAngleMarker(context.getDrawing(), a, b, c);
    }

    @Override
    public boolean defaultIgnoreDependencies() {
        return true;
    }
}
