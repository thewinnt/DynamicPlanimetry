package net.thewinnt.planimetry.ui.properties.layout;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Align;

import net.thewinnt.planimetry.ui.properties.Entry;

public class RightAlignedLayout implements CustomLayout {
    public static final RightAlignedLayout INSTANCE = new RightAlignedLayout();

    @Deprecated public RightAlignedLayout() {}

    @Override
    public void layout(Actor actor, Entry entry) {
        float x = entry.getWidth();
        if (actor instanceof Layout layout) {
            float width = layout.getPrefWidth();
            actor.setBounds(x - width, 0, width, entry.getHeight());
        } else {
            actor.setPosition(x, 0, Align.bottomRight);
        }
    }
}
