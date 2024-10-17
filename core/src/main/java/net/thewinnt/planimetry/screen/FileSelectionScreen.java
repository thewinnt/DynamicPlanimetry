package net.thewinnt.planimetry.screen;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import com.sun.nio.file.ExtendedCopyOption;
import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.Settings;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.ui.Notifications;
import net.thewinnt.planimetry.ui.SaveEntry;
import net.thewinnt.planimetry.ui.SaveEntry.SortingType;
import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.ui.properties.PropertyEntry;
import net.thewinnt.planimetry.ui.properties.types.BooleanProperty;
import net.thewinnt.planimetry.ui.properties.types.SelectionProperty;
import net.thewinnt.planimetry.ui.text.Component;

public class FileSelectionScreen extends FlatUIScreen {
    private ScrollPaneStyle paneStyle;
    // top row
    private Label linkBack;
    private Label title;
    // sorting method
    private Table sortingTable;
    private final SelectionProperty<SaveEntry.SortingType> sortingType;
    private final BooleanProperty isReverse;
    // file list
    private ScrollPane pane;
    private Table files;
    // control buttons
    private Table controlPanel;
    private TextButton openSaveFolder;
    private TextButton exportFile;
    private TextButton importFile;
    private TextButton rename;
    private TextButton update;
    private TextButton open;
    // renaming mini-screen
    private TextField nameField;
    private TextButton setName;
    private TextButton cancelRename;
    // selection stuff
    private boolean isRenaming;
    private Drawing selection;
    private long lastClickTime;

    public FileSelectionScreen(DynamicPlanimetry app) {
        super(app);
        this.sortingType = new SelectionProperty<>(Settings.get().getLastSortingType(), Component.translatable("ui.load_file.sort_by"), SortingType.values());
        this.isReverse = new BooleanProperty(Component.translatable("ui.load_file.sort_descending"), Settings.get().getLastSortingOrder());
        this.sortingType.addValueChangeListener(value -> {
            Settings.get().setLastSortingType(value);
            show();
        });
        this.isReverse.addValueChangeListener(value -> {
            Settings.get().setLastSortingOrder(value);
            show();
        });
    }

