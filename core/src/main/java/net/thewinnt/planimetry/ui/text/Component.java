package net.thewinnt.planimetry.ui.text;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.util.FontProvider;

public interface Component extends ComponentRepresentable {
    String toString();
    Vec2 draw(Batch batch, FontProvider font, Size size, Color color, float x, float y);
    Vec2 getSize(FontProvider font, Size size);
    /**
     * @deprecated use {@link #toNbt()} for actual saving
     */
    @Deprecated CompoundTag writeNbt();
    ComponentDeserializer<?> getDeserializer();

    public default CompoundTag toNbt() {
        CompoundTag nbt = this.writeNbt();
        nbt.putString("type", ComponentRegistry.getComponentType(this.getDeserializer()));
        return nbt;
    }

    public static Component fromNbt(CompoundTag nbt) {
        String type = nbt.getString("type").getValue();
        return ComponentRegistry.getDeserializer(type).deserialize(nbt);
    }

    public static Component empty() {
        return Empty.INSTANCE;
    }

    public static LiteralComponent literal(String value) {
        return value == null ? LiteralComponent.EMPTY : new LiteralComponent(value);
    }

    public static LiteralComponent number(double value) {
        return new LiteralComponent(DynamicPlanimetry.formatNumber(value));
    }

    public static Component of(Component... components) {
        return new MultiComponent(List.of(components));
    }

    public static Component optional(Component component) {
        return component == null ? Empty.INSTANCE : component;
    }

    @Override
    default Component toComponent() {
        return this;
    }

    static class Empty implements Component {
        public static final Empty INSTANCE = new Empty();

        private Empty() {}

        @Override
        public String toString() {
            return "";
        }

        @Override
        public Vec2 getSize(FontProvider font, Size size) {
            return Vec2.ZERO;
        }

        @Override
        public Vec2 draw(Batch batch, FontProvider font, Size size, Color color, float x, float y) {
            return Vec2.ZERO;
        }

        @Override
        public CompoundTag writeNbt() {
            return new CompoundTag();
        }

        @Override
        public ComponentDeserializer<?> getDeserializer() {
            return ComponentRegistry.EMPTY;
        }

        public static Empty readNbt(CompoundTag nbt) {
            return INSTANCE;
        }
    }

    @FunctionalInterface
    public static interface ComponentDeserializer<T extends Component> {
        T deserialize(CompoundTag nbt);
    }
}
