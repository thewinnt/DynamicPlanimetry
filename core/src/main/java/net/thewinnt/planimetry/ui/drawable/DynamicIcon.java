package net.thewinnt.planimetry.ui.drawable;

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
            drawer.filledTriangle(x(8), y(8), x(33), y(20.5f), x(8), y(33));
        }
    };
    
    public static final DynamicIcon DOWN_TRIANGLE = new DynamicIcon() {
        @Override
        public void render(float x, float y, float size) {
            super.render(x, y, size);
            drawer.filledTriangle(x(8), y(8), x(20.5f), y(33), x(33), y(8));
        }
    };

    public void render(float x, float y, float size) {
        this.x = x;
        this.y = y;
        this.size = size;
    };

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
