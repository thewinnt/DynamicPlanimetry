package net.thewinnt.planimetry.ui.properties.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.ui.ComponentSelectBox;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.text.Component;

public class ShapeProperty extends Property<Shape> {
    private final List<Consumer<Shape>> listeners = new ArrayList<>();
    private final Drawing drawing;
    private Predicate<Shape> canConnect;
    private Shape attachment;

    public ShapeProperty(Component name, Drawing drawing, Shape current) {
        super(name);
        this.drawing = drawing;
        this.attachment = current;
    }

    public ShapeProperty(Component name, Drawing drawing, Shape current, Predicate<Shape> canConnect) {
        this(name, drawing, current);
        this.canConnect = canConnect;
    }

    public void setConnectionPredicate(Predicate<Shape> canConnect) {
        this.canConnect = canConnect;
    }

    @Override
    public void addValueChangeListener(Consumer<Shape> listener) {
        this.listeners.add(listener);
    }

    @Override
    public void setValue(Shape value) {
        if (canConnect.test(value)) {
            this.attachment = value;
            for (Consumer<Shape> i : this.listeners) {
                i.accept(value);
            }
        }
    }

    @Override
    public Shape getValue() {
        return attachment;
    }

    @Override
    public WidgetGroup getActorSetup(StyleSet styles) {
        Table output = new Table();
        Collection<Shape> shapes = this.drawing.allShapes.stream().filter(canConnect).toList();
        SelectBox<Shape> selector = new ComponentSelectBox<>(styles.getListStyle(Size.MEDIUM), shapes, shape -> shape.getName());
        selector.setSelected(attachment);
        selector.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setValue(selector.getSelected());
            }
        });
        
        output.add(selector).expand().fill();
        return output;
    }
}
