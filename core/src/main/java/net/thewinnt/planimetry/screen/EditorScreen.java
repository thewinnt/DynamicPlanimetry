package net.thewinnt.planimetry.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;

import net.thewinnt.gdxutils.ColorUtils;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.ShapeSettings;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.drawable.CheckboxDrawable;
import net.thewinnt.planimetry.ui.drawable.RectangleDrawable;
import net.thewinnt.planimetry.ui.parameters.DoubleParameter;

public class EditorScreen extends FlatUIScreen {
    private DrawingBoard board;
    private ShapeSettings settings;
    private StyleSet styles;

    public EditorScreen(DynamicPlanimetry app) {
        super(app);
    }

    @Override
    public void addActorsBelowFps() {
        this.styles = new StyleSet();
        updateStyles();
        board = new DrawingBoard(drawer, app::getBoldFont);
        settings = new ShapeSettings(board, drawer, styles);
        stage.addActor(board);
        // stage.setScrollFocus(board);
        // stage.setKeyboardFocus(board);
        stage.addActor(settings);
        DoubleParameter test = new DoubleParameter(styles, 0);
        Table table = test.getActorSetup();
        table.setPosition(800, 100);
        table.setSize(100, 30);
        stage.addActor(table);
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
        board.setPosition(0, 0);
        board.setSize(Gdx.graphics.getWidth() - Gdx.graphics.getHeight() * 0.5f, Gdx.graphics.getHeight());
        settings.setPosition(Gdx.graphics.getWidth() - Gdx.graphics.getHeight() * 0.5f, 0);
        settings.setSize(Gdx.graphics.getHeight() * 0.5f, Gdx.graphics.getHeight());
        updateStyles();
    }

    @Override public void customRender() {}
    @Override public void addActorsAboveFps() {}
}
