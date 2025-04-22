package net.thewinnt.planimetry.ui;

import com.badlogic.gdx.graphics.Color;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.data.registry.Identifier;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.ComponentRepresentable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record BoardTheme(
    Component name,
    Identifier id,
    Color board,
    Color shape,
    Color shapeHovered,
    Color shapeHoveredParent,
    Color shapeSelected,
    Color point,
    Color pointHovered,
    Color pointSelected,
    Color utilityPoint,
    Color utilityPointHovered,
    Color utilityPointSelected,
    Color undefinedPoint,
    Color undefinedPointHovered,
    Color undefinedPointSelected,
    Color gridLine,
    Color gridHint,
    Color gridCenter,
    Color angleMarker,
    Color textAngleMarker
) implements ComponentRepresentable {
    public static final Map<Identifier, BoardTheme> REGISTRY = new HashMap<>();
    public static final List<BoardTheme> ORDERED_LIST = new ArrayList<>();

    public static final BoardTheme THEME_BOARD_WHITE = new BoardTheme(
        Component.translatable("theme.board.builtin.white"),
        new Identifier("white"),
        new Color(0xFFFFFFFF), // board
        new Color(0x000000FF), // shape
        new Color(0x00C0FFFF), // shape hovered
        new Color(0x00E0FFFF), // shape hovered (parent)
        new Color(0x0080FFFF), // shape selected
        new Color(0xFF8000FF), // point
        new Color(0xFFC84CFF), // point hovered
        new Color(0xCC5500FF), // point selected
        new Color(0x0080FFFF), // utility point
        new Color(0x00C4FFFF), // utility point hovered
        new Color(0x0055CCFF), // utility point selected
        new Color(0xA00000FF), // undefined point
        new Color(0xFF0000FF), // undefined point hovered
        new Color(0x800000FF), // undefined point selected
        new Color(0xDDDDDDFF), // grid line
        new Color(0xAAAAAAFF), // grid hint
        new Color(0x808080FF), // grid center
        new Color(0x202020FF), // angle marker
        new Color(0x202020FF)  // angle marker text
    );
    public static final BoardTheme THEME_BOARD_GREEN = new BoardTheme(
        Component.translatable("theme.board.builtin.green"),
        new Identifier("green"),
        new Color(0x0f352dFF), // board
        new Color(0xa4cdc2FF), // shape
        new Color(0x90fff2FF), // shape hovered
        new Color(0xa8eeddFF), // shape hovered (parent)
        new Color(0x579b8bFF), // shape selected
        new Color(0xbac9c5FF), // point
        new Color(0xd6e5e1FF), // point hovered
        new Color(0x9faea9FF), // point selected
        new Color(0xccc6a8FF), // utility point
        new Color(0xe8e2c3FF), // utility point hovered
        new Color(0xb0ab8eFF), // utility point selected
        new Color(0xf3b4c7FF), // undefined point
        new Color(0xe691acFF), // undefined point hovered
        new Color(0xc97691FF), // undefined point selected
        new Color(0x535f5bFF), // grid line
        new Color(0x6b7773FF), // grid hint
        new Color(0x919f9aFF), // grid center
        new Color(0xE0E0E0FF), // angle marker
        new Color(0xF0F0F0FF)  // angle marker text
    );
    public static final BoardTheme THEME_BOARD_BLACK = new BoardTheme(
        Component.translatable("theme.board.builtin.black"),
        new Identifier("black"),
        new Color(0x1F1F1FFF), // board
        new Color(0xEEEEEEFF), // shape
        new Color(0x00C0FFFF), // shape hovered
        new Color(0x00E0FFFF), // shape hovered (parent)
        new Color(0x0080FFFF), // shape selected
        new Color(0xFF8000FF), // point
        new Color(0xFFC84CFF), // point hovered
        new Color(0xCC5500FF), // point selected
        new Color(0x0080FFFF), // utility point
        new Color(0x00C4FFFF), // utility point hovered
        new Color(0x0055CCFF), // utility point selected
        new Color(0xA00000FF), // undefined point
        new Color(0xFF0000FF), // undefined point hovered
        new Color(0x800000FF), // undefined point selected
        new Color(0x404040FF), // grid line
        new Color(0x505050FF), // grid hint
        new Color(0x000000FF), // grid center
        new Color(0xE0E0E0FF), // angle marker
        new Color(0xF0F0F0FF)  // angle marker text
    );

    public static BoardTheme current() {
        return DynamicPlanimetry.SETTINGS.getBoardTheme();
    }

    public static BoardTheme byId(String id) {
        return REGISTRY.get(new Identifier(id));
    }

    public BoardTheme {
        REGISTRY.put(id, this);
        ORDERED_LIST.add(this);
    }

    @Override
    public Component toComponent() {
        return name;
    }
}
