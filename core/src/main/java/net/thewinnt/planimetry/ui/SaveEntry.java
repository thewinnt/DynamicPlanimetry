package net.thewinnt.planimetry.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.ui.text.Component;

public class SaveEntry extends Button {
    public static final DateFormat TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private Label nameLabel;
    public final String name;
    public final long creationTime;
    public final long editTime;
    public final String filename;

    public SaveEntry(String name, long creationTime, long editTime, String filename, StyleSet styles) {
        super(styles.getButtonStyleToggleable(Size.MEDIUM));
        this.name = name;
        this.creationTime = creationTime;
        this.editTime = editTime;
        this.filename = filename;
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

    public static enum SortingType {
        BY_NAME(Component.literal("По названию"), (o1, o2) -> o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase())),
        BY_CREATION_TIME(Component.literal("По времени создания"), Comparator.comparingLong(value -> value.getCreationTime())),
        BY_EDITING_TIME(Component.literal("По последнему изменению"), Comparator.comparingLong(value -> value.getLastEditTime())),
        BY_FILE_NAME(Component.literal("По имени файла"), (o1, o2) -> o1.getFilename().compareTo(o2.getFilename()));

        private final CharSequence name;
        private final Comparator<Drawing> comparator;
        private SortingType(CharSequence name, Comparator<Drawing> comparator) {
            this.name = name;
            this.comparator = comparator;
        }

        @Override
        public String toString() {
            return name.toString();
        }

        public Comparator<Drawing> getComparator(boolean reversed) {
            return reversed ? comparator.reversed() : comparator;
        }
    }
}
