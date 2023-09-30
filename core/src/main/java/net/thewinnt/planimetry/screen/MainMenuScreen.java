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
import net.thewinnt.planimetry.ui.Notifications;

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
        TextButton create = new TextButton("Create", style_inactive);
        TextButton load = new TextButton("Load", style_inactive);
        TextButton go_settings = new TextButton("Settings", style_inactive);
        TextButton exit = new TextButton("Exit", style_active);
        TextButton add_notification = new TextButton("New notification", style_active);

        create.setSize(30, 70);
        load.setSize(300, 70);
        go_settings.setSize(300, 70);
        exit.setSize(300, 70);
        add_notification.setSize(710, 70);

        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        add_notification.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Notifications.addNotification("New notification", 2000);
            }
        });

        buttons.add(create).uniform().prefWidth(300);
        buttons.add(load).uniform().prefWidth(300).padLeft(5);
        buttons.row();
        buttons.add(go_settings).uniform().prefWidth(300).padTop(5);
        buttons.add(exit).uniform().prefWidth(300).padTop(5).padLeft(5);
        buttons.row();
        buttons.add(add_notification).uniform().padTop(5);

        table.add(title).expandY();
        table.row();
        table.add(buttons).expandY().top();

        stage.addActor(table);
    }

    @Override public void customRender() {}
    @Override public void addActorsAboveFps() {}
}
