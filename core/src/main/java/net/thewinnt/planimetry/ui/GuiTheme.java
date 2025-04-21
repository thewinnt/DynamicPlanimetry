package net.thewinnt.planimetry.ui;

import com.badlogic.gdx.graphics.Color;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.data.registry.Identifier;
import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.ComponentRepresentable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record GuiTheme(
    Component name,
    Identifier id,
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
    Color selectionOutline,
    Color selectionFill,
    Color closeButton,
    Color closeButtonHover,
    Color closeButtonPress
) implements ComponentRepresentable {
    public static final Map<Identifier, GuiTheme> REGISTRY = new HashMap<>();
    public static final List<GuiTheme> ORDERED_LIST = new ArrayList<>();

    public static final GuiTheme THEME_GUI_LIGHT = new GuiTheme(
        Component.translatable("theme.gui.builtin.light"),
        new Identifier("light"),
        new Color(0xFFFFFFFF), // main
        new Color(0xDCDCDCFF), // pressed
        new Color(0xF0F0F0FF), // button
        new Color(0x000000FF), // outlines
        new Color(0x000000FF), // text (button)
        new Color(0x000000FF), // text (ui)
        new Color(0x2D2D2DFF), // inactive text
        new Color(0xFFFFFFFF), // text field
        new Color(0x0060FFB0), // selection background
        new Color(0xC0C0C0FF), // delimiter
        new Color(0x2D2D2DFF), // inactive
        new Color(0x00C0FFFF), // selection box outline
        new Color(0x00C0FF40), // selection box fill
        new Color(0xD86C6CFF), // close button
        new Color(0xFF7F7FFF), // close button (hovered)
        new Color(0xB25959FF)  // close button (pressed)
    );
    public static final GuiTheme THEME_GUI_DARK = new GuiTheme(
        Component.translatable("theme.gui.builtin.dark"),
        new Identifier("dark"),
        new Color(0x1F1F1FFF), // main
        new Color(0x000000FF), // pressed
        new Color(0x0F0F0FFF), // button
        new Color(0xA0A0A0FF), // outlines
        new Color(0xD0D0D0FF), // text (button)
        new Color(0xEEEEEEFF), // text (ui)
        new Color(0x2D2D2DFF), // inactive text
        new Color(0x000000FF), // text field
        new Color(0x0060FFB0), // selection background
        new Color(0x303030FF), // delimiter
        new Color(0x2D2D2DFF), // inactive
        new Color(0x00C0FFFF), // selection box outline
        new Color(0x00C0FF40), // selection box fill
        new Color(0xB25959FF), // close button
        new Color(0xCC6666FF), // close button (hovered)
        new Color(0x994C4CFF)  // close button (pressed)
    );

    public static GuiTheme current() {
        return DynamicPlanimetry.SETTINGS.getGuiTheme();
    }

    public static GuiTheme byId(String id) {
        return REGISTRY.get(new Identifier(id));
    }

    public GuiTheme {
        REGISTRY.put(id, this);
        ORDERED_LIST.add(this);
    }

    @Override
    public Component toComponent() {
        return name;
    }
}
