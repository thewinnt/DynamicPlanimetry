package net.thewinnt.planimetry.ui.drawable;

import com.badlogic.gdx.graphics.g2d.Batch;

import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.scene2d.ShapeDrawerDrawable;

public abstract class DynamicIcon extends ShapeDrawerDrawable {
    public static ShapeDrawer drawer;
    protected float x, y;
    protected float size;

    public static final DynamicIcon EXIT_SIGN = new DynamicIcon() {
        @Override
        public void render(float x, float y, float size) {
            super.render(x, y, size);
            float[] path1 = new float[]{
                x(19), y(11),
                x(19), y(4),
                x(36), y(4),
                x(36), y(37),
                x(19), y(37),
                x(19), y(30)
            };
            float[] path2 = new float[]{
                x(23), y(14),
                x(30), y(21),
                x(23), y(28)
            };
            drawer.path(path1, size/42*2, JoinType.POINTY, true);
            drawer.line(x(5), y(21), x(31), y(21), size/42*2);
            drawer.path(path2, size/42*2, JoinType.SMOOTH, true);
        }
    };

    public static final DynamicIcon RIGHT_TRIANGLE = new DynamicIcon() {
        @Override
        public void render(float x, float y, float size) {
            super.render(x, y, size);
            drawer.path(new float[]{x(14), y(7), x(28), y(21), x(14), y(35)}, 3, JoinType.NONE, true);
            drawer.filledCircle(x(14), y(7), 1.5f);
            drawer.filledCircle(x(28), y(21), 1.5f);
            drawer.filledCircle(x(14), y(35), 1.5f);
        }
    };

    public static final DynamicIcon DOWN_TRIANGLE = new DynamicIcon() {
        @Override
        public void render(float x, float y, float size) {
            super.render(x, y, size);
            drawer.path(new float[]{x(7), y(14), x(21), y(28), x(35), y(14)}, 3, JoinType.NONE, true);
            drawer.filledCircle(x(7), y(14), 1.5f);
            drawer.filledCircle(x(21), y(28), 1.5f);
            drawer.filledCircle(x(35), y(14), 1.5f);
        }
    };

    public static final DynamicIcon CLOSE = new DynamicIcon() {
        @Override
        public void render(float x, float y, float size) {
            super.render(x, y, size);
            drawer.line(x(4), y(4), x(38), y(38), 3);
            drawer.line(x(4), y(38), x(38), y(4), 3);
        }
    };

    public static final DynamicIcon MINIMIZE = new DynamicIcon() {
        @Override
        public void render(float x, float y, float size) {
            super.render(x, y, size);
            drawer.line(x(4), y(21), x(38), y(21), 3);
        }
    };

    public void render(float x, float y, float size) {
        this.x = x;
        this.y = y;
        this.size = size;
    };

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        this.render(x, y, Math.min(width, height));
    }

    @Override
    public void drawShapes(ShapeDrawer shapeDrawer, float x, float y, float width, float height) {
        ShapeDrawer old = DynamicIcon.drawer;
        DynamicIcon.drawer = shapeDrawer;
        this.render(x, y, Math.min(width, height));
        DynamicIcon.drawer = old;
    }

    // local space to render space
    protected float x(float x) {
        return x * this.size/42 + this.x;
    }

    protected float y(float y) {
        return (42-y) * this.size/42 + this.y;
    }
}
