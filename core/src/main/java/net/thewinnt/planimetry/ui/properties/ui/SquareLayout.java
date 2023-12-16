package net.thewinnt.planimetry.ui.properties.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class SquareLayout implements CustomLayout {
    public static final SquareLayout INSTANCE = new SquareLayout();

    @Deprecated public SquareLayout() {}
    @Override
    public void layout(Actor actor, Entry entry) {
        actor.setBounds(entry.getWidth() - entry.getHeight(), 2, entry.getHeight(), entry.getHeight() - 4);
    }
}
