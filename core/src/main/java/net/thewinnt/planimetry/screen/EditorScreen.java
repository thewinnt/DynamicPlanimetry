package net.thewinnt.planimetry.screen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.Settings;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.TreeNode;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.factories.CircleFactory;
import net.thewinnt.planimetry.shapes.factories.FreePolygonFactory;
import net.thewinnt.planimetry.shapes.factories.LimitedPolygonFactory;
import net.thewinnt.planimetry.shapes.factories.LineFactory;
import net.thewinnt.planimetry.shapes.factories.LineFactory.LineType;
import net.thewinnt.planimetry.shapes.factories.MultiPointLineFactory;
import net.thewinnt.planimetry.shapes.factories.PointAngleMarkerFactory;
import net.thewinnt.planimetry.shapes.factories.PointFactory;
import net.thewinnt.planimetry.shapes.factories.ShapeFactory;
import net.thewinnt.planimetry.ui.ComponentLabel;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.GuiHelper;
import net.thewinnt.planimetry.ui.ShapeSettingsBackground;
import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.Window;
import net.thewinnt.planimetry.ui.functions.Function;
import net.thewinnt.planimetry.ui.properties.DropdownLayout;
import net.thewinnt.planimetry.ui.properties.PropertyEntry;
import net.thewinnt.planimetry.ui.properties.PropertyLayout;
import net.thewinnt.planimetry.ui.text.Component;

public class EditorScreen extends FlatUIScreen {
    private DrawingBoard board;
    private ShapeSettingsBackground settings;
    private double lastScale = -1;
    private Vec2 lastOffset = null;
    private float splitPos; // TODO dockable windows

    private ScrollPane creation; // TODO text buttons for this
    private ScrollPane selectedShapeName; // TODO icons for this
    private ScrollPane properties; // TODO make them undockable
    private Table functions;
    private Table actions;

    private Actor selectionToggle;
    private TextButton exitToMenu;
    private TextButton goSettings;
    private TextButton save;
    private Window saveDialog;

