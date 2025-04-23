package net.thewinnt.planimetry.ui.text;

import com.badlogic.gdx.graphics.Color;
import net.thewinnt.planimetry.ui.GuiTheme;
import org.jetbrains.annotations.Nullable;

public record Formatting(@Nullable Color color, boolean bold) {
    public static final Formatting EMPTY = new Formatting(null, true);

    public Color getColor() {
        if (color == null) {
            return GuiTheme.current().textUI();
        }
        return color;
    }
}
