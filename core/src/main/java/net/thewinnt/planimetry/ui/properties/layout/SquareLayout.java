package net.thewinnt.planimetry.ui.properties.layout;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class SquareLayout implements CustomLayout {
    public static final SquareLayout INSTANCE = new SquareLayout();

    @Deprecated public SquareLayout() {}
    @Override
    public void layout(Actor actor, PropertyEntry entry) {
        actor.setBounds(entry.getWidth() - entry.getHeight() + 4, 2, entry.getHeight() - 4, entry.getHeight() - 4);
    }
}
