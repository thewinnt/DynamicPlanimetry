package net.thewinnt.planimetry.shapes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.badlogic.gdx.graphics.Color;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.LongTag;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.NbtUtil;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.definition.point.placement.CirclePlacement;
import net.thewinnt.planimetry.math.AABB;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.SegmentLike;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.factories.CircleTangentFactory;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.functions.BasicNamedFunction;
import net.thewinnt.planimetry.ui.functions.Function;
import net.thewinnt.planimetry.ui.properties.PropertyHelper;
import net.thewinnt.planimetry.ui.properties.types.BooleanProperty;
import net.thewinnt.planimetry.ui.properties.types.NumberProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.NameComponent;
import net.thewinnt.planimetry.util.FontProvider;
import net.thewinnt.planimetry.value.type.AngleValue;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class Circle extends Shape {
    public PointProvider center;
    private boolean keepRadius = false;
    public Supplier<Double> radius;
    private PointProvider radiusPoint;
    private Consumer<Vec2> radiusMove;

    public Circle(Drawing drawing, PointProvider center, double radius) {
        super(drawing);
        this.center = center;
        this.radius = () -> radius;
        center.addDepending(this);
        this.addDependency(center);
    }

    public Circle(Drawing drawing, PointProvider center, PointProvider radius, boolean keepRadius) {
        super(drawing);
        this.center = center;
        this.radius = () -> radius.getPosition().distanceTo(center.getPosition());
        this.keepRadius = keepRadius;
        this.setRadiusPoint(radius);
        center.addDepending(this);
        radius.addDepending(this);
        this.addDependency(center);
        this.addDependency(radius);
    }

    @Override
    public boolean contains(double x, double y) {
        return MathHelper.roughlyEquals(center.getPosition().distanceTo(x, y), this.radius.get());
    }

    @Override
    public boolean contains(Vec2 point) {
        return MathHelper.roughlyEquals(center.getPosition().distanceTo(point), this.radius.get());
    }

    @Override
    public double distanceToMouse(Vec2 point, DrawingBoard board) {
        return Math.abs(center.getPosition().distanceTo(point) - radius.get());
    }

    @Override
    public double distanceToMouse(double x, double y, DrawingBoard board) {
        return Math.abs(center.getPosition().distanceTo(x, y) - radius.get());
    }

    @Override
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        Vec2 center = this.center.getPosition();
        Color lineColor = this.getColor(selection);
        drawer.setColor(lineColor);
        drawer.circle(board.bx(center.x), board.by(center.y), (float)(radius.get() * board.getScale()), getThickness(board.getScale()));
        if (selection == SelectionStatus.SELECTED) {
            if (!this.drawing.hasShape(this.center) || !this.center.shouldRender()) {
                this.center.render(drawer, SelectionStatus.NONE, font, board);
            }
            if (this.radiusPoint != null && (!this.drawing.hasShape(this.radiusPoint) || !this.radiusPoint.shouldRender())) {
                this.radiusPoint.render(drawer, SelectionStatus.NONE, font, board);
            }
        }
    }

    public void setRadiusPoint(PointProvider point) {
        if (this.radiusPoint != null) this.center.removeMovementListener(this.radiusMove);
        if (point != null) {
            this.radiusMove = point::move;
            if (this.keepRadius) this.center.addMovementListener(radiusMove);
            this.radius = () -> point.getPosition().distanceTo(center.getPosition());
        } else {
            double radius = this.radius.get();
            this.radius = () -> radius;
            this.radiusMove = null;
        }
        this.radiusPoint = point;
    }

    public PointProvider getRadiusPoint() {
        return radiusPoint;
    }

    public boolean getKeepRadius() {
        return keepRadius;
    }

    public double getRadius() {
        return this.radius.get();
    }

    public void setKeepRadius(boolean keepRadius) {
        if (this.keepRadius != keepRadius && this.radiusPoint != null) {
            if (keepRadius) {
                this.center.addMovementListener(this.radiusMove);
            } else {
                this.center.removeMovementListener(this.radiusMove);
            }
        }
        this.keepRadius = keepRadius;
    }

    @Override
    public void replaceShape(Shape old, Shape neo) {
        if (old == center) {
            this.center.removeMovementListener(radiusMove);
            this.center = (PointProvider)neo;
            this.center.addMovementListener(radiusMove);
        } else if (old == radiusPoint) {
            setRadiusPoint(radiusPoint);
        }
    }

    @Override
    public void rebuildProperties() {
        this.properties.clear();
        if (radiusPoint != null) {
            BooleanProperty keep = new BooleanProperty(Component.translatable(getPropertyName("keep_radius")), keepRadius);
            keep.addValueChangeListener(this::setKeepRadius);
            this.properties.add(PropertyHelper.swappablePoint(center, t -> center = t, List.of(radiusPoint), true, getPropertyName("group.center_point")));
            this.properties.add(PropertyHelper.swappablePoint(radiusPoint, this::setRadiusPoint, List.of(center), true, getPropertyName("group.radius_point")));
            this.properties.add(keep);
        } else {
            NumberProperty radius = new NumberProperty(Component.translatable(getPropertyName("radius")), this.radius.get());
            radius.addValueChangeListener(r -> this.radius = () -> r);
            this.properties.add(PropertyHelper.swappablePoint(center, t -> center = t, List.of(), true, getPropertyName("group.center_point")));
            this.properties.add(radius);
        }
    }

    @Override
    public Collection<Function<?>> getFunctions() {
        Collection<Function<?>> functions = new ArrayList<>();
        DrawingBoard board = DynamicPlanimetry.getInstance().editorScreen.getBoard();
         if (this.radiusPoint != null) {
             functions.add(new BasicNamedFunction<>(drawing, this, s -> {
                 PointProvider old = radiusPoint;
                 double angle = MathHelper.angleTo(this.center.getPosition(), old.getPosition());
                 setRadiusPoint(null); // remove the radius point before it gets replaced
                 old.setPlacement(new CirclePlacement(this, new AngleValue(angle)));
             }, Component.translatable("function.circle.disconnect_radius"), Component.translatable("function.circle.disconnect_radius.action")));
         }
        functions.add(new BasicNamedFunction<>(drawing, this, s -> board.startCreation(new CircleTangentFactory(board, Circle.this)), Component.translatable("function.circle.create_tangent"), Component.translatable("function.circle.create_tangent.action")));
        functions.addAll(super.getFunctions());
        return functions;
    }

    @Override
    public Component getName() {
        if (nameOverride != null) return nameOverride;
        return Component.translatable(getTypeName(), this.center.getNameComponent());
    }

    @Override
    public ShapeDeserializer<?> type() {
        return ShapeData.CIRCLE;
    }

    @Override
    public CompoundTag writeNbt(SavingContext context) {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("center", this.center.getId());
        context.addShape(this.center);
        if (this.radiusPoint == null) {
            nbt.putDouble("radius", this.radius.get());
        } else {
            nbt.putLong("radius", this.radiusPoint.getId());
            context.addShape(this.radiusPoint);
            NbtUtil.writeBoolean(nbt, "keep_radius", keepRadius);
        }
        return nbt;
    }

    public static Circle readNbt(CompoundTag nbt, LoadingContext context) {
        PointProvider center = context.resolveShape(nbt.getLong("center"));
        if (nbt.containsKey("radius") && nbt.get("radius") instanceof LongTag longTag) {
            PointProvider radius = context.resolveShape(longTag.asLong());
            boolean keepRadius = NbtUtil.getBoolean(nbt, "keep_radius");
            return new Circle(context.getDrawing(), center, radius, keepRadius);
        } else {
            double radius = nbt.getDouble("radius");
            return new Circle(context.getDrawing(), center, radius);
        }
    }

    @Override
    public void move(Vec2 delta) {
        this.center.move(delta);
        if (keepRadius && radiusPoint != null) radiusPoint.move(delta);
    }

    @Override
    public void move(double dx, double dy) {
        this.center.move(dx, dy);
        if (keepRadius && radiusPoint != null) radiusPoint.move(dx, dy);
    }

    @Override
    public boolean canMove() {
        return true;
    }

    @Override
    public boolean intersects(AABB aabb) {
        for (SegmentLike i : aabb.asLineSegments()) {
            if (MathHelper.distanceToLine(i.point1(), i.point2(), center.getPosition()) <= radius.get()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Collection<Vec2> intersections(Shape other) {
        // TODO Auto-generated method stub
        return List.of();
    }

    @Override
    public Collection<Vec2> intersections(SegmentLike other) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<SegmentLike> asSegments() {
        return List.of();
    }
}
