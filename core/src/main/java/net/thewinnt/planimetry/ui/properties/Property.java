package net.thewinnt.planimetry.ui.properties;

import java.util.Map;

import net.thewinnt.planimetry.ui.parameters.Parameter;

public abstract class Property<T> {
    public final String name;

    public Property(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract Map<Parameter<?>, String> getParameters();
    public abstract T buildResult();
}
