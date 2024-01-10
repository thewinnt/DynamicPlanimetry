package net.thewinnt.planimetry.ui.properties.layout;

import com.badlogic.gdx.scenes.scene2d.Actor;

import net.thewinnt.planimetry.ui.properties.PropertyEntry;

public class BasicLayout implements CustomLayout {
    public static final BasicLayout INSTANCE = new BasicLayout();

    @Deprecated public BasicLayout() {}

    @Override
    public void layout(Actor actor, PropertyEntry entry) {
        actor.setBounds((int)Math.ceil(entry.getNameLabel().getPrefWidth() / 25) * 25, 2, entry.getWidth() - (int)Math.ceil(entry.getNameLabel().getPrefWidth() / 25) * 25, entry.getHeight() - 4);
    }
}
