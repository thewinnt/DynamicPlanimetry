package net.thewinnt.planimetry;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.DoubleFunction;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.data.NbtUtil;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.ui.Notifications;
import net.thewinnt.planimetry.ui.SaveEntry.SortingType;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.Theme;
import net.thewinnt.planimetry.ui.properties.PropertyEntry;
import net.thewinnt.planimetry.ui.properties.PropertyLayout;
import net.thewinnt.planimetry.ui.properties.layout.CustomLayout;
import net.thewinnt.planimetry.ui.properties.types.BooleanProperty;
import net.thewinnt.planimetry.ui.properties.types.NumberProperty;
import net.thewinnt.planimetry.ui.properties.types.SelectionProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.ComponentRepresentable;

public class Settings {
    public static final CustomLayout PROPERTY_LAYOUT = new CustomLayout() {
        @Override
        public void layout(Actor actor, PropertyEntry entry) {
            actor.setBounds((Gdx.graphics.getWidth() - 20) * 3 / 4, 2, (Gdx.graphics.getWidth() - 20) / 4 - 10, entry.getHeight() - 4);
        }
    };
    private SelectionProperty<Theme> theme = new SelectionProperty<>(DynamicPlanimetry.THEME_LIGHT, Component.literal("Тема"), DynamicPlanimetry.BUILT_IN_THEMES);
    private NumberProperty displayPresicion = new NumberProperty(Component.literal("Точность отображения чисел"), 3).withMin(OptionalDouble.of(1)).withMax(OptionalDouble.of(127)).requireWholeNumbers(true);
    private SelectionProperty<AngleType> angleUnits = new SelectionProperty<>(AngleType.DEGREES, Component.literal("Единица углов"), AngleType.values());
    private BooleanProperty showGrid = new BooleanProperty(Component.literal("Показывать сетку"), true);
    private BooleanProperty isDebug = new BooleanProperty(Component.literal("Режим отладки"), true);
    private byte mathPrecision = -23;
    private SortingType lastSortingType = SortingType.BY_EDITING_TIME;
    private boolean lastSortingOrder = true;

    public Settings() {
        theme.layoutOverride = PROPERTY_LAYOUT;
        displayPresicion.layoutOverride = PROPERTY_LAYOUT;
        angleUnits.layoutOverride = PROPERTY_LAYOUT;
        theme.addValueChangeListener(theme -> {
            if (Gdx.app != null) {
                DynamicPlanimetry app = DynamicPlanimetry.getInstance();
                app.setScreen(DynamicPlanimetry.MAIN_MENU);
                app.setScreen(DynamicPlanimetry.SETTINGS_SCREEN);
            }
        });
    }

    public Theme getTheme() {
        return theme.getValue();
    }

    public void setTheme(Theme theme) {
        this.theme.setValue(theme);
    }

    public byte getDisplayPresicion() {
        return (byte)(double)displayPresicion.getValue();
    }

    public double representAngle(double radians) {
        return angleUnits.getValue().apply(radians);
    }

    public AngleType getAngleUnit() {
        return angleUnits.getValue();
    }

    public SortingType getLastSortingType() {
        return lastSortingType;
    }

    public boolean getLastSortingOrder() {
        return lastSortingOrder;
    }   
    
    public void setLastSortingType(SortingType lastSortingType) {
        this.lastSortingType = lastSortingType;
    }

    public void setLastSortingOrder(boolean lastSortingOrder) {
        this.lastSortingOrder = lastSortingOrder;
    }

    public void setDisplayPresicion(byte displayPresicion) {
        this.displayPresicion.setValue((double)displayPresicion);
    }

    public byte getMathPrecision() {
        return mathPrecision;
    }

    public void setMathPrecision(byte mathPrecision) {
        if (mathPrecision < 0) {
            this.mathPrecision = mathPrecision;
        }
    }

    public boolean shouldShowGrid() {
        return showGrid.getValue();
    }

    public void setShowGrid(boolean value) {
        showGrid.setValue(value);
    }

    public PropertyLayout getLayout(StyleSet styles) {
        return new PropertyLayout(List.of(theme, displayPresicion, angleUnits, showGrid, isDebug), styles, null, true);
    }

    public boolean isDebug() {
        return isDebug.getValue();
    }

    public void fromNbt(CompoundTag nbt) {
        if (nbt == null) return;
        try {
            this.theme.setValue(NbtUtil.getOptionalInt(nbt, "theme", 0) == 1 ? DynamicPlanimetry.THEME_DARK : DynamicPlanimetry.THEME_LIGHT);
            this.displayPresicion.setValue((double)NbtUtil.getOptionalByte(nbt, "display_precision", (byte)3));
            this.angleUnits.setValue(AngleType.valueOf(NbtUtil.getOptionalString(nbt, "angle_units", "degrees").toUpperCase()));
            this.mathPrecision = NbtUtil.getOptionalByte(nbt, "math_precision", (byte)-23);
            this.showGrid.setValue(NbtUtil.getOptionalBoolean(nbt, "show_grid", true));
            this.lastSortingType = SortingType.valueOf(NbtUtil.getOptionalString(nbt, "last_sorting_type", "by_editing_time").toUpperCase());
            this.lastSortingOrder = NbtUtil.getOptionalBoolean(nbt, "is_reverse_sort", false);
            this.isDebug.setValue(NbtUtil.getOptionalBoolean(nbt, "debug_mode", false));
        } catch (Exception e) {
            Notifications.addNotification("Error when loading settings: " + e.getMessage(), 15000);
            e.printStackTrace();
            return;
        }
    }

    public void toNbt(File file) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("theme", this.theme.getValue() == DynamicPlanimetry.THEME_DARK ? 1 : 0);
        nbt.putByte("display_precision", displayPresicion.getValue().byteValue());
        nbt.putString("angle_units", angleUnits.getValue().name().toLowerCase());
        nbt.putByte("math_precision", mathPrecision);
        NbtUtil.writeBoolean(nbt, "show_grid", showGrid.getValue());
        nbt.putString("last_sorting_type", lastSortingType.name().toLowerCase());
        NbtUtil.writeBoolean(nbt, "is_reverse_sort", lastSortingOrder);
        NbtUtil.writeBoolean(nbt, "debug_mode", isDebug.getValue());
        try {
            DynamicPlanimetry.NBT.toFile(nbt, file);
        } catch (IOException e) {
            Notifications.addNotification("Error saving settings: " + e.getMessage(), 5000);
            e.printStackTrace();
        }
    }

    public static Settings get() {
        return DynamicPlanimetry.SETTINGS;
    }

    public static enum AngleType implements ComponentRepresentable {
        GRADIANS(Component.literal("Градианы"), t -> t * MathHelper.RADIANS_TO_GRADIANS, " град"),
        RADIANS(Component.literal("Радианы"), t -> t, " рад"),
        DEGREES(Component.literal("Градусы"), t -> Math.toDegrees(t), "°");

        private final Component name;
        private final DoubleFunction<Double> converter;
        private final String unit;
        
        private AngleType(Component name, DoubleFunction<Double> converter, String unit) {
            this.name = name;
            this.converter = converter;
            this.unit = unit;
        }

        @Override
        public Component toComponent() {
            return name;
        }

        public double apply(double radians) {
            return converter.apply(radians);
        }

        public String getUnit() {
            return unit;
        }
    }
}
