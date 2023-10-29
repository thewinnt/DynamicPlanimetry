package net.thewinnt.planimetry.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.thewinnt.gdxutils.ColorUtils;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.factories.CircleFactory;
import net.thewinnt.planimetry.shapes.factories.LineFactory;
import net.thewinnt.planimetry.shapes.factories.LineFactory.LineType;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.ShapeSettings;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.drawable.CheckboxDrawable;
import net.thewinnt.planimetry.ui.drawable.RectangleDrawable;

public class EditorScreen extends FlatUIScreen {
    private DrawingBoard board;
    private ShapeSettings settings;
    private StyleSet styles;
    
    private Table creation;
    private Table properties;
    private Table functions;

    private Label creationCategory;
    private TextButton createLine;
    private TextButton createRay;
    private TextButton createLineSegment;
    private TextButton createCircle;

    public EditorScreen(DynamicPlanimetry app) {
        super(app);
    }

    @Override
    public void addActorsBelowFps() {
        this.styles = new StyleSet();
        updateStyles();
        board = new DrawingBoard(drawer, app::getBoldFont);

        creation = new Table();
        properties = new Table();
        functions = new Table();
        rebuildUI(board.getSelection());
        settings = new ShapeSettings(drawer, creation, properties);

        stage.addActor(board);
        stage.setScrollFocus(board);
        stage.setKeyboardFocus(board);

        stage.addActor(settings);
        stage.addActor(creation);
        stage.addActor(properties);
        stage.addActor(functions);
    }

    public void rebuildUI(Shape selection) {
        // TABLES
        creation.reset();
        properties.reset();
        functions.reset();

        // ACTORS
        creationCategory = new Label("Создание", styles.getLabelStyle());
        createLine = new TextButton("Прямая", styles.getButtonStyle());
        createRay = new TextButton("Луч", styles.getButtonStyle());
        createLineSegment = new TextButton("Отрезок", styles.getButtonStyle());
        createCircle = new TextButton("Окружность", styles.getButtonStyle());

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

        // ADDING TO TABLES
        creation.add(creationCategory).expandX().fillX().pad(5, 5, 0, 5).row();
        creation.add(createLine).expandX().fillX().pad(5, 5, 0, 5).row();
        creation.add(createRay).expandX().fillX().pad(5, 5, 0, 5).row();
        creation.add(createLineSegment).expandX().fillX().pad(5, 5, 0, 5).row();
        creation.add(createCircle).expandX().fillX().pad(5, 5, 0, 5);

        System.out.println("Rebuilding UI with selected shape: " + selection);
    }

    public void updateStyles() {
        final int fontFactorA = 26;
        final int fontFactorB = 20;

        // text field style
        RectangleDrawable field = new RectangleDrawable(drawer);
        RectangleDrawable cursor = new RectangleDrawable(drawer);
        RectangleDrawable selection = new RectangleDrawable(drawer);
        field.withColors(Color.WHITE, Color.BLACK);
        cursor.withColors(Color.BLACK, Color.BLACK);
        selection.withColors(Color.BLUE, Color.BLUE);
        TextFieldStyle textFieldStyle = new TextFieldStyle(app.getBoldFont(Gdx.graphics.getHeight()/fontFactorA, Color.BLACK), Color.BLACK, cursor, selection, field);
        textFieldStyle.messageFontColor = ColorUtils.rgbColor(127, 127, 127);
        textFieldStyle.messageFont = app.getBoldFont(Gdx.graphics.getHeight()/fontFactorA, ColorUtils.rgbColor(127, 127, 127));
        this.styles.setTextFieldStyle(textFieldStyle);

        // selection list style
        RectangleDrawable paneBg = new RectangleDrawable(drawer).withColors(Color.WHITE, Color.WHITE);
        RectangleDrawable selectionOver = new RectangleDrawable(drawer).withColors(DynamicPlanimetry.COLOR_MAIN, null);
        RectangleDrawable fillBlack = new RectangleDrawable(drawer).withColors(Color.BLACK, Color.BLACK);
        ScrollPaneStyle paneStyle = new ScrollPaneStyle(fillBlack, fillBlack, paneBg, fillBlack, paneBg);
        ListStyle innerStyle = new ListStyle(app.getBoldFont(Gdx.graphics.getHeight()/fontFactorA, Color.BLACK), Color.BLACK, DynamicPlanimetry.COLOR_INACTIVE, selectionOver);
        innerStyle.over = selectionOver;
        innerStyle.background = normal;
        innerStyle.down = new RectangleDrawable(drawer).withColors(DynamicPlanimetry.COLOR_PRESSED, null);
        SelectBoxStyle listStyle = new SelectBoxStyle(app.getBoldFont(Gdx.graphics.getHeight()/fontFactorA, Color.BLACK), Color.BLACK, normal, paneStyle, innerStyle);
        listStyle.backgroundOver = over;
        listStyle.backgroundDisabled = disabled;
        listStyle.backgroundOpen = pressed;
        this.styles.setListStyle(listStyle);

        // checkbox style
        RectangleDrawable enabledNormal = new CheckboxDrawable(drawer).withColors(DynamicPlanimetry.COLOR_BUTTON, Color.BLACK);
        RectangleDrawable enabledOver = new CheckboxDrawable(drawer).withColors(DynamicPlanimetry.COLOR_MAIN, Color.BLACK);
        RectangleDrawable enabledPressed = new CheckboxDrawable(drawer).withColors(DynamicPlanimetry.COLOR_PRESSED, Color.BLACK);
        RectangleDrawable normal = new RectangleDrawable(drawer).withColors(DynamicPlanimetry.COLOR_BUTTON, Color.BLACK);
        RectangleDrawable pressed = new RectangleDrawable(drawer).withColors(DynamicPlanimetry.COLOR_PRESSED, Color.BLACK);
        RectangleDrawable over = new RectangleDrawable(drawer).withColors(DynamicPlanimetry.COLOR_MAIN, Color.BLACK);
        ButtonStyle checkBoxStyle = new ButtonStyle(normal, pressed, enabledNormal);
        checkBoxStyle.checkedDown = enabledPressed;
        checkBoxStyle.checkedOver = enabledOver;
        checkBoxStyle.over = over;
        this.styles.setCheckboxStyle(checkBoxStyle);
        
        // label style
        this.styles.setLabelStyle(new LabelStyle(app.getBoldFont(Gdx.graphics.getHeight()/fontFactorB, Color.BLACK), Color.BLACK));

        // text button style
        TextButtonStyle textButtonStyle = new TextButtonStyle(normal, pressed, normal, app.getBoldFont(Gdx.graphics.getHeight()/ fontFactorA, Color.BLACK));
        textButtonStyle.over = over;
        textButtonStyle.checkedOver = over;
        this.styles.setButtonStyle(textButtonStyle);
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

        updateStyles();
        rebuildUI(board.getSelection());

        creation.setSize(width - delimiter, creation.getPrefHeight());
        creation.setPosition(delimiter, height - creation.getHeight());

        properties.setSize(width - delimiter, properties.getPrefHeight());
        properties.setPosition(delimiter, height - creation.getHeight() - properties.getHeight());

        functions.setSize(width - delimiter, functions.getPrefHeight());
        functions.setPosition(delimiter, height - creation.getHeight() - properties.getHeight() - functions.getHeight());
    }

    @Override public void customRender() {}
    @Override public void addActorsAboveFps() {}
}
