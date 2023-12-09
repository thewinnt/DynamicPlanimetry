package net.thewinnt.planimetry;

import net.thewinnt.planimetry.ui.Theme;

public class Settings {
    private Theme theme = DynamicPlanimetry.THEME_LIGHT;
    private byte displayPresicion = 6;
    private byte mathPrecision = -23;

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public byte getDisplayPresicion() {
        return displayPresicion;
    }

    public void setDisplayPresicion(byte displayPresicion) {
        if (displayPresicion > 0) {
            this.displayPresicion = displayPresicion;
        }
    }

    public byte getMathPrecision() {
        return mathPrecision;
    }

    public void setMathPrecision(byte mathPrecision) {
        if (mathPrecision < 0) {
            this.mathPrecision = mathPrecision;
        }
    }
}
