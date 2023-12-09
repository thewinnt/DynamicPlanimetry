package net.thewinnt.planimetry.ui;

import com.badlogic.gdx.graphics.Color;

import net.thewinnt.planimetry.DynamicPlanimetry;

public record Theme(
    Color main,
    Color pressed,
    Color button,
    Color outline,
    Color textButton,
    Color textUI,
    Color textInactive,
    Color textField,
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
    Color gridCenter
) {
    public static Theme current() {
        return DynamicPlanimetry.SETTINGS.getTheme();
    }
}