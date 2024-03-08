package net.thewinnt.planimetry.data;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.badlogic.gdx.utils.StringBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.ComponentRepresentable;
import net.thewinnt.planimetry.ui.text.LiteralComponent;

public class Language implements ComponentRepresentable {
    public static final Language EMPTY = new Language("empty", "<empty language>", "", Map.of());
    private static Language FALLBACK = EMPTY;
    private final String id;
    private final String nameLocal;
    private final LiteralComponent name;
    private final String languageTag;
    private final Map<String, String> keys;

    public Language(String id, String nameLocal, String languageTag, Map<String, String> keys) {
        this.id = id;
        this.nameLocal = nameLocal;
        this.name = Component.literal(nameLocal);
        this.languageTag = languageTag;
        this.keys = keys;
    }

    public String getId() {
        return id;
    }

    public String getNameLocal() {
        return nameLocal;
    }

    public String getLanguageTag() {
        return languageTag;
    }

    public String get(String key) {
        if (keys.containsKey(key)) {
            return keys.get(key);
        } else if (FALLBACK.keys.containsKey(key)) {
            return FALLBACK.keys.get(key);
        }
        return key;
    }

    public String get(String key, Object... params) {
        if (keys.containsKey(key)) {
            return String.format(Locale.forLanguageTag(languageTag), keys.get(key), params);
        } else if (FALLBACK.keys.containsKey(key)) {
            return String.format(Locale.forLanguageTag(FALLBACK.languageTag), FALLBACK.keys.get(key), params);
        } else {
            return key;
        }
    }

    public boolean hasKey(String key) {
        return keys.containsKey(key);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Language ");
        builder.append(id);
        builder.append(" (");
        builder.append(nameLocal);
        builder.append("), locale ");
        builder.append(languageTag);
        builder.append(", translations: ");
        builder.append(keys);
        return builder.toString();
    }

    public static Language fromJson(String id, JsonObject json) {
        String locale;
        if (!json.has("locale")) {
            locale = "";
        } else {
            locale = json.get("locale").getAsString();
        }
        String name = json.get("name").getAsString();
        JsonObject data = json.getAsJsonObject("translations");
        Map<String, String> translations = new HashMap<>();
        for (Map.Entry<String, JsonElement> i : data.entrySet()) {
            if (i.getValue().isJsonPrimitive()) {
                translations.put(i.getKey(), i.getValue().getAsString());
            }
        }
        return new Language(id, name, locale, translations);
    }

    @Override
    public Component toComponent() {
        return name;
    }

    public static void setFallbackLanguage(Language fallback) {
        if (fallback != null) {
            Language.FALLBACK = fallback;
        }
    }
}
