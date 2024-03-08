package net.thewinnt.planimetry.shapes.point.relative;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.Settings;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.NbtUtil;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.types.DisplayProperty;
import net.thewinnt.planimetry.ui.properties.types.NameComponentProperty;
import net.thewinnt.planimetry.ui.properties.types.NumberProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.NameComponent;

import java.util.Collection;
import java.util.List;

import static net.thewinnt.planimetry.ui.text.Component.translatable;

public class AngleOffsetPoint extends PointProvider {
    private PointProvider start;
    private double angle;
    private double offset;
    private final DisplayProperty sourceDescription;
    private final NumberProperty angleProperty;
    private final NumberProperty offsetProperty;
    private final NameComponentProperty nameProperty;

    public AngleOffsetPoint(Drawing drawing, PointProvider start, double angle, double offset) {
        super(drawing);
        this.start = start;
        this.angle = angle;
        this.offset = offset;
        this.sourceDescription = new DisplayProperty(Component.translatable("property.angle_offset_point.source", Component.optional(start.getNameComponent())));
        this.angleProperty = new NumberProperty(Component.translatable("property.angle_offset_point.angle"), angle);
        this.angleProperty.addValueChangeListener(newAngle -> AngleOffsetPoint.this.angle = Settings.get().toRadians(newAngle));
        this.offsetProperty = new NumberProperty(translatable("property.angle_offset_point.distance"), offset);
        this.offsetProperty.addValueChangeListener(distance -> AngleOffsetPoint.this.offset = distance);
        this.nameProperty = new NameComponentProperty(translatable("property.angle_offset_point.name"), this.name);
        this.nameProperty.addValueChangeListener(this::setName);
    }

    public AngleOffsetPoint(Drawing drawing, PointProvider start, double angle, double offset, NameComponent name) {
        super(drawing, name);
        this.start = start;
        this.angle = angle;
        this.offset = offset;
        this.sourceDescription = new DisplayProperty(Component.translatable("property.angle_offset_point.source", Component.optional(start.getNameComponent())));
        this.angleProperty = new NumberProperty(Component.translatable("property.angle_offset_point.angle"), angle);
        this.angleProperty.addValueChangeListener(newAngle -> AngleOffsetPoint.this.angle = Settings.get().toRadians(newAngle));
        this.offsetProperty = new NumberProperty(translatable("property.angle_offset_point.distance"), offset);
        this.offsetProperty.addValueChangeListener(distance -> AngleOffsetPoint.this.offset = distance);
        this.nameProperty = new NameComponentProperty(translatable("property.angle_offset_point.name"), this.name);
        this.nameProperty.addValueChangeListener(this::setName);
    }

    @Override
    public Vec2 getPosition() {
        return MathHelper.continueFromAngle(start.getPosition(), angle, offset);
    }

    @Override
    public boolean canMove() {
        return true;
    }

    @Override
    public void move(Vec2 delta) {
        Vec2 start = this.start.getPosition();
        Vec2 position = getPosition();
        Vec2 newPoint = position.add(delta);
        double distanceToLine = MathHelper.distanceToLine(start, position, newPoint);
        if (MathHelper.isPointOnSegment(start, position, MathHelper.perpendicular(newPoint, angle, distanceToLine))) {
            this.offset = MathHelper.perpendicular(newPoint, angle, distanceToLine).distanceTo(start);
        } else if (MathHelper.isPointOnSegment(start, position, MathHelper.perpendicular(newPoint, angle, -distanceToLine))) {
            this.offset = MathHelper.perpendicular(newPoint, angle, -distanceToLine).distanceTo(start);
        }
        Vec2 finalPoint = getPosition();
        this.movementListeners.forEach(i -> i.accept(finalPoint.subtract(position)));
    }

    @Override
    public void move(double dx, double dy) {
        this.move(new Vec2(dx, dy));
    }

    @Override
    protected boolean shouldAutoAssingnName() {
        return true;
    }

    @Override
    public CompoundTag writeNbt(SavingContext context) {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("point", start.getId());
        nbt.putDouble("angle", angle);
        nbt.putDouble("offset", offset);
        nbt.put("name", name.toNbt());
        return nbt;
    }

    public static AngleOffsetPoint readNbt(CompoundTag nbt, LoadingContext context) {
        double angle = nbt.getDouble("angle").getValue();
        double offset = nbt.getDouble("offset").getValue();
        PointProvider point = (PointProvider)context.resolveShape(nbt.getLong("point").longValue());
        boolean shouldRender = NbtUtil.getOptionalBoolean(nbt, "should_render", true);
        if (nbt.containsCompound("name")) {
            NameComponent name = NameComponent.readNbt(nbt.getCompound("name"));
            AngleOffsetPoint output = new AngleOffsetPoint(context.getDrawing(), point, angle, offset, name);
            output.shouldRender = shouldRender;
            return output;
        }
        AngleOffsetPoint output = new AngleOffsetPoint(context.getDrawing(), point, angle, offset);
        output.shouldRender = shouldRender;
        return output;
    }

    @Override
    public ShapeDeserializer<?> getDeserializer() {
        return ShapeData.ANGLE_OFFSET_POINT;
    }

    @Override
    public Collection<Property<?>> getProperties() {
        if (nameOverride != null) return List.of(sourceDescription, angleProperty, offsetProperty);
        return List.of(sourceDescription, angleProperty, offsetProperty, nameProperty);
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getOffset() {
        return offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    public PointProvider getStart() {
        return start;
    }

    public void setStart(PointProvider start) {
        this.start = start;
    }

    public static AngleOffsetPoint fromPoints(PointProvider startPos, PointProvider thisPos) {
        Vec2 a = startPos.getPosition();
        Vec2 b = thisPos.getPosition();
        double angle = (a.y - b.y) / (a.x - b.x);
        double offset = a.distanceTo(b);
        if (MathHelper.roughlyEquals(MathHelper.continueFromTan(a, angle, offset).distanceTo(b), 0)) {
            return new AngleOffsetPoint(startPos.getDrawing(), startPos, angle, offset);
        } else {
            return new AngleOffsetPoint(startPos.getDrawing(), startPos, angle, -offset);
        }
    }
}
