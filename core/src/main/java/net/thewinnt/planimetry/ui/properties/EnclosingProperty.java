package net.thewinnt.planimetry.ui.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnclosingProperty extends Property<Property<?>> {
    private final List<Property<?>> properties = new ArrayList<>();

    public EnclosingProperty(String name, Property<?>... properties) {
        super(name);
        for (Property<?> i : properties) {
            this.properties.add(i);
        }
    }

    public EnclosingProperty(String name, Collection<Property<?>> properties) {
        super(name);
        this.properties.addAll(properties);
    }

    @Override
    public Property<?> buildResult() {
        throw new UnsupportedOperationException("An EnclosingProperty does not have a result");
    }

    @Override
    public Map<Parameter<?>, String> getParameters() {
        Map<Parameter<?>, String> output = new HashMap<>();
        for (Property<?> i : this.properties) {
            output.putAll(i.getParameters());
        }
        return output;
    }

    @Override
    public void addValueChangeListener(Runnable listener) {
        for (Property<?> i : this.properties) {
            i.addValueChangeListener(listener);
        }
    }
}
