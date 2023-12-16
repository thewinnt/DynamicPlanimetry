package net.thewinnt.planimetry.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.StyleSet.Size;

public class SettingsScreen extends FlatUIScreen {
    private Screen from;
    private Label title;
    private Label goBack;
    private ScrollPane settings;

    public SettingsScreen(DynamicPlanimetry app) {
        super(app);
    }

    public void setFrom(Screen screen) {
        this.from = screen;
    }

    @Override
    public void addActorsBelowFps() {
        this.title = new Label("Настройки", this.styles.getLabelStyle(Size.LARGE));
        this.settings = new ScrollPane(null);
        this.goBack = new Label("< Назад", styles.getLabelStyle(Size.MEDIUM));

        this.goBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(from);
            }
        });

        stage.addActor(title);
        stage.addActor(goBack);
        stage.addActor(settings);
    }

    @Override
    public void show() {
        super.show();
        this.title.setStyle(styles.getLabelStyle(Size.LARGE));
        this.goBack.setStyle(styles.getLabelStyle(Size.MEDIUM));

        this.title.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight(), Align.top);
        this.goBack.setPosition(10, Gdx.graphics.getHeight() - 10, Align.topLeft);
        this.settings.setPosition(10, 10);
        this.settings.setSize(Gdx.graphics.getWidth() - 20, Gdx.graphics.getHeight() - this.title.getPrefHeight());

        this.settings.setActor(DynamicPlanimetry.SETTINGS.getLayout(styles));
    }
    
    @Override public void addActorsAboveFps() {}
    @Override public void customRender() {}
}
