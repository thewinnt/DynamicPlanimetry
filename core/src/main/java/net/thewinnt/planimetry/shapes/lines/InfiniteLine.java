package net.thewinnt.planimetry.shapes.lines;

import java.util.function.DoubleFunction;

import com.badlogic.gdx.graphics.Color;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.shapes.point.PointReference;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

/** An infinite straight line, built using two points. */
public class InfiniteLine extends Line {
    public InfiniteLine(Drawing drawing, PointReference a, PointReference b) {
        super(drawing, a, b);
    }

    public InfiniteLine(Drawing drawing, PointProvider a, PointProvider b) {
        super(drawing, a, b);
    }

    @Override
    public boolean contains(Vec2 point) {
        return a.getPosition().distanceTo(point) + b.getPosition().distanceTo(point) - a.getPosition().distanceTo(b.getPosition()) <= Math.pow(2, -23);
    }

    @Override
    public boolean contains(double x, double y) {
        return a.getPosition().distanceTo(x, y) + b.getPosition().distanceTo(x, y) - a.getPosition().distanceTo(b.getPosition()) <= Math.pow(2, -23);
    }

    @Override
    public double distanceToMouse(Vec2 point, DrawingBoard board) {
        Vec2 a = this.a.getPosition();
        Vec2 b = this.b.getPosition();
        return Math.abs((b.x - a.x)*(a.y - point.y) - (a.x - point.x)*(b.y - a.y)) / a.distanceTo(b);
    }

    @Override
    public double distanceToMouse(double x, double y, DrawingBoard board) {
        Vec2 a = this.a.getPosition();
        Vec2 b = this.b.getPosition();
        return Math.abs((b.x - a.x)*(a.y - y) - (a.x - x)*(b.y - a.y)) / a.distanceTo(b);
    }

    @Override
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        DoubleFunction<Double> formula = compileFormula();
        Color lineColor = switch (selection) {
            default -> DynamicPlanimetry.COLOR_SHAPE;
            case HOVERED -> DynamicPlanimetry.COLOR_SHAPE_HOVER;
            case SELECTED -> DynamicPlanimetry.COLOR_SHAPE_SELECTED;
        };
        Vec2 a = this.a.getPosition();
        Vec2 b = this.b.getPosition();
        if (a.x == b.x) {
            drawer.line(board.bx(a.x), board.getY(), board.bx(b.x), board.getY() + board.getHeight(), lineColor, getThickness(board.getScale()));
        } else {
            drawer.line(board.getX(), board.by(formula.apply(board.minX())), board.getX() + board.getWidth(), board.by(formula.apply(board.maxX())), lineColor, getThickness(board.getScale()));
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

    @Override
    public String getTypeName() {
        return "Прямая";
    }

    @Override
    public ShapeDeserializer<InfiniteLine> getDeserializer() {
        return ShapeData.INFINITE_LINE;
    }

    public static InfiniteLine readNbt(CompoundTag nbt, LoadingContext context) {
        PointReference a = (PointReference)context.resolveShape(nbt.getLong("a").getValue());
        PointReference b = (PointReference)context.resolveShape(nbt.getLong("b").getValue());
        return new InfiniteLine(context.getDrawing(), a, b);
    }
}
