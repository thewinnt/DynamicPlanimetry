package net.thewinnt.planimetry.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.*;
import net.thewinnt.planimetry.ui.GuiTheme;
import net.thewinnt.planimetry.ui.drawable.DynamicIcon;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class FlatUIScreen implements Screen {
    protected final DynamicPlanimetry app; // replace with your main class
    protected Stage stage;
    private Texture texture;
    protected ShapeDrawer drawer;
    protected StyleSet styles;

    private Label fps;
    private Label mem_usage;
    private Notifications notifications;
    private ScrollManager scrollManager;
    private float fps_timer;
    private float fps_x = 4;
    private float fps_y = 0;

    private boolean hiddenBefore;

    public FlatUIScreen(DynamicPlanimetry app) {
        this.app = app;
        this.initActors();
    }

    public abstract void customRender();
    public abstract void addActorsBelowFps();
    public abstract void addActorsAboveFps();

    public void initActors() {
        this.stage = new Stage(new ScreenViewport(), new PolygonSpriteBatch());

        Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.drawPixel(0, 0);
        texture = new Texture(pixmap);
        pixmap.dispose();
        TextureRegion region = new TextureRegion(texture, 0, 0, 1, 1);
        this.drawer = new ShapeDrawer(stage.getBatch(), region);

        this.styles = new StyleSet(drawer, app::getBoldFont);

        if (notifications != null) notifications.dispose();

        fps = new Label(app.last_fps, styles.getLabelStyle(Size.MEDIUM));
        mem_usage = new Label("Mem: N/A", styles.getLabelStyle(Size.MEDIUM));
        notifications = new Notifications(drawer, app::getBoldFont);
        scrollManager = new ScrollManager();

        addActorsBelowFps();

        if (DynamicPlanimetry.isDebug()) {
            stage.addActor(fps);
            stage.addActor(mem_usage);
        }

        addActorsAboveFps();

        stage.addActor(notifications);
        stage.addActor(scrollManager);
    }

    @Override
    public void show() {
        if (drawer != null) {
            DynamicIcon.drawer = drawer;
        }
        if (hiddenBefore) initActors();
        hiddenBefore = false;
        Gdx.input.setInputProcessor(stage);
        styles.rebuild();
        fps.setStyle(styles.getLabelStyle(Size.MEDIUM));
        mem_usage.setStyle(styles.getLabelStyle(Size.MEDIUM));
        fps.setSize(fps.getPrefWidth(), fps.getPrefHeight());
        mem_usage.setSize(mem_usage.getPrefWidth(), mem_usage.getPrefHeight());
        fps.setPosition(fps_x, fps_y, Align.bottomLeft);
        mem_usage.setPosition(fps_x, fps_y + fps.getHeight(), Align.bottomLeft);
        notifications.setPosition(0, 0);
        notifications.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        notifications.updateCaches();
    }

    @Override
    public void render(float dt) {
        if (Gdx.graphics.getHeight() == 0) return;

        Color color = GuiTheme.current().main();
        ScreenUtils.clear(color.r, color.g, color.b, color.a, false, true);
        customRender();
        if (DynamicPlanimetry.isDebug()) {
            fps_timer += dt;
            if (fps_timer > 0.5f) {
                fps_timer = 0;
                fps.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
            }
            Runtime runtime = Runtime.getRuntime();
            mem_usage.setText("Mem: " + (runtime.totalMemory() - runtime.freeMemory()) / 1048576 + "/" + runtime.totalMemory() / 1048576 + " MB");
        }
        stage.setScrollFocus(scrollManager);
        stage.act(dt);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        show();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        if (!hiddenBefore) {
            dispose();
        }
        app.last_fps = fps.getText().toString();
        hiddenBefore = true;
    }

    @Override
    public void dispose() {
        texture.dispose();
        stage.dispose();
        notifications.dispose();
    }

    public Color getFpsColor() {
        return GuiTheme.current().textButton();
    }

    public void repositionFps(float x, float y) {
        fps_x = x;
        fps_y = y;

        fps.setPosition(fps_x, fps_y, Align.bottomLeft);
        mem_usage.setPosition(fps_x, fps_y + fps.getHeight(), Align.bottomLeft);
    }

    public ShapeDrawer getDrawer() {
        return drawer;
    }
}
