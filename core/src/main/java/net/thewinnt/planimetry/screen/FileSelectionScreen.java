package net.thewinnt.planimetry.screen;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.ui.Notifications;
import net.thewinnt.planimetry.ui.SaveEntry;
import net.thewinnt.planimetry.ui.StyleSet.Size;

public class FileSelectionScreen extends FlatUIScreen {
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
        updateStyles();
        // ==========================
        // ACTOR DEFINITIONS
        // ==========================
        this.files = new Table().align(Align.top);
        this.controlPanel = new Table();

        this.linkBack = new Label("< Назад", styles.getLabelStyle(Size.LARGE));
        this.title = new Label("Открыть чертёж", styles.getLabelStyle(Size.LARGE));
        this.pane = new ScrollPane(files, paneStyle);
        
        this.openSaveFolder = new TextButton("Открыть папку", styles.getButtonStyle(Size.MEDIUM, true));
        this.rename = new TextButton("Переименовать", styles.getButtonStyle(Size.MEDIUM, false));
        this.delete = new TextButton("Удалить", styles.getButtonStyle(Size.MEDIUM, false));
        this.update = new TextButton("Обновить", styles.getButtonStyle(Size.MEDIUM, true));
        this.open = new TextButton("Открыть", styles.getButtonStyle(Size.MEDIUM, true));

        this.nameField = new TextField("", styles.getTextFieldStyle(Size.MEDIUM, true));
        this.setName = new TextButton("Готово", styles.getButtonStyle(Size.MEDIUM, true));
        this.cancelRename = new TextButton("Отмена", styles.getButtonStyle(Size.MEDIUM, true));

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
                if (open.getStyle() != styles.getButtonStyle(Size.MEDIUM, isRenaming)) {
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
                rename.setStyle(styles.getButtonStyle(Size.MEDIUM, isRenaming));
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
        updateStyles();
        final int width = Gdx.graphics.getWidth();
        final int height = Gdx.graphics.getHeight();
        
        this.linkBack.setStyle(styles.getLabelStyle(Size.LARGE));
        this.title.setStyle(styles.getLabelStyle(Size.LARGE));
        this.pane.setStyle(paneStyle);
        
        this.openSaveFolder.setStyle(styles.getButtonStyle(Size.MEDIUM, true));
        this.update.setStyle(styles.getButtonStyle(Size.MEDIUM, true));
        this.open.setStyle(styles.getButtonStyle(Size.MEDIUM, true));

        this.nameField.setStyle(styles.getTextFieldStyle(Size.MEDIUM, true));
        this.setName.setStyle(styles.getButtonStyle(Size.MEDIUM, true));
        this.cancelRename.setStyle(styles.getButtonStyle(Size.MEDIUM, true));
        
        rename.setDisabled(selection == null);
        delete.setDisabled(selection == null);
        this.rename.setStyle(styles.getButtonStyle(Size.MEDIUM, selection != null));
        this.delete.setStyle(styles.getButtonStyle(Size.MEDIUM, selection != null));

        refillFiles();

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

    }

    @Override
    public void hide() {
        super.hide();
        selection = null;
    }

    private void refillFiles() {
        // ==========================
        // FILLING IN THE FILES
        // ==========================
        List<Drawing> drawings = app.getAllDrawings();
        files.clear();
        if (drawings == null || drawings.isEmpty()) {
            files.add(new Label("Тут ничего нет...", styles.getLabelStyle(Size.LARGE))).fill().center();
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
                            selectionUI.setStyle(styles.getButtonStyle(Size.MEDIUM, true));
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
        this.paneStyle = new ScrollPaneStyle(styles.pressed, styles.fullWhite, styles.fullBlack, styles.fullWhite, styles.fullBlack);
        this.selectedItemStyle = new TextButtonStyle(styles.pressed, styles.pressed, styles.pressed, app.getBoldFont(Gdx.graphics.getHeight() / Size.MEDIUM.factor, Color.BLACK));
    }
}
