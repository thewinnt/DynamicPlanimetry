package net.thewinnt.planimetry.definition.line.infinite;

import org.jetbrains.annotations.ApiStatus.Internal;

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
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.PropertySupplier;
import net.thewinnt.planimetry.ui.text.Component;

import java.util.List;

public abstract class InfiniteLineDefinition implements PropertySupplier {
    private InfiniteLine source;

    public abstract Vec2 point1();
    public abstract Vec2 point2();
    public abstract boolean canMove();
    public abstract void move(double dx, double dy);
    public abstract Component getName();
    public abstract void replaceShape(Shape old, Shape neo);
    public abstract InfiniteLineType<?> type();
    public abstract LineSegment asLineSegment(Drawing drawing);
    public abstract Ray asRay(Drawing drawing);
    public abstract PointProvider getBasePoint();
    public abstract List<Shape> dependencies();

    public void move(Vec2 delta) {
        this.move(delta.x, delta.y);
    }

    public InfiniteLine getSource() {
        return source;
    }

    @Internal
    public void setSource(InfiniteLine line) {
        this.source = line;
    }

    public Component createProperty(String postfix) {
        return Component.translatable(source.getPropertyName(postfix));
    }

    public final CompoundTag toNbt(SavingContext context) {
        CompoundTag output = this.type().writeNbt(this, context);
        output.putString("type", Registries.INFINITE_LINE_DEFINITION_TYPE.getName(this.type()).toString());
        return output;
    }

    public static InfiniteLineDefinition fromNbt(CompoundTag nbt, LoadingContext context) {
        InfiniteLineType<?> type = Registries.INFINITE_LINE_DEFINITION_TYPE.get(new Identifier(nbt.getString("type")));
        return type.fromNbt(nbt, context);
    }
}
