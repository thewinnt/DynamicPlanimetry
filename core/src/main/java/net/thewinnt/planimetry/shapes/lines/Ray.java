package net.thewinnt.planimetry.shapes.lines;

import java.util.Collection;
import java.util.Optional;
import java.util.function.DoubleFunction;
import java.util.stream.Stream;

import com.badlogic.gdx.graphics.Color;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.factories.LineFactory.LineType;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.shapes.point.PointReference;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.Theme;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.types.BooleanProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Ray extends Line {
    private boolean startFromA = true;
    private BooleanProperty startProperty = new BooleanProperty(Component.translatable("property.ray.start_from_a"), startFromA);
    
    public Ray(Drawing drawing, PointProvider a, PointProvider b) {
        super(drawing, a, b);
        startProperty.addValueChangeListener(value -> startFromA = value);
    }

    @Override
    public boolean contains(double x, double y) {
        boolean between = a.getPosition().distanceTo(x, y) + b.getPosition().distanceTo(x, y) - a.getPosition().distanceTo(b.getPosition()) < Math.pow(2, -23);
        if (between) return true;
        if (startFromA) {
            Vec2 c = new Vec2(x, y);
            return a.getPosition().distanceTo(b.getPosition()) + c.distanceTo(b.getPosition()) - a.getPosition().distanceTo(c) < Math.pow(2, -23);
        } else {
            Vec2 c = new Vec2(x, y);
            return b.getPosition().distanceTo(a.getPosition()) + c.distanceTo(a.getPosition()) - b.getPosition().distanceTo(c) < Math.pow(2, -23);
        }
    }

    @Override
    public boolean contains(Vec2 point) {
        boolean between = a.getPosition().distanceTo(point) + b.getPosition().distanceTo(point) - a.getPosition().distanceTo(b.getPosition()) < Math.pow(2, -23);;
        if (between) return true;
        if (startFromA) {
            return a.getPosition().distanceTo(b.getPosition()) + point.distanceTo(b.getPosition()) - a.getPosition().distanceTo(point) < Math.pow(2, -23);
        } else {
            return b.getPosition().distanceTo(a.getPosition()) + point.distanceTo(a.getPosition()) - b.getPosition().distanceTo(point) < Math.pow(2, -23);
        }
    }

    @Override
    public double distanceToMouse(Vec2 point, DrawingBoard board) {
        Vec2 a, b;
        if (startFromA) {
            a = this.a.getPosition();
            b = this.b.getPosition();
        } else {
            a = this.b.getPosition();
            b = this.a.getPosition();
        }
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
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        DoubleFunction<Double> formula = compileFormula();
        Color lineColor = switch (selection) {
            default -> Theme.current().shape();
            case HOVERED -> Theme.current().shapeHovered();
            case SELECTED -> Theme.current().shapeSelected();
        };
        Vec2 a, b;
        if (startFromA) {
            a = this.a.getPosition();
            b = this.b.getPosition();
        } else {
            a = this.b.getPosition();
            b = this.a.getPosition();
        }
        if (a.x == b.x) {
            float startY = board.by(a.y);
            float targHeight = a.y < b.y ? board.getY() + board.getHeight() : board.getY();
            drawer.line(board.bx(a.x), startY, board.bx(b.x), targHeight, lineColor, getThickness(board.getScale()));
        } else if (a.x < b.x) {
            drawer.line(board.bx(a.x), board.by(a.y), board.getX() + board.getWidth(), board.by(formula.apply(board.maxX())), lineColor, getThickness(board.getScale()));
        } else {
            drawer.line(board.getX(), board.by(formula.apply(board.minX())), board.bx(a.x), board.by(a.y), lineColor, getThickness(board.getScale()));
        }
        if (selection == SelectionStatus.SELECTED) {
            if (!board.getShapes().contains(this.a)) {
                this.a.render(drawer, SelectionStatus.NONE, font, board);
            }
            if (!board.getShapes().contains(this.b)) {
                this.b.render(drawer, SelectionStatus.NONE, font, board);
            }
        }
    }

    public void invert() {
        this.startFromA = !this.startFromA;
    }

    @Override
    public Collection<Property<?>> getProperties() {
        return Stream.concat(super.getProperties().stream(), Stream.of(startProperty)).toList();
    }

    @Override
    public String getTypeName() {
        return "shape.ray";
    }

    @Override
    public ShapeDeserializer<?> getDeserializer() {
        return ShapeData.RAY;
    }

    @Override
    public CompoundTag writeNbt(SavingContext context) {
        CompoundTag nbt = super.writeNbt(context);
        nbt.putByte("start_from_a", startFromA ? (byte)1 : (byte)0);
        return nbt;
    }

    public static Ray readNbt(CompoundTag nbt, LoadingContext context) {
        PointReference a = (PointReference)context.resolveShape(nbt.getLong("a").getValue());
        PointReference b = (PointReference)context.resolveShape(nbt.getLong("b").getValue());
        boolean startFromA = nbt.getByte("start_from_a").getValue() > 0;
        Ray output = new Ray(context.getDrawing(), a, b);
        output.startFromA = startFromA;
        return output;
    }

    @Override
    public LineType getType() {
        return LineType.RAY;
    }
}
