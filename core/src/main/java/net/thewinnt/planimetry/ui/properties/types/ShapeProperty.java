package net.thewinnt.planimetry.ui.properties.types;

import java.util.Collection;
import java.util.function.Predicate;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.ui.ComponentSelectBox;
import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.text.Component;

public class ShapeProperty extends Property<Shape> {
    private final Drawing drawing;
    private Predicate<Shape> predicate;

    public ShapeProperty(Component name, Drawing drawing, Shape current) {
        super(name, current);
        this.drawing = drawing;
    }

    public ShapeProperty(Component name, Drawing drawing, Shape current, Predicate<Shape> predicate) {
        this(name, drawing, current);
        this.predicate = predicate;
    }

    public void setConnectionPredicate(Predicate<Shape> predicate) {
        this.predicate = predicate;
    }
    
    @Override
    public boolean filterValue(Shape value) {
        return this.predicate.test(value);
    }

    @Override
    public WidgetGroup getActorSetup(StyleSet styles, Size size) {
        Table output = new Table();
        Collection<Shape> shapes = this.drawing.allShapes.stream().filter(predicate).toList();
        SelectBox<Shape> selector = new ComponentSelectBox<>(styles.getListStyle(size), shapes, Shape::getName, size);
        selector.setSelected(value);
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
