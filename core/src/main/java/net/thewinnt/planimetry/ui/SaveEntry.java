package net.thewinnt.planimetry.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

public class SaveEntry extends Button {
    public static final DateFormat TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private Label nameLabel;

    public SaveEntry(String name, long creationTime, long editTime, String filename, StyleSet styles) {
        super(styles.getButtonStyle());
        this.nameLabel = new Label(name, styles.getLabelStyleLarge());
        Label creationTimeLabel = new Label("Время создания: " + TIME_FORMAT.format(new Date(creationTime)), styles.getLabelStyleSmall());
        Label editTimeLabel = new Label("Последнее изменение: " + TIME_FORMAT.format(new Date(editTime)), styles.getLabelStyleSmall());
        Label filenameLabel = new Label("(" + filename + ")", styles.getLabelStyleSmall());
        this.add(nameLabel).padLeft(5).padRight(10);
        this.add(filenameLabel).padRight(5).expand().fill().row();
        this.add(creationTimeLabel).padLeft(5).padRight(5).colspan(2).align(Align.left).row();
        this.add(editTimeLabel).padLeft(5).padRight(5).colspan(2).align(Align.left).row();
    }

    public void setName(String name) {
        this.nameLabel.setText(name);
        this.invalidateHierarchy();
    }
}
