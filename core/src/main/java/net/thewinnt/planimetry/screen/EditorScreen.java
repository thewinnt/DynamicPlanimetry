package net.thewinnt.planimetry.screen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import com.badlogic.gdx.utils.Align;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.factories.CircleFactory;
import net.thewinnt.planimetry.shapes.factories.FreePolygonFactory;
import net.thewinnt.planimetry.shapes.factories.LimitedPolygonFactory;
import net.thewinnt.planimetry.shapes.factories.LineFactory;
import net.thewinnt.planimetry.shapes.factories.LineFactory.LineType;
import net.thewinnt.planimetry.shapes.factories.PointFactory;
import net.thewinnt.planimetry.ui.ComponentLabel;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.ShapeSettingsBackground;
import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.ui.functions.Function;
import net.thewinnt.planimetry.ui.properties.DropdownLayout;
import net.thewinnt.planimetry.ui.properties.PropertyLayout;
import net.thewinnt.planimetry.ui.text.Component;

public class EditorScreen extends FlatUIScreen {
    private DrawingBoard board;
    private ShapeSettingsBackground settings;
    private double lastScale = -1;
    private Vec2 lastOffset = null;

    private ScrollPane creation;
    private ScrollPane selectedShapeName;
    private ScrollPane properties;
    private Table functions;
    private Table actions;
    private Container<Window> saveOverlay;

    private TextButton createPoint;
    private TextButton createLine;
    private TextButton createRay;
    private TextButton createLineSegment;
    private TextButton createCircle;
    private TextButton createPolygon;
    private TextButton createTriangle;

    private TextButton exitToMenu;
    private TextButton goSettings;
    private TextButton save;
    private Window saveDialog;

    public EditorScreen(DynamicPlanimetry app) {
        super(app);
    }

    @Override
    public void addActorsBelowFps() {
        if (app.getDrawing() == null) {
            app.setDrawing(new Drawing(), true);
        }
        board = new DrawingBoard(drawer, app::getBoldFont, app.getDrawing());
        board.addSelectionListener(shape -> show());

        creation = new ScrollPane(null);
        selectedShapeName = new ScrollPane(null);
        properties = new ScrollPane(null);
        functions = new Table();
        actions = new Table();
        saveOverlay = new Container<>();
        rebuildUI(board.getSelection());
        settings = new ShapeSettingsBackground(drawer, creation, properties);

        stage.addActor(board);
        stage.setKeyboardFocus(board);

        stage.addActor(settings);
        stage.addActor(creation);
        stage.addActor(selectedShapeName);
        stage.addActor(properties);
        stage.addActor(functions);
        stage.addActor(actions);
        stage.addActor(saveOverlay);
    }

    private TextButton leftAlignedButton(String text, Size size, boolean active) {
        TextButton output = new TextButton(text, styles.getButtonStyle(size, active));
        output.getLabel().setAlignment(Align.left);
        output.getLabelCell().padLeft(4);
        return output;
    }

