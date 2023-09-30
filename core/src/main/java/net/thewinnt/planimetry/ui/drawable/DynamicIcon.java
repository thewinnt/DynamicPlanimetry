package net.thewinnt.planimetry.ui.drawable;

import com.badlogic.gdx.graphics.Color;

import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.scene2d.ShapeDrawerDrawable;

public abstract class DynamicIcon extends ShapeDrawerDrawable {
    public static ShapeDrawer drawer;
    protected float x, y;
    protected float size;

    public static final DynamicIcon CROSS = new DynamicIcon() {
        @Override
        public void render(float x, float y, float size) {
            super.render(x, y, size);
            float old = drawer.getPackedColor();
            float[] shape = new float[]{
                x(1), y(6),
                x(6), y(1),
                x(7), y(1),
                x(20), y(14),
                x(21), y(14),
                x(34), y(1),
                x(35), y(1),
                x(40), y(6),
                x(40), y(7),
                x(27), y(20),
                x(27), y(21),
                x(40), y(34),
                x(40), y(35),
                x(35), y(40),
                x(34), y(40),
                x(21), y(27),
                x(20), y(27),
                x(7), y(40),
                x(6), y(40),
                x(1), y(35),
                x(1), y(34),
                x(14), y(21),
                x(14), y(20),
                x(1), y(7)
            };
            drawer.setColor(new Color(0xd86344ff));
            drawer.filledPolygon(shape);
            drawer.setColor(new Color(0xa74629ff));
            drawer.polygon(shape, size/21, JoinType.SMOOTH);
            drawer.setColor(old);
        }
    };

    public static final DynamicIcon EXCLAMATION = new DynamicIcon() {
        @Override
        public void render(float x, float y, float size) {
            super.render(x, y, size);
            float[] shape = new float[]{
                x(16), y(0),
                x(24), y(0),
                x(23), y(31),
                x(17), y(31)
            };
            float old = drawer.getPackedColor();
            drawer.setColor(new Color(0xedc87eff));
            drawer.filledPolygon(shape);
            drawer.filledCircle(x(20), y(37), 4*size/42);
            drawer.setColor(new Color(0xa4802bff));
            drawer.polygon(shape, size/21, JoinType.SMOOTH);
            drawer.circle(x(20), y(37), 4*size/42, size/21);
            drawer.setColor(old);
        }
    };

    public static final DynamicIcon TICK = new DynamicIcon() {
        @Override
        public void render(float x, float y, float size) {
            super.render(x, y, size);
            float old = drawer.getPackedColor();
            float[] shape = new float[]{
                x(-2), y(20),
                x(3), y(15),
                x(4), y(15),
                x(15), y(27),
                x(16), y(27),
                x(37), y(0),
                x(43), y(5),
                x(16), y(39),
                x(15), y(39),
                x(-2), y(21)
            };
            drawer.setColor(new Color(0x76a797ff));
            drawer.filledPolygon(shape);
            drawer.setColor(new Color(0x3a7662ff));
            drawer.polygon(shape, size/21, JoinType.SMOOTH);
            drawer.setColor(old);
        }
    };

    public static final DynamicIcon DOWN_ARROW = new DynamicIcon() {
        @Override
        public void render(float x, float y, float size) {
            super.render(x, y, size);
            float old = drawer.getPackedColor();
            float[] shape = new float[]{
                x(0), y(8),
                x(41), y(8),
                x(41), y(10),
                x(21), y(30),
                x(20), y(30),
                x(0), y(10)
            };
            drawer.setColor(new Color(0xd86344ff));
            drawer.filledPolygon(shape);
            drawer.setColor(new Color(0x9e381cff));
            drawer.polygon(shape, size/21, JoinType.SMOOTH);
            drawer.setColor(old);
        }
    };

    public static final DynamicIcon MINUS = new DynamicIcon() {
        @Override
        public void render(float x, float y, float size) {
            super.render(x, y, size);
            drawer.filledRectangle(x(0), y(27), 42*size/42, 14*size/42, new Color(0xedc37eff));
            drawer.rectangle(x(0), y(27), 42*size/42, 14*size/42, new Color(0xa4802bff), size/21);
        }
    };

    public static final DynamicIcon UP_ARROW = new DynamicIcon() {
        @Override
        public void render(float x, float y, float size) {
            super.render(x, y, size);
            float old = drawer.getPackedColor();
            float[] shape = new float[]{
                x(0), y(33),
                x(41), y(33),
                x(41), y(31),
                x(21), y(11),
                x(20), y(11),
                x(0), y(31)
            };
            drawer.setColor(new Color(0x76a797ff));
            drawer.filledPolygon(shape);
            drawer.setColor(new Color(0x326f5bff));
            drawer.polygon(shape, size/21, JoinType.SMOOTH);
            drawer.setColor(old);
        }
    };

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
