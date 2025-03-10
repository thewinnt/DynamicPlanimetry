package net.thewinnt.planimetry.shapes.lines;

import java.util.Optional;
import java.util.function.DoubleFunction;

import com.badlogic.gdx.graphics.Color;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.Settings;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.factories.LineFactory.LineType;
import net.thewinnt.planimetry.shapes.lines.definition.infinite.InfiniteLineDefinition;
import net.thewinnt.planimetry.shapes.lines.definition.infinite.InfiniteLineType;
import net.thewinnt.planimetry.shapes.lines.definition.infinite.TwoPointInfiniteLine;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.properties.types.RegistryElementProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

/** An infinite straight line, built using two points. */
public class InfiniteLine extends Line {
    private InfiniteLineDefinition definition;

    public InfiniteLine(Drawing drawing, InfiniteLineDefinition definition) {
        super(drawing);
        this.definition = definition;
        if (this.definition != null) {
            this.definition.setSource(this);
        }
    }

    @Override
    public boolean contains(Vec2 point) {
        return point1().distanceTo(point) + point2().distanceTo(point) - point1().distanceTo(point2()) <= Math.pow(2, Settings.get().getMathPrecision());
    }

    @Override
    public boolean contains(double x, double y) {
        return point1().distanceTo(x, y) + point2().distanceTo(x, y) - point1().distanceTo(point2()) <= Math.pow(2, Settings.get().getMathPrecision());
    }

    @Override
    public double distanceToMouse(Vec2 point, DrawingBoard board) {
        Vec2 a = this.point1();
        Vec2 b = this.point2();
        return Math.abs((b.x - a.x)*(a.y - point.y) - (a.x - point.x)*(b.y - a.y)) / a.distanceTo(b);
    }

    @Override
    public double distanceToMouse(double x, double y, DrawingBoard board) {
        Vec2 a = this.point1();
        Vec2 b = this.point2();
        return Math.abs((b.x - a.x)*(a.y - y) - (a.x - x)*(b.y - a.y)) / a.distanceTo(b);
    }

    @Override
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        DoubleFunction<Double> formula = this.compileFormula();
        Color lineColor = this.getColor(selection);
        Vec2 a = this.point1();
        Vec2 b = this.point2();
        if (MathHelper.roughlyEquals(a.x, b.x)) {
            drawer.line(board.bx(a.x), board.getY(), board.bx(b.x), board.getY() + board.getHeight(), lineColor, getThickness(board.getScale()));
        } else {
            drawer.line(board.getX(), board.by(formula.apply(board.minX())), board.getX() + board.getWidth(), board.by(formula.apply(board.maxX())), lineColor, getThickness(board.getScale()));
        }
    }

    @Override
    public String getTypeName() {
        return "shape.infinite_line";
    }

    @Override
    public Optional<Vec2> intersection(Line other) {
        return switch (other.getType()) {
            case INFINITE -> intersectInf(other);
            case RAY, SEGMENT -> intersectInf(other).filter(other::contains);
        };
    }

    @Override
    public Vec2 point1() {
        return definition.point1();
    }

    @Override
    public Vec2 point2() {
        return definition.point2();
    }

    @Override
    public boolean canMove() {
        return true;
    }

    @Override
    public Component getName() {
        return definition.getName();
    }

    @Override
    public void move(Vec2 delta) {
        this.definition.move(delta);
    }

    @Override
    public void move(double dx, double dy) {
        this.definition.move(dx, dy);
    }

    @Override
    public void replaceShape(Shape old, Shape neo) {
        this.definition.replaceShape(old, neo);
    }

    @Override
    public void rebuildProperties() {
        this.properties.clear();
        RegistryElementProperty<InfiniteLineType<?>> definitionType = new RegistryElementProperty<>(this.definition.type(), Component.translatable(this.getPropertyName("definition")), Registries.INFINITE_LINE_DEFINITION_TYPE);
        this.properties.add(definitionType);
        this.properties.addAll(definition.properties());
        definitionType.addValueChangeListener(type -> {
            try {
                InfiniteLine.this.definition = type.convert(InfiniteLine.this.definition, drawing);
                InfiniteLine.this.definition.setSource(InfiniteLine.this);
                InfiniteLine.this.rebuildProperties();
            } catch (RuntimeException e) {
                definitionType.setValueSilent(type);
            }
        });
    }

    @Override
    protected CompoundTag writeNbt(SavingContext context) {
        CompoundTag nbt = new CompoundTag();
        nbt.put("definition", definition.toNbt(context));
        return nbt;
    }

    @Override
    public Line convertTo(LineType other) {
        return switch (other) {
            case INFINITE -> this;
            case RAY -> definition.asRay(drawing);
            case SEGMENT -> definition.asLineSegment(drawing);
        };
    }

    @Override
    public ShapeDeserializer<? extends InfiniteLine> type() {
        return ShapeData.INFINITE_LINE;
    }

    public static InfiniteLine readNbt(CompoundTag nbt, LoadingContext context) {
        CompoundTag definition = nbt.getCompoundTag("definition");
        return new InfiniteLine(context.getDrawing(), InfiniteLineDefinition.fromNbt(definition, context));
    }

    @Override
    public LineType getType() {
        return LineType.INFINITE;
    }

    public static InfiniteLine of(Drawing drawing, PointProvider a, PointProvider b) {
        return new InfiniteLine(drawing, new TwoPointInfiniteLine(a, b));
    }
}
