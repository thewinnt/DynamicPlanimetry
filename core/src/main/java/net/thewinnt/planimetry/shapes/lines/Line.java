package net.thewinnt.planimetry.shapes.lines;

import net.thewinnt.planimetry.shapes.point.PointProvider;

public abstract class Line {
    public final PointProvider point1;
    public final PointProvider point2;

    public Line(PointProvider point1, PointProvider point2) {
        this.point1 = point1;
        this.point2 = point2;
    }

    
}
