package net.thewinnt.planimetry.screen;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.DrawingBoard;

public class EditorScreen extends FlatUIScreen {
    private DrawingBoard board;

    public EditorScreen(DynamicPlanimetry app) {
        super(app);
    }

    @Override
    public void addActorsBelowFps() {
        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(true);

        board = new DrawingBoard(drawer, app::getBoldFont);
        
        table.add(board).expand().fill();

        stage.addActor(table);
        stage.setScrollFocus(board);
    }

    @Override public void customRender() {}
    @Override public void addActorsAboveFps() {}
}
