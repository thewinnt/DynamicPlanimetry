package net.thewinnt.planimetry.shapes.lines.definition.infinite;

import java.util.Collection;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.data.registry.Identifier;
import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.lines.InfiniteLine;
import net.thewinnt.planimetry.shapes.lines.LineSegment;
import net.thewinnt.planimetry.shapes.lines.Ray;
import net.thewinnt.planimetry.shapes.lines.definition.ray.RayDefinition;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.text.Component;

public abstract class InfiniteLineDefinition {
    public abstract Vec2 point1();
    public abstract Vec2 point2();
    public abstract boolean canMove();
    public abstract void move(double dx, double dy);
    public abstract Component getName();
    public abstract void replaceShape(Shape old, Shape neo);
    public abstract Collection<Property<?>> properties();
    public abstract InfiniteLineType<?> type();
    public abstract LineSegment asLineSegment(Drawing draiwng);
    public abstract Ray asRay(Drawing draiwng);

    public void move(Vec2 delta) {
        this.move(delta.x, delta.y);
    }

    public final CompoundTag toNbt(SavingContext context) {
        CompoundTag output = this.type().writeNbt(this, context);
        output.putString("type", Registries.INFINITE_LINE_DEFINITION_TYPE.getName(this.type()).toString());
        return output;
    }

    public static InfiniteLineDefinition fromNbt(CompoundTag nbt, LoadingContext context) {
        InfiniteLineType<?> type = Registries.INFINITE_LINE_DEFINITION_TYPE.byName(new Identifier(nbt.getString("type")));
        return type.fromNbt(nbt, context);
    }
    // TODO line conversions
}
