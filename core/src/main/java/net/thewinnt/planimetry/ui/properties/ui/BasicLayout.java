package net.thewinnt.planimetry.ui.properties.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class BasicLayout implements CustomLayout {
    public static final BasicLayout INSTANCE = new BasicLayout();

    @Deprecated public BasicLayout() {}

    @Override
    public void layout(Actor actor, Entry entry) {
        actor.setBounds((int)Math.ceil(entry.getNameLabel().getPrefWidth() / 25) * 25, 0, entry.getWidth() - (int)Math.ceil(entry.getNameLabel().getPrefWidth() / 25) * 25, entry.getHeight());
    }
}
