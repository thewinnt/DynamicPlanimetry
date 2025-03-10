package net.thewinnt.planimetry.data.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.Pair;
import net.thewinnt.planimetry.DynamicPlanimetry;

public record TagKey<T>(Identifier registry, Identifier id) {
    public static <T> TagKey<T> create(Registry<T> registry, Identifier id) {
        return new TagKey<>(registry.id(), id);
    }

    public static Pair<TagKey<?>, List<Identifier>> readJson(String filename) {
        // example: tags/ivlev_invasion/shape_type/example_tag.json resolves to [dynamic_planimetry:shape_type / ivlev_invasion:example_tag]
        String[] name = filename.split("[\\/\\\\]", 2);
        FileHandle file = Gdx.files.internal("tags/" + filename);
        Gdx.app.log("TagReader", Arrays.toString(name)); // debug
        Identifier registry = new Identifier(name[1]);
        Identifier id = new Identifier(name[0], name[2]);
        TagKey<?> tag = new TagKey<>(registry, id);
        List<Identifier> elements = new ArrayList<>();
        
        JsonObject object = DynamicPlanimetry.GSON.fromJson(file.readString("utf8"), JsonObject.class);
        JsonArray list = object.get("values").getAsJsonArray();
        for (JsonElement i : list) {
            elements.add(new Identifier(i.getAsString()));
        }
        return Pair.of(tag, elements);
    }
}
