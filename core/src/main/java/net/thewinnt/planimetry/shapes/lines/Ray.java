package net.thewinnt.planimetry.shapes.lines;

import java.util.function.DoubleFunction;

import com.badlogic.gdx.graphics.Color;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.factories.LineFactory.LineType;
import net.thewinnt.planimetry.definition.line.ray.RayDefinition;
import net.thewinnt.planimetry.definition.line.ray.RayDefinitionType;
import net.thewinnt.planimetry.definition.line.ray.impl.TwoPointRay;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.properties.types.RegistryElementProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Ray extends Line {
    private RayDefinition definition;

    public Ray(Drawing drawing, RayDefinition definition) {
        super(drawing);
        this.definition = definition;
        definition.setSource(this);
        this.dependencies.clear();
        this.dependencies.addAll(this.definition.dependencies());
        this.dependencies.forEach(t -> t.addDepending(this));
    }

    @Override
    public boolean contains(double x, double y) {
        boolean between = MathHelper.roughlyEquals(point1().distanceTo(x, y) + point2().distanceTo(x, y), point1().distanceTo(point2()));
        if (between) return true;
        Vec2 c = new Vec2(x, y);
        return MathHelper.roughlyEquals(point1().distanceTo(point2()) + c.distanceTo(point2()), point1().distanceTo(c));
    }

    @Override
    public boolean contains(Vec2 point) {
        boolean between = MathHelper.roughlyEquals(point1().distanceTo(point) + point2().distanceTo(point), point1().distanceTo(point2()));
        if (between) return true;
        return MathHelper.roughlyEquals(point1().distanceTo(point2()) + point.distanceTo(point2()), point1().distanceTo(point));
    }

    @Override
    public double distanceToMouse(Vec2 point, DrawingBoard board) {
        Vec2 a, b;
        a = this.point1();
        b = this.point2();
        double distance = Math.abs((b.x - a.x)*(a.y - point.y) - (a.x - point.x)*(b.y - a.y)) / a.distanceTo(b);
        double slope = this.getSlope();
        if (!this.contains(MathHelper.perpendicular(point, slope, distance)) && !this.contains(MathHelper.perpendicular(point, slope, -distance))) {
            return Math.min(a.distanceTo(point), b.distanceTo(point));
        } else {
            return distance;
        }
    }

    @Override
    public double distanceToMouse(double x, double y, DrawingBoard board) {
        return distanceToMouse(new Vec2(x, y), board);
    }

    @Override
    public Vec2 point1() {
        return definition.start();
    }

    @Override
    public Vec2 point2() {
        return MathHelper.continueFromAngle(definition.start(), definition.direction(), 1);
    }

    @Override
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        DoubleFunction<Double> formula = compileFormula();
        Color lineColor = this.getColor(selection);
        Vec2 a, b;
        a = this.point1();
        b = this.point2();
        if (a.x == b.x) {
            float startY = board.by(a.y);
            float targHeight = a.y < b.y ? board.getY() + board.getHeight() : board.getY();
            drawer.line(board.bx(a.x), startY, board.bx(b.x), targHeight, lineColor, getThickness(board.getScale()));
        } else if (a.x < b.x) {
            drawer.line(board.bx(a.x), board.by(a.y), board.getX() + board.getWidth(), board.by(formula.apply(board.maxX())), lineColor, getThickness(board.getScale()));
        } else {
            drawer.line(board.getX(), board.by(formula.apply(board.minX())), board.bx(a.x), board.by(a.y), lineColor, getThickness(board.getScale()));
        }
    }

    @Override
    public void rebuildProperties() {
        this.properties.clear();
        RegistryElementProperty<RayDefinitionType<?>> property = new RegistryElementProperty<>(this.definition.type(), Component.translatable(this.getPropertyName("definition")), Registries.RAY_DEFITINION_TYPE);
        this.properties.add(property);
        this.properties.addAll(this.definition.properties());
        property.addValueChangeListener(type -> {
            try {
                this.definition = type.convert(this.definition, drawing);
                this.definition.setSource(this);
                this.rebuildProperties();
                this.dependencies.clear();
                this.dependencies.addAll(this.definition.dependencies());
                this.dependencies.forEach(t -> t.addDepending(this));
                DynamicPlanimetry.getInstance().editorScreen.show();
            } catch (RuntimeException e) {
                property.setValueSilent(type);
            }
        });
    }

    @Override
    public Line convertTo(LineType other) {
        return switch (other) {
            case RAY -> this;
            case INFINITE -> definition.asInfiniteLine(drawing);
            case SEGMENT -> definition.asLineSegment(drawing);
        };
    }

    @Override
    public ShapeDeserializer<?> type() {
        return ShapeData.RAY;
    }

    @Override
    public CompoundTag writeNbt(SavingContext context) {
        CompoundTag nbt = new CompoundTag();
        nbt.put("definition", definition.toNbt(context));
        return nbt;
    }

    public static Ray readNbt(CompoundTag nbt, LoadingContext context) {
        RayDefinition definition = RayDefinition.fromNbt(nbt.getCompoundTag("definition"), context);
        return new Ray(context.getDrawing(), definition);
    }

    @Override
    public LineType getType() {
        return LineType.RAY;
    }

    @Override
    public void replaceShape(Shape old, Shape neo) {
        this.definition.replaceShape(old, neo);
    }

    @Override
    public boolean canMove() {
        return this.definition.canMove();
    }

    @Override
    public Component getName() {
        return Component.translatable(this.type().typeName(), this.definition.getName());
    }

    @Override
    public void move(Vec2 delta) {
        this.definition.move(delta);
    }

    @Override
    public void move(double dx, double dy) {
        this.definition.move(dx, dy);
    }

    public static Ray of(Drawing drawing, PointProvider a, PointProvider b) {
        return new Ray(drawing, new TwoPointRay(a, b));
    }
}
