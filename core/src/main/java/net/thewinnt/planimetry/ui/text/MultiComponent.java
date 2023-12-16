package net.thewinnt.planimetry.ui.text;

import java.util.Collection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.util.FontProvider;

public class MultiComponent implements Component {
    private final Collection<Component> components;

    public MultiComponent(Collection<Component> components) {
        this.components = components;
    }

    @Override
    public Vec2 getSize(FontProvider font, int fontSize) {
        double width = 0;
        double height = 0;
        for (Component i : this.components) {
            Vec2 size = i.getSize(font, fontSize);
            width += size.x;
            height = Math.max(height, size.y);
        }
        return new Vec2(width, height);
    }

    @Override
    public Vec2 draw(Batch batch, FontProvider font, int fontSize, Color color, float x, float y) {
        float offx = 0;
        double height = 0;
        for (Component i : this.components) {
            Vec2 size = i.draw(batch, font, fontSize, color, x + offx, y);
            offx += (float)size.x;
            height = Math.max(height, size.y);
        }
        return new Vec2(offx, y);
    }
}
