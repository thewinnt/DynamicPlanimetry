package net.thewinnt.planimetry.data.registry;

import java.util.Objects;

public final class Identifier {
    public static final String DEFAULT_NAMESPACE = "dynamic_planimetry";
    public static final String ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789-_/";
    public static final String ALLOWED_CHARACTERS_NAMESPACE = "abcdefghijklmnopqrstuvwxyz0123456789-_";
    public final String namespace;
    public final String path;

    public Identifier(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }

    public Identifier(String path) {
        String[] outputs = path.split(":", 2);
        if (outputs.length == 1) {
            this.namespace = DEFAULT_NAMESPACE;
            this.path = validatePath(outputs[0]);
        } else {
            this.namespace = validateNamespace(outputs[0]);
            this.path = validatePath(outputs[1]);
        }
    }

    public static String validatePath(String path) {
        for (char i : path.toCharArray()) {
            if (!ALLOWED_CHARACTERS.contains(String.valueOf(i))) {
                throw new IllegalArgumentException("Invalid character in path: " + i);
            }
        }
        return path;
    }

    public static String validateNamespace(String namespace) {
        for (char i : namespace.toCharArray()) {
            if (!ALLOWED_CHARACTERS_NAMESPACE.contains(String.valueOf(i))) {
                throw new IllegalArgumentException("Invalid character in namespace: " + i);
            }
        }
        return namespace;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Identifier) obj;
        return Objects.equals(this.namespace, that.namespace) && Objects.equals(this.path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, path);
    }

    @Override
    public String toString() {
        return namespace + ":" + path;
    }

}
