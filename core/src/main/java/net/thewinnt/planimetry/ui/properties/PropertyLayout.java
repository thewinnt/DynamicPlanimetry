package net.thewinnt.planimetry.ui.properties;

import java.util.Collection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.properties.types.Property;

public class PropertyLayout extends ScrollPane {
    private final VerticalGroup propertyList;

    public PropertyLayout(Collection<Property<?>> properties, StyleSet styles) {
        super(null, styles.getScrollPaneStyleNoBg());
        
        setupOverscroll(Gdx.graphics.getHeight() / 18 * 1.1f, 10, 200);
        
        this.propertyList = new VerticalGroup().left().expand().fill().pad(2, 5, 2, 5);
        this.propertyList.setFillParent(true);
        for (Property<?> i : properties) {
            this.propertyList.addActor(new Entry(i, styles));
        }
        this.setActor(propertyList);
    }
}
