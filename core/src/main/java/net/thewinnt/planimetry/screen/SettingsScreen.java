package net.thewinnt.planimetry.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.Settings;
import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.ui.text.Component;

public class SettingsScreen extends FlatUIScreen {
    public static final CharSequence TITLE = Component.translatable("ui.settings.title");
    public static final CharSequence EXIT = Component.translatable("ui.settings.exit");
    public static final CharSequence DEBUG_SETTINGS = Component.translatable("ui.settings.debug");
    private Screen from;
    private Label title;
    private Label goBack;
    private ScrollPane settings;
    private TextButton debugSettings;

    public SettingsScreen(DynamicPlanimetry app) {
        super(app);
    }

    public void setFrom(Screen screen) {
        this.from = screen;
    }

    @Override
    public void addActorsBelowFps() {
        this.title = new Label(TITLE, this.styles.getLabelStyle(Size.LARGE));
        this.settings = new ScrollPane(null);
        this.goBack = new Label(EXIT, styles.getLabelStyle(Size.MEDIUM));
        this.debugSettings = new TextButton(DEBUG_SETTINGS.toString(), styles.getButtonStyle(Size.MEDIUM, true));

        this.goBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(from);
            }
        });

        this.debugSettings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(DynamicPlanimetry.DEBUG_SETTINGS_SCREEN);
            }
        });

        stage.addActor(title);
        stage.addActor(goBack);
        stage.addActor(settings);
        if (DynamicPlanimetry.isDebug()) {
            stage.addActor(debugSettings);
        }
    }

    @Override
    public void show() {
        super.show();
        this.title.setStyle(styles.getLabelStyle(Size.LARGE));
        this.goBack.setStyle(styles.getLabelStyle(Size.MEDIUM));
        this.debugSettings.setStyle(styles.getButtonStyle(Size.MEDIUM, true));

        this.title.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight(), Align.top);
        this.goBack.setPosition(10, Gdx.graphics.getHeight() - 10, Align.topLeft);
        this.settings.setPosition(10, 10 + Size.MEDIUM.lines(1));
        this.settings.setSize(Gdx.graphics.getWidth() - 20, Gdx.graphics.getHeight() - this.title.getPrefHeight() - 20 - Size.MEDIUM.lines(1));

        this.settings.setActor(DynamicPlanimetry.SETTINGS.getLayout(styles));

        this.debugSettings.setPosition(10, 10);
        this.debugSettings.setSize((Gdx.graphics.getWidth() - 20) / 2f, Size.MEDIUM.lines(1));
    }

    @Override
    public void hide() {
        super.hide();
        Settings.get().toNbt(DynamicPlanimetry.platform().getSettingsFile());
    }

    @Override public void addActorsAboveFps() {}
    @Override public void customRender() {}
}
