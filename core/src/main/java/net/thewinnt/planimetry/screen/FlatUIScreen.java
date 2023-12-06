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
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.Notifications;
import net.thewinnt.planimetry.ui.StyleSet;
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
    private float fps_timer;

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

        LabelStyle style_fps = new LabelStyle(app.getBoldFont(DynamicPlanimetry.FPS), getFpsColor());
        this.styles = new StyleSet(drawer, app::getBoldFont);

        if (notifications != null) notifications.dispose();

        fps = new Label(app.last_fps, style_fps);
        mem_usage = new Label("Mem: N/A", style_fps);
        notifications = new Notifications(drawer, app::getBoldFont);

        addActorsBelowFps();

        if (DynamicPlanimetry.DEBUG_MODE) {
            stage.addActor(fps);
            stage.addActor(mem_usage);
        }

        addActorsAboveFps();

        stage.addActor(notifications);
    }

    @Override
    public void show() {
        if (hiddenBefore) initActors();
        hiddenBefore = false;
        Gdx.input.setInputProcessor(stage);
        fps.setPosition(4, 0, Align.bottomLeft);
        mem_usage.setPosition(4, 40, Align.bottomLeft);
        notifications.setPosition(0, 0);
        notifications.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        notifications.updateCaches();
        styles.rebuild();
    }

    @Override
    public void render(float dt) {
        if (Gdx.graphics.getHeight() == 0) return;

        ScreenUtils.clear(DynamicPlanimetry.COLOR_MAIN);
        customRender();
        if (DynamicPlanimetry.DEBUG_MODE) {
            fps_timer += dt;
            if (fps_timer > 0.5f) {
                fps_timer = 0;
                fps.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
            }
            Runtime runtime = Runtime.getRuntime();
            mem_usage.setText("Mem: " + (runtime.totalMemory() - runtime.freeMemory()) / 1048576 + "/" + runtime.totalMemory() / 1048576 + " MB");
        }
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
        return Color.BLACK;
    }
}