    @Override
    public void addActorsBelowFps() {
        app.preloadDrawings(Gdx.files.getLocalStoragePath() + "drawings");
        updateStyles();
        // ==========================
        // ACTOR DEFINITIONS
        // ==========================
        this.files = new Table().align(Align.top);
        this.sortingTable = new Table();
        this.controlPanel = new Table();

        this.linkBack = new Label(Component.translatable("ui.load_file.exit"), styles.getLabelStyle(Size.LARGE));
        this.title = new Label(Component.translatable("ui.load_file.title"), styles.getLabelStyle(Size.LARGE));
        this.pane = new ScrollPane(files, paneStyle);

        this.openSaveFolder = new TextButton(DynamicPlanimetry.translate("ui.load_file.open_folder"), styles.getButtonStyle(Size.MEDIUM, true));
        this.exportFile = new TextButton(DynamicPlanimetry.translate("ui.load_file.export"), styles.getButtonStyle(Size.MEDIUM, false));
        this.importFile = new TextButton(DynamicPlanimetry.translate("ui.load_file.import"), styles.getButtonStyle(Size.MEDIUM, true));
        this.rename = new TextButton(DynamicPlanimetry.translate("ui.load_file.rename"), styles.getButtonStyle(Size.MEDIUM, false));
        this.update = new TextButton(DynamicPlanimetry.translate("ui.load_file.update"), styles.getButtonStyle(Size.MEDIUM, true));
        this.open = new TextButton(DynamicPlanimetry.translate("ui.load_file.open_file"), styles.getButtonStyle(Size.MEDIUM, false));

        this.nameField = new TextField("", styles.getTextFieldStyle(Size.MEDIUM, true));
        this.setName = new TextButton(DynamicPlanimetry.translate("ui.load_file.rename.done"), styles.getButtonStyle(Size.MEDIUM, true));
        this.cancelRename = new TextButton(DynamicPlanimetry.translate("ui.load_file.rename.cancel"), styles.getButtonStyle(Size.MEDIUM, true));

        // refillFiles();

        // ==========================
        // LISTENERS
        // ==========================
        this.linkBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(DynamicPlanimetry.MAIN_MENU);
            }
        });
        if (DynamicPlanimetry.platform().canOpenDrawingFolder()) {
            this.openSaveFolder.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    try {
                        DynamicPlanimetry.platform().openDrawingFolder();
                    } catch (Exception e) {
                        Notifications.addNotification(DynamicPlanimetry.translate("error.load_file.cannot_open_folder", e.getMessage()), 5000);
                    }
                }
            });
        } else {
            this.exportFile.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (selection != null) {
                        File file = DynamicPlanimetry.io().suggestSave();
                        if (file != null && (!file.exists() || file.canWrite())) {
                            CompoundTag nbt = selection.toNbt();
                            try {
                                DynamicPlanimetry.NBT.toFile(nbt, file);
                            } catch (IOException e) {
                                Notifications.addNotification(DynamicPlanimetry.translate("error.load_file.export", e.getMessage()), 0);
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }

        this.importFile.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                List<File> file = DynamicPlanimetry.io().suggestOpen();
                if (file != null && !file.isEmpty()) {
                    for (File i : file) {
                        if (i != null && i.canRead()) {
                            Path drawingPath = Path.of(Gdx.files.getLocalStoragePath(), "drawings", i.getName());
                            System.out.println(drawingPath);
                            Drawing drawing = Drawing.load(i);
                            if (drawing != null) {
                                drawing.saveAs(drawingPath.toFile());
                                DynamicPlanimetry.getInstance().getAllDrawings().add(drawing);
                            } else {
                                Notifications.addNotification("Error loading drawing", 5000);
                            }
                        }
                    }
                }
            }
        });

        this.update.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selection = null;
                app.preloadDrawings(Gdx.files.getLocalStoragePath() + "drawings");
                refillFiles();
                show();
            }
        });

        this.open.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (open.getStyle() != styles.getButtonStyle(Size.MEDIUM, isRenaming) && selection != null) {
                    app.setDrawing(selection, true);
                    app.editorScreen.hide();
                    app.setScreen(DynamicPlanimetry.EDITOR_SCREEN);
                }
            }
        });

        this.rename.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selection != null) {
                    isRenaming = true;
                    rename.setStyle(styles.getButtonStyle(Size.MEDIUM, isRenaming));
                    show();
                }
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

        this.nameField.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER) {
                    if (!nameField.getText().isEmpty()) {
                        isRenaming = false;
                        selection.setName(nameField.getText());
                        selection.save();
                        refillFiles();
                        show();
                        return true;
                    }
                }
                return false;
            }
        });

        this.cancelRename.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isRenaming = false;
                nameField.setText(selection.getName());
                show();
            }
        });

        // ==========================
        // STAGE ADDING
        // ==========================
        stage.addActor(linkBack);
        stage.addActor(title);
        stage.addActor(sortingTable);
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

        this.importFile.setStyle(styles.getButtonStyle(Size.MEDIUM, true));
        this.exportFile.setDisabled(selection == null);
        this.exportFile.setStyle(styles.getButtonStyle(Size.MEDIUM, selection != null));
        this.openSaveFolder.setStyle(styles.getButtonStyle(Size.MEDIUM, true));
        this.update.setStyle(styles.getButtonStyle(Size.MEDIUM, true));
        this.open.setDisabled(selection == null);
        this.open.setStyle(styles.getButtonStyle(Size.MEDIUM, selection != null));

        this.nameField.setStyle(styles.getTextFieldStyle(Size.MEDIUM, true));
        this.setName.setStyle(styles.getButtonStyle(Size.MEDIUM, true));
        this.cancelRename.setStyle(styles.getButtonStyle(Size.MEDIUM, true));

        rename.setDisabled(selection == null);
        this.rename.setStyle(styles.getButtonStyle(Size.MEDIUM, selection != null));

        refillFiles();

        sortingTable.reset();
        sortingTable.add(new PropertyEntry(sortingType, styles, Size.MEDIUM)).expand().fill().padRight(20).left();
        sortingTable.add(new PropertyEntry(isReverse, styles, Size.MEDIUM)).expand().fill().left();

        controlPanel.reset();
        if (DynamicPlanimetry.platform().canOpenDrawingFolder()) {
            controlPanel.add(openSaveFolder).expand().fill().pad(5).uniform();
            // TODO export file instead
        } else {
            controlPanel.add(exportFile).expand().fill().pad(5).uniform();
        }
        controlPanel.add(importFile).expand().fill().pad(5).uniform();
        controlPanel.add(open).expand().fill().pad(5).uniform();
        if (isRenaming) {
            controlPanel.add(nameField).expand().fill().pad(5).uniform();
            controlPanel.add(setName).expand().fill().pad(5).uniform();
            controlPanel.add(cancelRename).expand().fill().pad(5).uniform();
        } else {
            controlPanel.add(rename).expand().fill().pad(5).uniform();
        }
        controlPanel.add(update).expand().fill().pad(5).uniform();

        linkBack.setPosition(5, height - linkBack.getPrefHeight() - 5);
        title.setPosition((width - title.getPrefWidth()) / 2, linkBack.getY());

        sortingTable.setSize(Math.min(width - 10, sortingTable.getPrefWidth() + 20), sortingTable.getPrefHeight());
        sortingTable.setPosition(5, height - title.getHeight() - sortingTable.getHeight() - 15);

        controlPanel.setPosition(5, 5);
        controlPanel.setSize(width - 10, controlPanel.getPrefHeight());

        pane.setPosition(5, controlPanel.getHeight() + 10);
        pane.setSize(width - 10, height - pane.getY() - title.getHeight() - 10 - sortingTable.getHeight() - 10);

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
        drawings.sort(sortingType.getValue().getComparator(isReverse.getValue()));
        files.clear();
        if (drawings.isEmpty()) {
            files.add(new Label(DynamicPlanimetry.translate("ui.load_file.no_files"), styles.getLabelStyle(Size.LARGE))).fill().center();
        } else {
            for (Drawing i : drawings) {
                if (i == null) {
                    Notifications.addNotification(DynamicPlanimetry.translate("error.load_file.null_loaded"), 100);
                    continue;
                }
                SaveEntry file = new SaveEntry(i.getName(), i.getCreationTime(), i.getLastEditTime(), i.getFilename().replace(Gdx.files.getLocalStoragePath(), ""), styles);
                file.setChecked(selection == i);
                file.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (System.currentTimeMillis() - lastClickTime < 300 && selection == i) {
                            app.setDrawing(i, true);
                            app.editorScreen.hide();
                            app.setScreen(DynamicPlanimetry.EDITOR_SCREEN);
                            return;
                        }
                        selection = i;
                        lastClickTime = System.currentTimeMillis();
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
    }
}
