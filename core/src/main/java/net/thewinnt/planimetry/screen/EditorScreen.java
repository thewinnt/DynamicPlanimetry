package net.thewinnt.planimetry.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.factories.CircleFactory;
import net.thewinnt.planimetry.shapes.factories.LineFactory;
import net.thewinnt.planimetry.shapes.factories.LineFactory.LineType;
import net.thewinnt.planimetry.shapes.factories.PolygonFactory;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.NameSequence;
import net.thewinnt.planimetry.ui.ShapeSettingsBackground;
import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.ui.properties.Property;

public class EditorScreen extends FlatUIScreen {
    private DrawingBoard board;
    private ShapeSettingsBackground settings;
    
    private Table creation;
    private Table properties;
    private Table functions;
    private Table actions;
    private Container<Window> saveOverlay;

    private Label creationCategory;
    private TextButton createLine;
    private TextButton createRay;
    private TextButton createLineSegment;
    private TextButton createCircle;
    private TextButton createPolygon;

    private TextButton exitToMenu;
    private TextButton save;
    private Window saveDialog;

    public EditorScreen(DynamicPlanimetry app) {
        super(app);
    }

    @Override
    public void addActorsBelowFps() {
        if (app.getDrawing() == null) {
            app.setDrawing(new Drawing(), false);
        }
        board = new DrawingBoard(drawer, app::getBoldFont, app.getDrawing());
        board.addSelectionListener(shape -> show());

        creation = new Table();
        properties = new Table();
        functions = new Table();
        actions = new Table();
        saveOverlay = new Container<>();
        rebuildUI(board.getSelection());
        settings = new ShapeSettingsBackground(drawer, creation, properties);

        stage.addActor(board);
        stage.setScrollFocus(board);
        stage.setKeyboardFocus(board);

        stage.addActor(settings);
        stage.addActor(creation);
        stage.addActor(properties);
        stage.addActor(functions);
        stage.addActor(actions);
        stage.addActor(saveOverlay);
    }

    public void rebuildUI(Shape selection) {
        // TABLES
        creation.reset();
        properties.reset();
        functions.reset();
        actions.reset();

        // ACTORS
        creationCategory = new Label("Создание", styles.getLabelStyle(Size.MEDIUM));
        createLine = new TextButton("Прямая", styles.getButtonStyle(Size.SMALL, true));
        createRay = new TextButton("Луч", styles.getButtonStyle(Size.SMALL, true));
        createLineSegment = new TextButton("Отрезок", styles.getButtonStyle(Size.SMALL, true));
        createCircle = new TextButton("Окружность", styles.getButtonStyle(Size.SMALL, true));
        createPolygon = new TextButton("Многоугольник", styles.getButtonStyle(Size.SMALL, true));

        exitToMenu = new TextButton("В меню", styles.getButtonStyle(Size.SMALL, true));
        save = new TextButton("Сохранить", styles.getButtonStyle(Size.SMALL, true));

        // LISTENERS
        createLine.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                board.startCreation(new LineFactory(board, LineType.INFINITE));
            }
        });

        createRay.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                board.startCreation(new LineFactory(board, LineType.RAY));
            }
        });

        createLineSegment.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                board.startCreation(new LineFactory(board, LineType.SEGMENT));
            }
        });
        
        createCircle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                board.startCreation(new CircleFactory(board));
            }
        });
        
        createPolygon.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                board.startCreation(new PolygonFactory(board));
            }
        });

        exitToMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                app.setScreen(DynamicPlanimetry.MAIN_MENU);
            }
        });

        save.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                saveDialog = new Window("Сохранение", styles.getWindowStyle(Size.MEDIUM));
                saveDialog.getTitleLabel().getStyle().background = styles.pressed;
                saveDialog.row().row();
                Label filename = new Label("Будет сохранён в " + app.getDrawing().withFilename(app.getDrawing().getName(), false).getFilename(), styles.getLabelStyle(Size.SMALL));
                TextField namePicker = new TextField(app.getDrawing().getName(), styles.getTextFieldStyle(Size.SMALL, true));
                namePicker.setTextFieldListener((textField, c) -> {
                    filename.setText("Будет сохранён в " + app.getDrawing().withFilename(namePicker.getText(), false).getFilename());
                });
                TextButton save = new TextButton("Сохранить", styles.getButtonStyle(Size.SMALL, true));
                save.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        app.getDrawing().setName(namePicker.getText());
                        app.getDrawing().save(namePicker.getText(), false);
                        saveOverlay.setActor(null);
                    }
                });

                saveDialog.add(new Label("Имя чертежа", styles.getLabelStyle(Size.SMALL))).pad(Gdx.graphics.getHeight() / 40, 5, 0, 5);
                saveDialog.add(namePicker).expand().fill().pad(Gdx.graphics.getHeight() / 40 + 5, 5, 0, 5).row();;
                saveDialog.add(filename).colspan(2).pad(5).row();
                saveDialog.add(save).colspan(2).pad(5);
                saveOverlay.setActor(saveDialog);
            }
        });
        // ADDING TO TABLES
        creation.add(creationCategory).expandX().fillX().pad(5, 5, 0, 5).row();
        creation.add(createLine).expandX().fillX().pad(5, 5, 0, 5).row();
        creation.add(createRay).expandX().fillX().pad(5, 5, 0, 5).row();
        creation.add(createLineSegment).expandX().fillX().pad(5, 5, 0, 5).row();
        creation.add(createCircle).expandX().fillX().pad(5, 5, 0, 5).row();
        creation.add(createPolygon).expandX().fillX().pad(5, 5, 0, 5);

        if (selection != null) {
            properties.add(new NameSequence(selection.getTypeName(), selection.getFullName(), app::getBoldFont, Gdx.graphics.getHeight() / Size.MEDIUM.factor)).colspan(9999).expand().fill().row();
            for (Property<?> i : selection.getProperties()) {
                properties.add(new Label(i.getName(), styles.getLabelStyle(Size.SMALL))).expand().fill();
                properties.add(i.getActorSetup(styles)).expand().fill().pad(5, 5, 0, 5).row();
            }
        }

        actions.add(exitToMenu).expand().fill().pad(5, 5, 5, 0);
        actions.add(save).expand().fill().pad(5);
    }

    @Override
    public void show() {
        super.show();
        final float width = Gdx.graphics.getWidth();
        final float height = Gdx.graphics.getHeight();
        final float delimiter = width - height * 0.5f;

        board.setPosition(0, 0);
        board.setSize(delimiter, height);

        settings.setPosition(delimiter, 0);
        settings.setSize(height * 0.5f, height);

        rebuildUI(board.getSelection());

        creation.setSize(height * 0.5f, creation.getPrefHeight());
        creation.setPosition(delimiter, height - creation.getHeight());
        
        properties.setSize(height * 0.5f, properties.getPrefHeight());
        properties.setPosition(delimiter, height - creation.getHeight() - properties.getHeight() - 10);
        
        functions.setSize(height * 0.5f, functions.getPrefHeight());
        functions.setPosition(delimiter, height - creation.getHeight() - properties.getHeight() - functions.getHeight() - 10);

        actions.setSize(height * 0.5f, actions.getPrefHeight());
        actions.setPosition(delimiter, 0);
        
        saveOverlay.setSize(width, height);
        saveOverlay.setPosition(0, 0);
        saveOverlay.center().fill(false);
    }

    @Override public void customRender() {}
    @Override public void addActorsAboveFps() {}
    
    public DrawingBoard getBoard() {
        return board;
    }
}
