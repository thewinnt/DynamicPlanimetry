package net.thewinnt.planimetry.ui.text;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.util.FontProvider;

public interface Component {
    String toString();
    Vec2 draw(Batch batch, FontProvider font, int fontSize, Color color, float x, float y);
    Vec2 getSize(FontProvider font, int fontSize);

    public static Component empty() {
        return Empty.INSTANCE;
    }

    public static LiteralComponent literal(String value) {
        return value == null ? LiteralComponent.EMPTY : new LiteralComponent(value);
    }

    public static Component of(Component... components) {
        return new MultiComponent(List.of(components));
    }

    static class Empty implements Component {
        public static final Empty INSTANCE = new Empty();

        private Empty() {}

        @Override
        public String toString() {
            return "";
        }

        @Override
        public Vec2 getSize(FontProvider font, int fontSize) {
            return Vec2.ZERO;
        }

        @Override
        public Vec2 draw(Batch batch, FontProvider font, int fontSize, Color color, float x, float y) {
            return Vec2.ZERO;
        }
    }
}
