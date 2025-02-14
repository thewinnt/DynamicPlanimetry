package net.thewinnt.planimetry.shapes;

import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.util.ShapeReader;
import net.thewinnt.planimetry.util.ShapeWriter;

public interface ShapeDefinitionType<T extends G, G> extends ShapeReader<T>, ShapeWriter<G> {
    T convert(G other, Drawing drawing);
}
