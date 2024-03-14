package net.thewinnt.planimetry.shapes.display.angle;

import java.util.Collection;
import java.util.List;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.text.Component;

public class PointAngleMarker extends AngleMarker {
    private final PointProvider a, b, c;
    
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
        return MathHelper.polarAngle(b.getPosition(), a.getPosition());
    }

    @Override
    public double getAngleB() {
        return MathHelper.polarAngle(b.getPosition(), c.getPosition());
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
        return "shape.angle_marker.point";
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
    public Collection<Property<?>> getProperties() {
        return List.of();
    }

    @Override
    public ShapeDeserializer<?> getDeserializer() {
        return ShapeData.POINT_ANGLE_MARKER;
    }

    public static PointAngleMarker readNbt(CompoundTag nbt, LoadingContext context) {
        PointProvider a = (PointProvider)context.resolveShape(nbt.getLong("a").longValue());
        PointProvider b = (PointProvider)context.resolveShape(nbt.getLong("main").longValue());
        PointProvider c = (PointProvider)context.resolveShape(nbt.getLong("b").longValue());
        return new PointAngleMarker(context.getDrawing(), a, b, c);
    }

    @Override
    public boolean defaultIgnoreDependencies() {
        return true;
    }
}
