package net.thewinnt.planimetry.shapes;

import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Circle extends Shape {
    public PointProvider center;
    public double radius;

    public Circle(PointProvider center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    @Override
    public boolean contains(double x, double y) {
        return center.getPosition().distanceTo(x, y) < Math.pow(2, -23);
    }

    @Override
    public boolean contains(Vec2 point) {
        return center.getPosition().distanceTo(point) < Math.pow(2, -23);
    }

    @Override
    public double distanceToMouse(Vec2 point, DrawingBoard board) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double distanceToMouse(double x, double y, DrawingBoard board) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        // TODO Auto-generated method stub
        
    }
}
