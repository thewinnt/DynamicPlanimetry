package net.thewinnt.planimetry.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.StyleSet.Size;

public class MainMenuScreen extends FlatUIScreen {
    private StyleSet stylesThin;

    public MainMenuScreen(DynamicPlanimetry app) {
        super(app);
    }

    @Override
    public void addActorsBelowFps() {
        Table table = new Table();
        table.setFillParent(true);

        Table buttons = new Table();
        
        stylesThin = new StyleSet(drawer, app::getFont);
        LabelStyle style_title = new LabelStyle(app.getBoldFont(200, Color.BLACK), Color.BLACK);
        Label title = new Label("Dynamic Planimetry", style_title);

        TextButton create = new TextButton(app.getDrawing() == null ? "Создать" : "Продолжить", stylesThin.getButtonStyle(Size.VERY_LARGE, true));
        TextButton load = new TextButton("Загрузить", stylesThin.getButtonStyle(Size.VERY_LARGE, true));
        TextButton go_settings = new TextButton("Настройки", stylesThin.getButtonStyle(Size.VERY_LARGE, false));
        TextButton exit = new TextButton("Выход", stylesThin.getButtonStyle(Size.VERY_LARGE, true));

        create.setSize(30, 70);
        load.setSize(300, 70);
        go_settings.setSize(300, 70);
        exit.setSize(300, 70);

        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        load.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // app.preloadDrawings(Gdx.files.getLocalStoragePath() + "drawings");
                app.setScreen(DynamicPlanimetry.FILE_SELECTION_SCREEN);
            }
        });

        create.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                app.setScreen(DynamicPlanimetry.EDITOR_SCREEN);
            }
        });

        buttons.add(create).uniform().prefWidth(300);
        buttons.add(load).uniform().prefWidth(300).padLeft(5);
        buttons.row();
        buttons.add(go_settings).uniform().prefWidth(300).padTop(5);
        buttons.add(exit).uniform().prefWidth(300).padTop(5).padLeft(5);

        table.add(title).expandY();
        table.row();
        table.add(buttons).expandY().top();

        stage.addActor(table);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override public void customRender() {}
    @Override public void addActorsAboveFps() {}
}
