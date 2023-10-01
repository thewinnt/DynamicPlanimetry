package net.thewinnt.planimetry.screen;

import com.badlogic.gdx.Gdx;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.DrawingBoard;

public class EditorScreen extends FlatUIScreen {
    private DrawingBoard board;

    public EditorScreen(DynamicPlanimetry app) {
        super(app);
    }

    @Override
    public void addActorsBelowFps() {
        board = new DrawingBoard(drawer, app::getBoldFont);
        stage.addActor(board);
        stage.setScrollFocus(board);
    }

    @Override
    public void show() {
        super.show();
        board.setPosition(0, 0);
        board.setSize(Gdx.graphics.getWidth() - Gdx.graphics.getHeight() * 0.4f, Gdx.graphics.getHeight());
    }

    @Override public void customRender() {}
    @Override public void addActorsAboveFps() {}
}
