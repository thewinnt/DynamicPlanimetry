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

public class MainMenuScreen extends FlatUIScreen {
    public MainMenuScreen(DynamicPlanimetry app) {
        super(app);
    }

    @Override
    public void addActorsBelowFps() {
        LabelStyle style_title = new LabelStyle(app.getBoldFont(200, Color.BLACK), Color.BLACK);

        Table table = new Table();
        table.setFillParent(true);

        Table buttons = new Table();

        Label title = new Label("Dynamic Planimetry", style_title);
        TextButton create = new TextButton("Создать", style_active);
        TextButton load = new TextButton("Загрузить", style_inactive);
        TextButton go_settings = new TextButton("Настройки", style_inactive);
        TextButton exit = new TextButton("Выход", style_active);

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

    @Override public void customRender() {}
    @Override public void addActorsAboveFps() {}
}
