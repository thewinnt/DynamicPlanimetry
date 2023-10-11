package net.thewinnt.planimetry.shapes.factories;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

import net.thewinnt.planimetry.ui.DrawingBoard;

public abstract class ShapeFactory {
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
}
