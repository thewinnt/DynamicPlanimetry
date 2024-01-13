package net.thewinnt.planimetry.ui.text;

import java.util.ArrayList;
import java.util.Collection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

import dev.dewy.nbt.tags.collection.CompoundTag;
import dev.dewy.nbt.tags.collection.ListTag;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.util.FontProvider;

public class MultiComponent implements Component {
    private final Collection<Component> components;

    public MultiComponent(Collection<Component> components) {
        this.components = components;
    }

    @Override
    public Vec2 getSize(FontProvider font, int fontSize) {
        double width = 0;
        double height = 0;
        for (Component i : this.components) {
            Vec2 size = i.getSize(font, fontSize);
            width += size.x;
            height = Math.max(height, size.y);
        }
        return new Vec2(width, height);
    }

    @Override
    public Vec2 draw(Batch batch, FontProvider font, int fontSize, Color color, float x, float y) {
        float offx = 0;
        double height = 0;
        for (Component i : this.components) {
            Vec2 size = i.draw(batch, font, fontSize, color, x + offx, y);
            offx += (float)size.x;
            height = Math.max(height, size.y);
        }
        return new Vec2(offx, y);
    }

    @Override
    public CompoundTag writeNbt() {
        CompoundTag output = new CompoundTag();
        ListTag<CompoundTag> list = new ListTag<>();
        for (Component i : this.components) {
            list.add(i.toNbt());
        }
        output.put("components", list);
        return output;
    }

    @Override
    public ComponentDeserializer<?> getDeserializer() {
        return ComponentRegistry.MULTIPLE;
    }

    public static MultiComponent readNbt(CompoundTag nbt) {
        ListTag<CompoundTag> list = nbt.getList("components");
        ArrayList<Component> components = new ArrayList<>(list.size());
        for (CompoundTag i : list) {
            components.add(Component.fromNbt(i));
        }
        return new MultiComponent(components);
    }
}
