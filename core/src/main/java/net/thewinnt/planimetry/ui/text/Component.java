package net.thewinnt.planimetry.ui.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.Settings;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.util.FontProvider;

import org.jetbrains.annotations.ApiStatus;

public interface Component extends ComponentRepresentable {
    String toString();
    Vec2 drawGetSize(Batch batch, FontProvider font, Size size, Color color, float x, float y);
    void draw(Batch batch, FontProvider font, Size size, Color color, float x, float y);
    Vec2 getSize(FontProvider font, Size size);
    /**
     * @deprecated use {@link #toNbt()} for actual saving
     */
    @Deprecated
    @ApiStatus.OverrideOnly
    CompoundTag writeNbt();
    ComponentDeserializer<?> getDeserializer();

    default boolean canCache() {
        return false;
    }

    default BitmapFontCache createCache(FontProvider font, Size size, Color color) {
        return null;
    }

    default CompoundTag toNbt() {
        CompoundTag nbt = this.writeNbt();
        nbt.putString("type", Components.getComponentType(this.getDeserializer()).toString());
        return nbt;
    }

    static Component fromNbt(CompoundTag nbt) {
        String type = nbt.getString("type");
        return Components.getDeserializer(type).deserialize(nbt);
    }

    static Component empty() {
        return Empty.INSTANCE;
    }

    static LiteralComponent literal(String value) {
        return value == null ? LiteralComponent.EMPTY : new LiteralComponent(value);
    }

    static LiteralComponent literal(String value, Formatting formatting) {
        return value == null ? LiteralComponent.EMPTY : new LiteralComponent(value, formatting);
    }

    static LiteralComponent number(double value) {
        return new LiteralComponent(DynamicPlanimetry.formatNumber(value));
    }

    static LiteralComponent angle(double radians) {
        return new LiteralComponent(DynamicPlanimetry.formatNumber(Settings.get().toUnit(radians)) + Settings.get().getAngleUnit().getUnit());
    }

    static Component of(Component... components) {
        return new MultiComponent(Arrays.asList(components));
    }

    static Component optional(Component component) {
        return component == null ? Empty.INSTANCE : component;
    }

    static SimpleTranslatableComponent translatable(String key) {
        return new SimpleTranslatableComponent(key);
    }

    static SimpleTranslatableComponent translatable(String key, Formatting formatting) {
        return new SimpleTranslatableComponent(key, formatting);
    }

    static Component translatable(String key, Object... args) {
        boolean isMulti = false;
        for (Object i : args) {
            if (i instanceof Component) {
                isMulti = true;
                break;
            }
        }
        if (isMulti) {
            Object[] swapped = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Component) {
                    swapped[i] = "\ue631" + i + "\ue632"; // i picked these numbers randomly
                } else {
                    swapped[i] = args[i];
                }
            }
            String[] results = DynamicPlanimetry.translate(key, swapped).split("\ue631");
            List<Component> components = new ArrayList<>(results.length);
            for (String i : results) {
                if (i.contains("\ue632")) {
                    String[] pair = i.split("\ue632");
                    int index = Integer.parseInt(pair[0]);
                    components.add(((Component) args[index])); // we checked it before, so it's safe
                    if (pair.length > 1 && !pair[1].isBlank()) {
                        components.add(new LiteralComponent(pair[1]));
                    }
                } else {
                    components.add(new LiteralComponent(i));
                }
            }
            return new MultiComponent(components);
        }
        return new ComplexTranslatableComponent(key, args);
    }

    @Override
    default Component toComponent() {
        return this;
    }

    class Empty implements Component {
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
        public Vec2 drawGetSize(Batch batch, FontProvider font, Size size, Color color, float x, float y) {
            return Vec2.ZERO;
        }

        @Override
        public void draw(Batch batch, FontProvider font, Size size, Color color, float x, float y) {}

        @Override
        public CompoundTag writeNbt() {
            return new CompoundTag();
        }

        @Override
        public ComponentDeserializer<?> getDeserializer() {
            return Components.EMPTY;
        }

        public static Empty readNbt(CompoundTag nbt) {
            return INSTANCE;
        }
    }

    @FunctionalInterface
    interface ComponentDeserializer<T extends Component> {
        T deserialize(CompoundTag nbt);
    }
}
