package net.thewinnt.planimetry.ui;

import com.badlogic.gdx.graphics.Color;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.ComponentRepresentable;

public record Theme(
    Component name,
    Color main,
    Color pressed,
    Color button,
    Color outline,
    Color textButton,
    Color textUI,
    Color textInactive,
    Color textField,
    Color textSelectionBg,
    Color delimiter,
    Color inactive,
    Color shape,
    Color shapeHovered,
    Color shapeSelected,
    Color point,
    Color pointHovered,
    Color pointSelected,
    Color utilityPoint,
    Color utilityPointHovered,
    Color utilityPointSelected,
    Color gridLine,
    Color gridHint,
    Color gridCenter,
    Color angleMarker,
    Color textAngleMarker,
    Color selectionOutline,
    Color selectionFill,
    Color closeButton,
    Color closeButtonHover,
    Color closeButtonPress
) implements ComponentRepresentable {
    public static Theme current() {
        return DynamicPlanimetry.SETTINGS.getTheme();
    }

    @Override
    public Component toComponent() {
        return name;
    }
}
