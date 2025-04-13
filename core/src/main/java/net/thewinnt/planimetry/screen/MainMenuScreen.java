package net.thewinnt.planimetry.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.ui.ComponentLabel;
import net.thewinnt.planimetry.ui.GuiHelper;
import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.Theme;
import net.thewinnt.planimetry.ui.Window;
import net.thewinnt.planimetry.ui.text.Component;

public class MainMenuScreen extends FlatUIScreen {
    public static final CharSequence CREATE_DRAWING = Component.translatable("ui.main.create_drawing");
    public static final CharSequence NEW_DRAWING = Component.translatable("ui.main.new_drawing");
    public static final CharSequence LOAD = Component.translatable("ui.main.load");
    public static final CharSequence SETTINGS = Component.translatable("ui.main.settings");
    public static final CharSequence EXIT = Component.translatable("ui.main.exit");
    public static final CharSequence CONTINUE = Component.translatable("ui.main.continue");
    private Label title;
    private Label version;
    private TextButton load;
    private TextButton go_settings;
    private TextButton exit;
    private TextButton newDrawing;
    private TextButton create;

    public MainMenuScreen(DynamicPlanimetry app) {
        super(app);
    }

    @Override
    public void addActorsBelowFps() {
        Table table = new Table();
        table.setFillParent(true);

        Table buttons = new Table();

        title = new Label("Dynamic Planimetry", styles.getLabelStyle(Size.EXTREMELY_LARGE));
        version = new Label(Component.literal(DynamicPlanimetry.APP_VERSION), styles.getLabelStyle(Size.MEDIUM));

        TextButtonStyle buttonStyle = new TextButtonStyle(styles.normal, styles.pressed, styles.normal, app.getFont(styles.getHeight() / Size.VERY_LARGE.getFactor(), Theme.current().textButton()));
        buttonStyle.over = styles.over;
        buttonStyle.checkedOver = styles.over;
        create = new TextButton(app.getDrawing() == null ? CREATE_DRAWING.toString() : NEW_DRAWING.toString(), buttonStyle);
        load = new TextButton(LOAD.toString(), buttonStyle);
        go_settings = new TextButton(SETTINGS.toString(), buttonStyle);
        exit = new TextButton(EXIT.toString(), buttonStyle);

        create.setSize(30, 70);
        load.setSize(300, 70);
        go_settings.setSize(300, 70);
        exit.setSize(300, 70);

        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Drawing current = app.getDrawing();
                if (current != null && current.isUnsaved()) {
                    stage.addActor(createSaveWindow(styles, stage, Gdx.app::exit));
                } else {
                    Gdx.app.exit();
                }
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
                Drawing current = app.getDrawing();
                if (current != null && current.isUnsaved()) {
                    stage.addActor(createSaveWindow(styles, stage));
                } else {
                    app.setDrawing(null, true);
                    app.setScreen(DynamicPlanimetry.EDITOR_SCREEN);
                }
            }
        });

        if (app.getDrawing() != null) {
            newDrawing = new TextButton(CONTINUE.toString(), buttonStyle);
            newDrawing.setSize(605, 70);
            newDrawing.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    app.setScreen(DynamicPlanimetry.EDITOR_SCREEN);
                }
            });
            buttons.add(newDrawing).uniform().fill().prefWidth(605).colspan(2).padBottom(5).row();
        }
        buttons.add(create).uniform().fill().prefWidth(300);
        buttons.add(load).uniform().fill().prefWidth(300).padLeft(5);
        buttons.row();
        buttons.add(go_settings).uniform().fill().prefWidth(300).padTop(5);
        buttons.add(exit).uniform().fill().prefWidth(300).padTop(5).padLeft(5);

        table.add(title).expandY();
        table.row();
        table.add(buttons).expandY().top();

        version.setPosition(4, 0);
        this.repositionFps(4, version.getHeight());

        stage.addActor(table);
        stage.addActor(version);
    }

    @Override
    public void show() {
        super.show();

        title.setStyle(styles.getLabelStyle(Size.EXTREMELY_LARGE));
        version.setStyle(styles.getLabelStyle(Size.MEDIUM));
        version.setSize(version.getPrefWidth(), version.getPrefHeight());

        TextButtonStyle buttonStyle = new TextButtonStyle(styles.normal, styles.pressed, styles.normal, app.getFont(styles.getHeight() / Size.VERY_LARGE.getFactor(), Theme.current().textButton()));
        buttonStyle.over = styles.over;
        buttonStyle.checkedOver = styles.over;

        load.setStyle(buttonStyle);
        go_settings.setStyle(buttonStyle);
        exit.setStyle(buttonStyle);
        if (newDrawing != null) newDrawing.setStyle(buttonStyle);
        create.setStyle(buttonStyle);

        version.setPosition(4, 0);
        this.repositionFps(4, version.getHeight());
    }

    @Override public void customRender() {}
    @Override public void addActorsAboveFps() {}

    public static Window createSaveWindow(StyleSet styles, Stage stage) {
        return createSaveWindow(styles, stage, () -> {});
    }

    public static Window createSaveWindow(StyleSet styles, Stage stage, Runnable postAction) {
        DynamicPlanimetry app = DynamicPlanimetry.getInstance();
        Window window = new Window(styles, Component.translatable("ui.save_or_exit.title"), true);
        Table table = new Table();
        ComponentLabel label = new ComponentLabel(Component.translatable("ui.save_or_exit"), styles.font, Size.MEDIUM);

        TextButton save = GuiHelper.createTextButton("ui.save_or_exit.save", styles, Size.MEDIUM, () -> {
            Window saveDialogue = EditorScreen.createSaveDialogue(styles, postAction);
            saveDialogue.setBounds(window.getX(), window.getY(), window.getWidth(), window.getHeight());
            window.remove();
            stage.addActor(saveDialogue);
        });

        TextButton noSave = GuiHelper.createTextButton("ui.save_or_exit.nosave", styles, Size.MEDIUM, () -> {
            window.remove();
            app.setDrawing(null, false);
            app.setScreen(DynamicPlanimetry.EDITOR_SCREEN);
            postAction.run();
        });

        TextButton cancel = GuiHelper.createTextButton("ui.save_or_exit.cancel", styles, Size.MEDIUM, window::remove);
        window.setActor(table);

        table.add(label).pad(5).colspan(3).fill().center().row();
        table.add(save).pad(5).expand().fill();
        table.add(noSave).pad(5).expand().fill();
        table.add(cancel).pad(5).expand().fill();

        return window;
    }
}
