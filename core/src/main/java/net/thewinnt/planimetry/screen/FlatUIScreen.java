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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.Notifications;
import net.thewinnt.planimetry.ui.drawable.RectangleDrawable;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class FlatUIScreen implements Screen {
    protected final DynamicPlanimetry app; // replace with your main class
    protected Stage stage;
    private Texture texture;
    protected ShapeDrawer drawer;

    protected TextButtonStyle style_inactive;
    protected TextButtonStyle style_active;

    private Label fps;
    private Label mem_usage;
    private Notifications notifications;
    private float fps_timer;
    
    protected RectangleDrawable normal;
    protected RectangleDrawable pressed;
    protected RectangleDrawable over;
    protected RectangleDrawable disabled;

    private boolean hiddenBefore;

    public FlatUIScreen(DynamicPlanimetry app) {
        this.app = app;
        this.initActors();
    }

    public abstract void customRender();
    public abstract void addActorsBelowFps();
    public abstract void addActorsAboveFps();

    public void initActors() {
        stage = new Stage(new ScreenViewport(), new PolygonSpriteBatch());

        Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.drawPixel(0, 0);
        texture = new Texture(pixmap);
        pixmap.dispose();
        TextureRegion region = new TextureRegion(texture, 0, 0, 1, 1);
        drawer = new ShapeDrawer(stage.getBatch(), region);
        normal = new RectangleDrawable(drawer).withColors(DynamicPlanimetry.COLOR_BUTTON, Color.BLACK);
        pressed = new RectangleDrawable(drawer).withColors(DynamicPlanimetry.COLOR_PRESSED, Color.BLACK);
        over = new RectangleDrawable(drawer).withColors(DynamicPlanimetry.COLOR_MAIN, Color.BLACK);
        disabled = new RectangleDrawable(drawer).withColors(DynamicPlanimetry.COLOR_PRESSED, DynamicPlanimetry.COLOR_INACTIVE);
        style_active = new TextButtonStyle(normal, pressed, normal, app.getFont(DynamicPlanimetry.BUTTON_ACTIVE));
        style_active.over = over;
        style_active.checkedOver = over;
        style_inactive = new TextButtonStyle(disabled, disabled, disabled, app.getFont(DynamicPlanimetry.BUTTON_INACTIVE));
        LabelStyle style_fps = new LabelStyle(app.getBoldFont(DynamicPlanimetry.FPS), getFpsColor());

        fps = new Label(app.last_fps, style_fps);
        mem_usage = new Label("Mem: N/A", style_fps);
        notifications = new Notifications(drawer, app::getBoldFont);

        addActorsBelowFps();

        stage.addActor(fps);
        stage.addActor(mem_usage);

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
    }

    @Override
    public void render(float dt) {
        if (Gdx.graphics.getHeight() == 0) return;

        ScreenUtils.clear(DynamicPlanimetry.COLOR_MAIN);
        customRender();
        fps_timer += dt;
        if (fps_timer > 0.5f) {
            fps_timer = 0;
            fps.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
        }
        Runtime runtime = Runtime.getRuntime();
        mem_usage.setText("Mem: " + (runtime.totalMemory() - runtime.freeMemory()) / 1048576 + "/" + runtime.totalMemory() / 1048576 + " MB");
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
        app.last_fps = fps.getText().toString();
        hiddenBefore = true;
        dispose();
    }

    @Override
    public void dispose() {
        texture.dispose();
        stage.dispose();
    }

    public Color getFpsColor() {
        return Color.BLACK;
    }
}
