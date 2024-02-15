package net.thewinnt.planimetry.settings;

import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.ComponentRepresentable;

import java.util.function.Predicate;

public enum ShapeMovementPredicate implements ComponentRepresentable, Predicate<Shape> {
    ALL(Component.literal("Все фигуры"), shape -> true),
    ONLY_POINTS(Component.literal("Только точки"), shape -> shape instanceof PointProvider),
    ONLY_NON_POINTS(Component.literal("Все, кроме точек"), shape -> !(shape instanceof PointProvider)),
    NONE(Component.literal("Ничего"), shape -> false);

    private final Component name;
    private final Predicate<Shape> condition;

    private ShapeMovementPredicate(Component name, Predicate<Shape> condition) {
        this.name = name;
        this.condition = condition;
    }

    @Override
    public Component toComponent() {
        return name;
    }

    @Override
    public boolean test(Shape shape) {
        return condition.test(shape);
    }
}
