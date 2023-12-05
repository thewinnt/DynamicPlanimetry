package net.thewinnt.planimetry.screen;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.thewinnt.gdxutils.ColorUtils;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.ui.Notifications;
import net.thewinnt.planimetry.ui.SaveEntry;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.drawable.RectangleDrawable;

public class FileSelectionScreen extends FlatUIScreen {
    private StyleSet styles;
    private ScrollPaneStyle paneStyle;
    private TextButtonStyle selectedItemStyle;
    // top row
    private Label linkBack;
    private Label title;
    // file list
    private ScrollPane pane;
    private Table files;
    // control buttons
    private Table controlPanel;
    private TextButton openSaveFolder;
    private TextButton rename;
    private TextButton delete;
    private TextButton update;
    private TextButton open;
    // renaming mini-screen
    private TextField nameField;
    private TextButton setName;
    private TextButton cancelRename;
    // selection stuff
    private boolean isRenaming;
    private Drawing selection;
    private SaveEntry selectionUI;
    private long lastClickTime;

    public FileSelectionScreen(DynamicPlanimetry app) {
        super(app);
    }
    
    @Override
    public void addActorsBelowFps() {
        app.preloadDrawings(Gdx.files.getLocalStoragePath() + "drawings");
        this.styles = new StyleSet();
        updateStyles();
        // ==========================
        // ACTOR DEFINITIONS
        // ==========================
        this.files = new Table().align(Align.top);
        this.controlPanel = new Table();

        this.linkBack = new Label("< Назад", styles.getLabelStyleLarge());
        this.title = new Label("Открыть чертёж", styles.getLabelStyleLarge());
        this.pane = new ScrollPane(files, paneStyle);
        
        this.openSaveFolder = new TextButton("Открыть папку", styles.getButtonStyle());
        this.rename = new TextButton("Переименовать", styles.getButtonStyleInactive());
        this.delete = new TextButton("Удалить", styles.getButtonStyleInactive());
        this.update = new TextButton("Обновить", styles.buttonStyle);
        this.open = new TextButton("Открыть", styles.buttonStyleInactive);

        this.nameField = new TextField("", styles.textFieldStyle);
        this.setName = new TextButton("Готово", styles.buttonStyle);
        this.cancelRename = new TextButton("Отмена", styles.buttonStyle);

        refillFiles();

        // ==========================
        // LISTENERS
        // ==========================
        this.linkBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(DynamicPlanimetry.MAIN_MENU);
            }
        });
        this.openSaveFolder.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    java.awt.Desktop.getDesktop().open(new File(Gdx.files.getLocalStoragePath() + "drawings"));
                } catch (IOException e) {
                    Notifications.addNotification("Error opening folder: " + e.getMessage(), 5000);
                }
            }
        });

        this.update.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                app.preloadDrawings(Gdx.files.getLocalStoragePath() + "drawings");
                refillFiles();
                show();
            }
        });

        this.open.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (open.getStyle() != styles.buttonStyleInactive) {
                    app.setDrawing(selection, true);
                    app.editorScreen.hide();
                    app.setScreen(DynamicPlanimetry.EDITOR_SCREEN);
                }
            }
        });

        this.rename.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isRenaming = true;
                rename.setStyle(styles.buttonStyleInactive);
                show();
            }
        });

        this.setName.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!nameField.getText().isEmpty()) {
                    isRenaming = false;
                    selection.setName(nameField.getText());
                    selection.save();
                    refillFiles();
                    show();
                }
            }
        });

        this.cancelRename.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isRenaming = false;
                nameField.setText(selection.getName());
            }
        });

        // ==========================
        // STAGE ADDING
        // ==========================
        stage.addActor(linkBack);
        stage.addActor(title);
        stage.addActor(pane);
        stage.addActor(controlPanel);
        stage.setScrollFocus(pane);
    }

    @Override
    public void show() {
        super.show();
        final int width = Gdx.graphics.getWidth();
        final int height = Gdx.graphics.getHeight();
        controlPanel.reset();
        controlPanel.add(openSaveFolder).expand().fill().pad(5);
        controlPanel.add(open).expand().fill().pad(5);
        controlPanel.add(rename).expand().fill().pad(5);
        if (isRenaming) {
            controlPanel.add(nameField).expand().fill().pad(5);
            controlPanel.add(setName).expand().fill().pad(5);
            controlPanel.add(cancelRename).expand().fill().pad(5);
        }
        controlPanel.add(delete).expand().fill().pad(5);
        controlPanel.add(update).expand().fill().pad(5);

        linkBack.setPosition(5, height - linkBack.getPrefHeight() - 5);
        title.setPosition((width - title.getPrefWidth()) / 2, linkBack.getY());

        controlPanel.setPosition(5, 5);
        controlPanel.setSize(width - 10, controlPanel.getPrefHeight());

        pane.setPosition(5, controlPanel.getHeight() + 10);
        pane.setSize(width - 10, height - pane.getY() - title.getHeight() - 10);

        if (selection == null) {
            rename.setDisabled(true);
            delete.setDisabled(true);
            rename.setStyle(styles.buttonStyleInactive);
            delete.setStyle(styles.buttonStyleInactive);
        } else {
            rename.setDisabled(false);
            delete.setDisabled(false);
            rename.setStyle(styles.buttonStyle);
            delete.setStyle(styles.buttonStyle);
        }
    }

    private void refillFiles() {
        // ==========================
        // FILLING IN THE FILES
        // ==========================
        List<Drawing> drawings = app.getAllDrawings();
        files.clear();
        if (drawings == null || drawings.isEmpty()) {
            files.add(new Label("Тут ничего нет...", styles.labelStyleLarge)).fill().center();
        } else {
            for (Drawing i : drawings) {
                if (i == null) {
                    Notifications.addNotification("Null drawing loaded", 100);
                    continue;
                }
                SaveEntry file = new SaveEntry(i.getName(), i.getCreationTime(), i.getLastEditTime(), i.getFilename().replace(Gdx.files.getLocalStoragePath(), ""), styles);
                file.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (System.currentTimeMillis() - lastClickTime < 300) {
                            app.setDrawing(i, true);
                            app.editorScreen.hide();
                            app.setScreen(DynamicPlanimetry.EDITOR_SCREEN);
                            return;
                        }
                        if (selectionUI != null) {
                            selectionUI.setStyle(styles.buttonStyle);
                        }
                        selection = i;
                        selectionUI = file;
                        lastClickTime = System.currentTimeMillis();
                        file.setStyle(selectedItemStyle);
                        nameField.setText(i.getName());
                        show();
                    }
                });
                files.add(file).expandX().fillX().pad(5).row();
            }
        }
    }

    @Override public void addActorsAboveFps() {}
    @Override public void customRender() {}

    public void updateStyles() {
        final int fontFactorA = 20;
        final int fontFactorB = 14;

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

        // scroll pane style
        RectangleDrawable paneBg = new RectangleDrawable(drawer).withColors(Color.WHITE, Color.WHITE);
        RectangleDrawable fillBlack = new RectangleDrawable(drawer).withColors(Color.BLACK, Color.BLACK);
        ScrollPaneStyle paneStyle = new ScrollPaneStyle(pressed, paneBg, fillBlack, paneBg, fillBlack);
        this.paneStyle = paneStyle;
        
        // label styles
        this.styles.setLabelStyleSmall(new LabelStyle(app.getBoldFont(Gdx.graphics.getHeight()/fontFactorA, Color.BLACK), Color.BLACK));
        this.styles.setLabelStyleLarge(new LabelStyle(app.getBoldFont(Gdx.graphics.getHeight()/fontFactorB, Color.BLACK), Color.BLACK));

        // text button styles
        TextButtonStyle textButtonStyle = new TextButtonStyle(normal, pressed, normal, app.getBoldFont(Gdx.graphics.getHeight()/ fontFactorA, Color.BLACK));
        textButtonStyle.over = over;
        textButtonStyle.checkedOver = over;
        this.styles.setButtonStyle(textButtonStyle);

        TextButtonStyle textButtonStyleInactive = new TextButtonStyle(disabled, disabled, disabled, app.getBoldFont(Gdx.graphics.getHeight()/ fontFactorA, DynamicPlanimetry.COLOR_INACTIVE));
        this.styles.setButtonStyleInactive(textButtonStyleInactive);

        this.selectedItemStyle = new TextButtonStyle(pressed, pressed, pressed, app.getBoldFont(Gdx.graphics.getHeight()/ fontFactorA, Color.BLACK));
        

        // window style
        WindowStyle windowStyle = new WindowStyle(app.getBoldFont(Gdx.graphics.getHeight()/fontFactorB, Color.BLACK), Color.BLACK, normal);
        this.styles.setWindowStyle(windowStyle);
    }
}
