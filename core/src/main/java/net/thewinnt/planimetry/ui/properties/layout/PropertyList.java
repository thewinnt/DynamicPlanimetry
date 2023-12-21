package net.thewinnt.planimetry.ui.properties.layout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.thewinnt.planimetry.ui.ComponentLabel;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.Theme;
import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.ui.drawable.DynamicIcon;
import net.thewinnt.planimetry.ui.drawable.RectangleDrawable;
import net.thewinnt.planimetry.ui.drawable.RectangledIconDrawable;
import net.thewinnt.planimetry.ui.text.Component;

public class PropertyList extends ScrollPane {
    private final VerticalGroup group;
    private final PropertyLayout propertyLayout;
    private final ButtonStyle style;
    private final Button toggle;
    private final Component name;
    private boolean isOpen;

    public PropertyList(PropertyLayout layout, Component name, StyleSet styles) {
        super(null, styles.getScrollPaneStyleNoBg());

        this.setFillParent(true);
        this.setupOverscroll(Gdx.graphics.getHeight() / Size.MEDIUM.factor * 1.1f, 10, 200);

        this.group = new VerticalGroup().left().expand().fill().pad(2, 5, 2, 5);
        this.group.setFillParent(true);

        this.propertyLayout = layout;
        this.name = name;

        // styles init
        RectangleDrawable normal = new RectangledIconDrawable(styles.drawer, DynamicIcon.RIGHT_TRIANGLE).withColors(Theme.current().button(), Theme.current().outline());
        RectangleDrawable pressed = new RectangledIconDrawable(styles.drawer, DynamicIcon.RIGHT_TRIANGLE).withColors(Theme.current().pressed(), Theme.current().outline());
        RectangleDrawable over = new RectangledIconDrawable(styles.drawer, DynamicIcon.RIGHT_TRIANGLE).withColors(Theme.current().main(), Theme.current().outline());
        RectangleDrawable normalOpen = new RectangledIconDrawable(styles.drawer, DynamicIcon.DOWN_TRIANGLE).withColors(Theme.current().button(), Theme.current().outline());
        RectangleDrawable pressedOpen = new RectangledIconDrawable(styles.drawer, DynamicIcon.DOWN_TRIANGLE).withColors(Theme.current().pressed(), Theme.current().outline());
        RectangleDrawable overOpen = new RectangledIconDrawable(styles.drawer, DynamicIcon.DOWN_TRIANGLE).withColors(Theme.current().main(), Theme.current().outline());

        this.style = new ButtonStyle(normal, pressed, normalOpen);
        this.style.over = over;
        this.style.checkedDown = pressedOpen;
        this.style.checkedOver = overOpen;

        this.toggle = new Button(this.style);
        this.toggle.add(new ComponentLabel(this.name, styles.font, Gdx.graphics.getHeight() / Size.MEDIUM.factor)).padLeft(Gdx.graphics.getHeight() / Size.MEDIUM.factor);
        this.toggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isOpen = toggle.isChecked();
                if (isOpen) {
                    group.addActor(propertyLayout);
                } else {
                    group.removeActor(propertyLayout);
                }
            }
        });
        this.group.addActor(this.toggle);
        this.setActor(this.group);
        // TODO actual layout - do not create new actors, just add/remove the layout when opened/closed
    }
}
