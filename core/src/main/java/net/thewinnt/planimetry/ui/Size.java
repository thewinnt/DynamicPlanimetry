package net.thewinnt.planimetry.ui;

import com.badlogic.gdx.Gdx;

import net.thewinnt.planimetry.DynamicPlanimetry;

public enum Size {
    EXTREMELY_LARGE(6),
    SUPER_LARGE(9),
    VERY_LARGE(12),
    LARGE(17),
    MEDIUM(22),
    SMALL(26),
    VERY_SMALL(36);

    private final int factor;

    private Size(int factor) {
        this.factor = factor;
    }

    public Size smaller() {
        return switch (this) {
            case SUPER_LARGE -> VERY_LARGE;
            case VERY_LARGE -> LARGE;
            case LARGE -> MEDIUM;
            case MEDIUM -> SMALL;
            default -> VERY_SMALL;
        };
    }

    public Size larger() {
        return switch (this) {
            case VERY_SMALL -> SMALL;
            case SMALL -> MEDIUM;
            case MEDIUM -> LARGE;
            case LARGE -> VERY_LARGE;
            default -> SUPER_LARGE;
        };
    }

    public float getFactor() {
        return factor / DynamicPlanimetry.getDisplayScaling();
    }

    public float lines(int lines) {
        return Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) * lines / getFactor();
    }
}
