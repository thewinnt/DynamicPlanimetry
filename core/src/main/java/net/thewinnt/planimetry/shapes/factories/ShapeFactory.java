package net.thewinnt.planimetry.shapes.factories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.point.Point;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.text.Component;

public abstract class ShapeFactory {
    private final List<Shape> addingShapes = new ArrayList<>();
    protected final DrawingBoard board;

    public ShapeFactory(DrawingBoard board) {
        this.board = board;
    }

    /**
     * When a shape is being created, this method is called whenever the user clicks on the screen.
     * <p>
     * This method will never be called after {@code isDone()} returns true.
     * @param event The {@link InputEvent} for the click event
     * @param x the x position of the click <b>on the board</b>
     * @param y the y position of the click <b>on the board</b>
     * @return whether the factory can be removed after this
     */
    public abstract boolean click(InputEvent event, double x, double y);

    /**
     * @return whether the shape has been added and this factory can be safely removed.
     */
    public abstract boolean isDone();

    /**
     * @return the name of the shape being created, used when showing the creating text on the board
     */
    public abstract Component getName();

    /**
     * Executed upon finishing the shape creation, once isDone() turns true.
     */
    public void onFinish() {}

    /**
     * Executed during rendering. Use this to update shape positions in real-time.
     */
    public void onRender(double mx, double my) {}

    /**
     * @return the shapes being added by this factory
     */
    public Collection<Shape> getSuggestedShapes() {
        return addingShapes;
    };

    protected void addShape(Shape shape) {
        this.board.addShape(shape);
        this.addingShapes.add(shape);
    }

    protected void replaceShape(Shape old, Shape neo) {
        this.board.replaceShape(old, neo);
        this.addingShapes.remove(old);
        this.addingShapes.add(neo);
    }

    protected PointProvider getOrCreatePoint(double x, double y) {
        PointProvider p1 = (PointProvider) board.getHoveredShape(Gdx.input.getX(), Gdx.input.getY(), shape -> shape instanceof PointProvider);
        if (p1 != null) {
            return p1;
        } else {
            return new Point(board.getDrawing(), new Vec2(x, y));
        }
    }
}