    public void rebuildUI(Shape selection) {
        // TABLES
        functions.reset();
        actions.reset();

        // ACTORS
        createPoint = leftAlignedButton(DynamicPlanimetry.translate("ui.edit.create.point"), Size.SMALL, true);
        createLine = leftAlignedButton(DynamicPlanimetry.translate("ui.edit.create.infinite_line"), Size.SMALL, true);
        createRay = leftAlignedButton(DynamicPlanimetry.translate("ui.edit.create.ray"), Size.SMALL, true);
        createLineSegment = leftAlignedButton(DynamicPlanimetry.translate("ui.edit.create.line_segment"), Size.SMALL, true);
        createCircle = leftAlignedButton(DynamicPlanimetry.translate("ui.edit.create.circle"), Size.SMALL, true);
        createPolygon = leftAlignedButton(DynamicPlanimetry.translate("ui.edit.create.polygon"), Size.SMALL, true);
        createTriangle = leftAlignedButton(DynamicPlanimetry.translate("ui.edit.create.triangle"), Size.SMALL, true);

        exitToMenu = new TextButton(DynamicPlanimetry.translate("ui.edit.exit_to_menu"), styles.getButtonStyle(Size.SMALL, true));
        goSettings = new TextButton(DynamicPlanimetry.translate("ui.edit.settings"), styles.getButtonStyle(Size.SMALL, true));
        save = new TextButton(DynamicPlanimetry.translate("ui.edit.save"), styles.getButtonStyle(Size.SMALL, true));

        // LISTENERS
        createPoint.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                board.startCreation(new PointFactory(board));
            }
        });

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
                board.startCreation(new FreePolygonFactory(board));
            }
        });

        createTriangle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                board.startCreation(new LimitedPolygonFactory(board, 3));
            }
        });

        exitToMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                app.setScreen(DynamicPlanimetry.MAIN_MENU);
            }
        });

        goSettings.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                app.settingsScreen.setFrom(EditorScreen.this);
                app.setScreen(DynamicPlanimetry.SETTINGS_SCREEN);
            }
        });

        save.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                saveDialog = new Window(DynamicPlanimetry.translate("ui.save.title"), styles.getWindowStyle(Size.MEDIUM));
                saveDialog.getTitleLabel().getStyle().background = styles.pressed;
                saveDialog.row().row();

                String willBeSavedAs = DynamicPlanimetry.translate("ui.save.filename", app.getDrawing().withFilename(app.getDrawing().getName(), false).getFilename());
                Label filename = new Label(willBeSavedAs, styles.getLabelStyle(Size.VERY_SMALL));

                TextField namePicker = new TextField(app.getDrawing().getName(), styles.getTextFieldStyle(Size.SMALL, true));
                namePicker.setTextFieldListener((textField, c) -> {
                    String translate = DynamicPlanimetry.translate("ui.save.filename", app.getDrawing().withFilename(namePicker.getText(), false).getFilename());
                    filename.setText(translate);
                });

                TextButton save = new TextButton(DynamicPlanimetry.translate("ui.save.save"), styles.getButtonStyle(Size.SMALL, true));
                save.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        app.getDrawing().setName(namePicker.getText());
                        app.getDrawing().save(namePicker.getText(), false);
                        saveOverlay.setActor(null);
                    }
                });

                saveDialog.add(new Label(DynamicPlanimetry.translate("ui.save.drawing_name"), styles.getLabelStyle(Size.SMALL))).pad(Gdx.graphics.getHeight() / 40f, 5, 0, 5);
                saveDialog.add(namePicker).expand().fill().pad(Gdx.graphics.getHeight() / 40f + 5, 5, 0, 5).row();;
                saveDialog.add(filename).colspan(2).pad(5).row();
                saveDialog.add(save).colspan(2).pad(5);
                saveOverlay.setActor(saveDialog);
            }
        });
        // ADDING TO TABLES
        List<Actor> creations = new ArrayList<>();
        creations.add(createPoint);
        creations.add(createCircle);
        creations.add(new DropdownLayout(List.of(createLine, createLineSegment, createRay), styles, Component.translatable("ui.edit.create.group.lines"), Size.SMALL, false));
        creations.add(new DropdownLayout(List.of(createPolygon, createTriangle), styles, Component.translatable("ui.edit.create.group.polygons"), Size.SMALL, false));
        creation.setActor(new DropdownLayout(creations, styles, Component.translatable("ui.edit.create.title"), Size.SMALL, true));

        if (selection != null) {
            selectedShapeName.setActor(new ComponentLabel(Component.translatable("ui.edit.properties.title"), app::getBoldFont, Size.MEDIUM));
            properties.setActor(new PropertyLayout(selection.getProperties(), styles, selection.getName(), Size.SMALL, true));
            Collection<Function<?>> shapeFunctions = selection.getFunctions();
            if (!shapeFunctions.isEmpty()) {
                functions.add(new ComponentLabel(Component.translatable("ui.edit.actions.title"), app::getBoldFont, Size.MEDIUM)).expand().left().row();
                for (Function<?> i : shapeFunctions) {
                    i.addUseListener(shape -> show());
                    functions.add(i.setupActors(styles)).expandX().fillX().row();
                }
            }
        } else {
            selectedShapeName.setActor(null);
            properties.setActor(null);
        }

        actions.add(exitToMenu).expand().fill().pad(5, 5, 5, 0);
        actions.add(goSettings).expand().fill().pad(5, 5, 5, 0);
        actions.add(save).expand().fill().pad(5);
    }

    @Override
    public void show() {
        super.show();

        if (lastScale > 0 && lastOffset != null) {
            board.setScaleBoard(lastScale);
            board.setOffset(lastOffset);
            lastScale = -1;
            lastOffset = null;
        }

        final float width = Gdx.graphics.getWidth();
        final float height = Gdx.graphics.getHeight();
        final float delimiter = width - height * 0.5f;

        board.setPosition(0, 0);
        board.setSize(delimiter, height);

        settings.setPosition(delimiter, 0);
        settings.setSize(height * 0.5f, height);

        rebuildUI(board.getSelection());

        layout();
    }

    public void layout() {
        final float width = Gdx.graphics.getWidth();
        final float height = Gdx.graphics.getHeight();
        final float delimiter = width - height * 0.5f;

        creation.setSize(height * 0.5f - 10, creation.getPrefHeight());
        creation.setPosition(delimiter + 5, height - creation.getHeight() - 5);

        selectedShapeName.setSize(height * 0.5f - 10, selectedShapeName.getPrefHeight());
        selectedShapeName.setPosition(delimiter + 5, height - creation.getHeight() - selectedShapeName.getHeight() - 15);

        properties.setSize((int)(height * 0.5f) - 4, Math.min(properties.getPrefHeight(), height * 0.4f));
        properties.setPosition(delimiter + 2, height - creation.getHeight() - selectedShapeName.getHeight() - properties.getHeight() - 15);

        functions.setSize(height * 0.5f - 4, Math.min(functions.getPrefHeight(), height * 0.4f));
        functions.setPosition(delimiter + 2, height - creation.getHeight() - selectedShapeName.getHeight() - properties.getHeight() - functions.getHeight() - 25);

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

    @Override
    public void hide() {
        super.hide();
        lastScale = board.getScale();
        lastOffset = board.getOffset();
    }
}