    private Shape lastSelected;

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
    }

    private Button leftAlignedButton(String translation, Size size, boolean active) {
        Button output = new Button(styles.getButtonStyle(size, active));
        output.add(new ComponentLabel(Component.translatable(translation), styles.font, size)).padLeft(3).expand().fill();
        return output;
    }

    private Button shapeCreationButton(String translation, Size size, Supplier<? extends ShapeFactory> factory) {
        Button output = leftAlignedButton(translation, size, true);
        output.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                board.cancelCreation();
                board.startCreation(factory.get());
            }
        });
        return output;
    }

    public void rebuildUI(List<Shape> selections) {
        // TABLES
        functions.reset();
        actions.reset();

        // ACTORS
        Button createPoint = shapeCreationButton("ui.edit.create.point", Size.SMALL, () -> new PointFactory(board));
        Button createLine = shapeCreationButton("ui.edit.create.infinite_line", Size.SMALL, () -> new LineFactory(board, LineType.INFINITE));
        Button createRay = shapeCreationButton("ui.edit.create.ray", Size.SMALL, () -> new LineFactory(board, LineType.RAY));
        Button createLineSegment = shapeCreationButton("ui.edit.create.line_segment", Size.SMALL, () -> new LineFactory(board, LineType.SEGMENT));
        Button createMultiPointLine = shapeCreationButton("ui.edit.create.polygonal_chain", Size.SMALL, () -> new MultiPointLineFactory(board));
        Button createCircle = shapeCreationButton("ui.edit.create.circle", Size.SMALL, () -> new CircleFactory(board));
        Button createPolygon = shapeCreationButton("ui.edit.create.polygon", Size.SMALL, () -> new FreePolygonFactory(board));
        Button createTriangle = shapeCreationButton("ui.edit.create.triangle", Size.SMALL, () -> new LimitedPolygonFactory(board, 3));
        Button createPointAngleMarker = shapeCreationButton("ui.edit.create.angle_marker.point", Size.SMALL, () -> new PointAngleMarkerFactory(board));

        selectionToggle = new PropertyEntry(Settings.get().ctrlSelection, styles, Size.SMALL);
        exitToMenu = new TextButton(DynamicPlanimetry.translate("ui.edit.exit_to_menu"), styles.getButtonStyle(Size.SMALL, true));
        goSettings = new TextButton(DynamicPlanimetry.translate("ui.edit.settings"), styles.getButtonStyle(Size.SMALL, true));
        save = new TextButton(DynamicPlanimetry.translate("ui.edit.save"), styles.getButtonStyle(Size.SMALL, true));

        // LISTENERS
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
                saveDialog = createSaveDialogue(styles, () -> {});
                stage.addActor(saveDialog);
            }
        });
        // ADDING TO TABLES
        List<Actor> creations = new ArrayList<>();
        creations.add(createPoint);
        creations.add(createCircle);
        creations.add(new DropdownLayout(List.of(createLine, createLineSegment, createRay, createMultiPointLine), styles, Component.translatable("ui.edit.create.group.lines"), Size.SMALL, false));
        creations.add(new DropdownLayout(List.of(createPolygon, createTriangle), styles, Component.translatable("ui.edit.create.group.polygons"), Size.SMALL, false));
        creations.add(new DropdownLayout(List.of(createPointAngleMarker), styles, Component.translatable("ui.edit.create.group.markers"), Size.SMALL, false));
        creation.setActor(new DropdownLayout(creations, styles, Component.translatable("ui.edit.create.title"), Size.SMALL, true));

        if (selections.size() == 1) {
            Shape selection = selections.get(0);
            selection.rebuildProperties();
            selectedShapeName.setActor(new ComponentLabel(selection.getName(), app::getBoldFont, Size.MEDIUM));
            // create properties, keeping previously open sections
            TreeNode<PropertyLayout.OpenStatus> openStatuses = null;
            if (lastSelected == selection && properties.getActor() instanceof PropertyLayout layout) {
                openStatuses = layout.getOpenChildren();
            }
            PropertyLayout layout = new PropertyLayout(selection.getProperties(), styles, Component.translatable("ui.edit.properties.title"), Size.SMALL, true);
            properties.setActor(layout);
            if (openStatuses != null) {
                layout.applyOpenStatus(openStatuses);
            }
            // add functions
            Collection<Function<?>> shapeFunctions = selection.getFunctions();
            if (!shapeFunctions.isEmpty()) {
                functions.add(new ComponentLabel(Component.translatable("ui.edit.actions.title"), app::getBoldFont, Size.MEDIUM)).expand().left().row();
                for (Function<?> i : shapeFunctions) {
                    i.addUseListener(shape -> show());
                    functions.add(i.setupActors(styles)).expandX().fillX().row();
                }
            }
            lastSelected = selection;
        } else if (selections.size() > 1) {
            selectedShapeName.setActor(new ComponentLabel(Component.translatable("ui.edit.selection.multiple", selections.size()), app::getBoldFont, Size.MEDIUM));
            properties.setActor(null);
            lastSelected = null;
        } else {
            selectedShapeName.setActor(null);
            properties.setActor(null);
            lastSelected = null;
        }

