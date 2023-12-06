package net.thewinnt.planimetry.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import net.thewinnt.planimetry.ui.StyleSet.Size;

public class SaveEntry extends Button {
    public static final DateFormat TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private Label nameLabel;

    public SaveEntry(String name, long creationTime, long editTime, String filename, StyleSet styles) {
        super(styles.getButtonStyle(Size.MEDIUM, true));
        this.nameLabel = new Label(name, styles.getLabelStyle(Size.MEDIUM));
        Label creationTimeLabel = new Label("Время создания: " + TIME_FORMAT.format(new Date(creationTime)), styles.getLabelStyle(Size.SMALL));
        Label editTimeLabel = new Label("Последнее изменение: " + TIME_FORMAT.format(new Date(editTime)), styles.getLabelStyle(Size.SMALL));
        Label filenameLabel = new Label("(" + filename + ")", styles.getLabelStyle(Size.SMALL));
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
