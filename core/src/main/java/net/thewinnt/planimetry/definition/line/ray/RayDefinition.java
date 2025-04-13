package net.thewinnt.planimetry.definition.line.ray;

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
import net.thewinnt.planimetry.ui.properties.PropertySupplier;
import net.thewinnt.planimetry.ui.text.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

public abstract class RayDefinition implements PropertySupplier {
    private Ray source;

    public abstract Vec2 start();
    public abstract double direction();
    public abstract boolean canMove();
    public abstract void move(double dx, double dy);
    public abstract Component getName();
    public abstract void replaceShape(Shape old, Shape neo);
    public abstract RayDefinitionType<?> type();
    public abstract InfiniteLine asInfiniteLine(Drawing drawing);
    public abstract LineSegment asLineSegment(Drawing drawing);
    public abstract List<Shape> dependencies();

    public void move(Vec2 delta) {
        this.move(delta.x, delta.y);
    }

    public Ray getSource() {
        return source;
    }

    @ApiStatus.Internal
    public void setSource(Ray source) {
        this.source = source;
    }

    public final CompoundTag toNbt(SavingContext context) {
        CompoundTag output = this.type().writeNbt(this, context);
        output.putString("type", Registries.RAY_DEFITINION_TYPE.getName(this.type()).toString());
        return output;
    }

    public static RayDefinition fromNbt(CompoundTag nbt, LoadingContext context) {
        RayDefinitionType<?> type = Registries.RAY_DEFITINION_TYPE.get(new Identifier(nbt.getString("type")));
        return type.fromNbt(nbt, context);
    }
}
