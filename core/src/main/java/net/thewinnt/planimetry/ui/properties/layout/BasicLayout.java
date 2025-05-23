package net.thewinnt.planimetry.ui.properties.layout;

import com.badlogic.gdx.scenes.scene2d.Actor;

import net.thewinnt.planimetry.ui.properties.PropertyEntry;

public class BasicLayout implements CustomLayout {
    public static final BasicLayout INSTANCE = new BasicLayout();

    private BasicLayout() {}

    @Override
    public void layout(Actor actor, PropertyEntry entry) {
        int length = (int) Math.min(Math.ceil(entry.getNameLabel().getPrefWidth() / 25) * 25, entry.getWidth() / 2);
        actor.setBounds(length, 2, entry.getWidth() - length, entry.getHeight() - 4);
    }
}
