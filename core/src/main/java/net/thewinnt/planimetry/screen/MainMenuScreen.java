package net.thewinnt.planimetry.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.Theme;

public class MainMenuScreen extends FlatUIScreen {
    public MainMenuScreen(DynamicPlanimetry app) {
        super(app);
    }

    @Override
    public void addActorsBelowFps() {
        Table table = new Table();
        table.setFillParent(true);

        Table buttons = new Table();
        
        // the font size is 201 because with 200 the letters 'a', 'c' and 'e' don't show up :(
        // painful, i know
        LabelStyle style_title = new LabelStyle(app.getBoldFont(201, Theme.current().textUI()), Theme.current().textUI());
        Label title = new Label("Dynamic Planimetry", style_title);

        TextButtonStyle buttonStyle = styles.createButtonStyle(app.getFont(85, Theme.current().textButton()), true);
        TextButton create = new TextButton(app.getDrawing() == null ? "Создать" : "Продолжить", buttonStyle);
        TextButton load = new TextButton("Загрузить", buttonStyle);
        TextButton go_settings = new TextButton("Настройки", styles.createButtonStyle(app.getFont(85, Theme.current().textButton()), true));
        TextButton exit = new TextButton("Выход", buttonStyle);

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

        go_settings.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                app.settingsScreen.setFrom(MainMenuScreen.this);
                app.setScreen(DynamicPlanimetry.SETTINGS_SCREEN);
            }
        });

        create.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                app.setScreen(DynamicPlanimetry.EDITOR_SCREEN);
            }
        });

        if (app.getDrawing() != null) {
            TextButton newDrawing = new TextButton("Новый рисунок", buttonStyle);
            newDrawing.setSize(605, 70);
            newDrawing.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    app.setDrawing(null, true);
                    app.setScreen(DynamicPlanimetry.EDITOR_SCREEN);
                }
            });
            buttons.add(newDrawing).uniform().prefWidth(605).colspan(2).padBottom(5).row();
        }
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