//        actions.add(selectionToggle).expand().fill().colspan(3).pad(5, 5, 5, 0).row();
        actions.add(exitToMenu).expand().fill().pad(5, 5, 5, 0);
        actions.add(goSettings).expand().fill().pad(5, 5, 5, 0);
        actions.add(save).expand().fill().pad(5);

        if (saveDialog != null) {
            saveDialog.invalidate();
        }
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
        splitPos = width - height * Settings.get().getEditPaneScale() / 2;

        board.setPosition(0, 0);
        board.setSize(splitPos, height);

        settings.setPosition(splitPos, 0);
        settings.setSize(width - splitPos, height);

        rebuildUI(board.getSelection());

        layout();
    }

    public void layout() {
        final float width = Gdx.graphics.getWidth();
        final float height = Gdx.graphics.getHeight();
        splitPos = width - height * Settings.get().getEditPaneScale() / 2;

        creation.setSize(width - splitPos - 10, creation.getPrefHeight());
        creation.setPosition(splitPos + 5, height - creation.getHeight() - 5);

        selectedShapeName.setSize(width - splitPos - 10, selectedShapeName.getPrefHeight());
        selectedShapeName.setPosition(splitPos + 5, height - creation.getHeight() - selectedShapeName.getHeight() - 15);

        properties.setSize((int) (width - splitPos) - 4, Math.min(properties.getPrefHeight(), height * 0.4f));
        properties.setPosition(splitPos + 2, height - creation.getHeight() - selectedShapeName.getHeight() - properties.getHeight() - 15);

        functions.setSize(width - splitPos - 4, Math.min(functions.getPrefHeight(), height * 0.4f));
        functions.setPosition(splitPos + 2, height - creation.getHeight() - selectedShapeName.getHeight() - properties.getHeight() - functions.getHeight() - 25);

        actions.setSize(width - splitPos, actions.getPrefHeight());
        actions.setPosition(splitPos, 0);
    }

    @Override
    public void customRender() {
    }

    @Override
    public void addActorsAboveFps() {
    }

    public DrawingBoard getBoard() {
        return board;
    }

    @Override
    public void hide() {
        super.hide();
        lastScale = board.getScale();
        lastOffset = board.getOffset();
    }

    public static Window createSaveDialogue(StyleSet styles, Runnable postAction) {
        DynamicPlanimetry app = DynamicPlanimetry.getInstance();
        Window saveDialog = new Window(styles, Component.translatable("ui.save.title"), false);
        Table table = new Table();

        Drawing drawing = app.getDrawing();
        String willBeSavedAs = DynamicPlanimetry.translate("ui.save.filename", drawing.withFilename(drawing.getName(), false).getFilename());
        Label filename = new Label(willBeSavedAs, styles.getLabelStyle(Size.VERY_SMALL));

        TextField namePicker = new TextField(drawing.getName(), styles.getTextFieldStyle(Size.SMALL, true));
        namePicker.setTextFieldListener((textField, c) -> {
            String translate = DynamicPlanimetry.translate("ui.save.filename", drawing.withFilename(namePicker.getText(), false).getFilename());
            filename.setText(translate);
        });

        TextButton save = GuiHelper.createTextButton("ui.save.save", styles, Size.SMALL, () -> {
            drawing.setName(namePicker.getText());
            drawing.save(namePicker.getText(), false);
            saveDialog.remove();
            postAction.run();
        });

        TextButton cancel = GuiHelper.createTextButton("ui.save.cancel", styles, Size.SMALL, saveDialog::remove);

        Label nameLabel = new Label(DynamicPlanimetry.translate("ui.save.drawing_name"), styles.getLabelStyle(Size.SMALL));
        table.add(nameLabel).pad(Gdx.graphics.getHeight() / 40f, 5, 0, 5);
        table.add(namePicker).expand().fill().pad(Gdx.graphics.getHeight() / 40f + 5, 5, 0, 5).row();
        table.add(filename).colspan(2).pad(5).row();
        HorizontalGroup group = new HorizontalGroup();
        group.center().expand().pad(5).fill().space(5);
        group.addActor(save);
        group.addActor(cancel);
        table.add(group).colspan(2).expand().fill();
        saveDialog.setActor(table);
        saveDialog.setRestyler(actor1 -> {
            filename.setStyle(styles.getLabelStyle(Size.VERY_SMALL));
            namePicker.setStyle(styles.getTextFieldStyle(Size.SMALL, true));
            save.setStyle(styles.getButtonStyle(Size.SMALL, true));
            cancel.setStyle(styles.getButtonStyle(Size.SMALL, true));
            nameLabel.setStyle(styles.getLabelStyle(Size.SMALL));
        });
        return saveDialog;
    }
}
