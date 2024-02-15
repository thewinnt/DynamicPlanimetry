package net.thewinnt.planimetry.shapes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.badlogic.gdx.graphics.Color;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.NbtUtil;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.factories.CircleTangentFactory;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.Theme;
import net.thewinnt.planimetry.ui.functions.BasicNamedFunction;
import net.thewinnt.planimetry.ui.functions.Function;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.types.BooleanProperty;
import net.thewinnt.planimetry.ui.properties.types.EnclosingProperty;
import net.thewinnt.planimetry.ui.properties.types.NumberProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Circle extends Shape {
    public final PointProvider center;
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
        Color lineColor = switch (selection) {
            default -> Theme.current().shape();
            case HOVERED -> Theme.current().shapeHovered();
            case SELECTED -> Theme.current().shapeSelected();
        };
        drawer.setColor(lineColor);
        drawer.circle(board.bx(center.x), board.by(center.y), (float)(radius.get() * board.getScale()), getThickness(board.getScale()));
        if (selection == SelectionStatus.SELECTED) {
            if (!this.drawing.hasShape(this.center)) {
                this.center.render(drawer, SelectionStatus.NONE, font, board);
            }
            if (!this.drawing.hasShape(this.radiusPoint)) {
                this.radiusPoint.render(drawer, SelectionStatus.NONE, font, board);
            }
        }
    }

    public void setRadiusPoint(PointProvider point) {
        if (this.radiusPoint != null) this.center.removeMovementListener(this.radiusMove);
        this.radiusMove = point::move;
        if (this.keepRadius) this.center.addMovementListener(radiusMove);
        this.radiusPoint = point;
        this.radius = () -> point.getPosition().distanceTo(center.getPosition());
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
    public Collection<Property<?>> getProperties() {
        if (radiusPoint != null) {
            BooleanProperty keep = new BooleanProperty(Component.literal("Сохранять радиус"), keepRadius);
            keep.addValueChangeListener(result -> Circle.this.setKeepRadius(result));
            return List.of(
                new EnclosingProperty(Component.literal("Центр"), this.center.getProperties()),
                new EnclosingProperty(Component.literal("Точка радиуса"), this.radiusPoint.getProperties()),
                keep
            );
        } else {
            NumberProperty radius = new NumberProperty(Component.literal("Радиус"), this.radius.get());
            radius.addValueChangeListener(r -> Circle.this.radius = () -> r);
            return List.of(new EnclosingProperty(Component.literal("Центр"), this.center.getProperties()), radius);
        }
    }

    @Override
    public Collection<Function<?>> getFunctions() {
        Collection<Function<?>> functions = new ArrayList<>();
        DrawingBoard board = DynamicPlanimetry.getInstance().editorScreen.getBoard();
        if (!this.drawing.hasShape(this.radiusPoint)) {
            functions.add(new BasicNamedFunction<>(drawing, this, s -> drawing.addShape(radiusPoint), Component.literal("Отметить точку радиуса"), Component.literal("Отметить")));
        }
        functions.add(new BasicNamedFunction<>(drawing, this, s -> board.startCreation(new CircleTangentFactory(board, Circle.this)), Component.literal("Построить касательную"), Component.literal("Построить")));
        functions.addAll(super.getFunctions());
        return functions;
    }

    @Override
    public Component getName() {
        if (nameOverride != null) return nameOverride;
        return Component.of(Component.literal(getTypeName()), this.center.getNameComponent());
    }

    @Override
    public String getTypeName() {
        return "Окружность ";
    }

    @Override
    public ShapeDeserializer<?> getDeserializer() {
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
        PointProvider center = (PointProvider)context.resolveShape(nbt.getLong("center").getValue());
        if (nbt.containsLong("radius")) {
            PointProvider radius = (PointProvider)context.resolveShape(nbt.getLong("radius").getValue());
            boolean keepRadius = NbtUtil.getBoolean(nbt, "keep_radius");
            return new Circle(context.getDrawing(), center, radius, keepRadius);
        } else {
            double radius = nbt.getDouble("radius").getValue();
            return new Circle(context.getDrawing(), center, radius);
        }
    }

    @Override
    public void move(Vec2 delta) {
        this.center.move(delta);
        if (!keepRadius && radiusPoint != null) radiusPoint.move(delta);
    }

    @Override
    public void move(double dx, double dy) {
        this.center.move(dx, dy);
        if (!keepRadius && radiusPoint != null) radiusPoint.move(dx, dy);
    }

    @Override
    public boolean canMove() {
        return true;
    }
}
