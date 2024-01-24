package net.thewinnt.planimetry;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.OptionalDouble;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.ui.Notifications;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.Theme;
import net.thewinnt.planimetry.ui.SaveEntry.SortingType;
import net.thewinnt.planimetry.ui.properties.PropertyEntry;
import net.thewinnt.planimetry.ui.properties.PropertyLayout;
import net.thewinnt.planimetry.ui.properties.layout.CustomLayout;
import net.thewinnt.planimetry.ui.properties.types.BooleanProperty;
import net.thewinnt.planimetry.ui.properties.types.NumberProperty;
import net.thewinnt.planimetry.ui.properties.types.SelectionProperty;
import net.thewinnt.planimetry.ui.text.Component;

public class Settings {
    public static final CustomLayout PROPERTY_LAYOUT = new CustomLayout() {
        @Override
        public void layout(Actor actor, PropertyEntry entry) {
            actor.setBounds((Gdx.graphics.getWidth() - 20) * 3 / 4, 2, (Gdx.graphics.getWidth() - 20) / 4 - 10, entry.getHeight() - 4);
        }
    };
    private SelectionProperty<Theme> theme = new SelectionProperty<>(DynamicPlanimetry.THEME_LIGHT, Component.literal("Тема"), DynamicPlanimetry.BUILT_IN_THEMES);
    private NumberProperty displayPresicion = new NumberProperty(Component.literal("Точность отображения чисел"), 3).withMin(OptionalDouble.of(1)).withMax(OptionalDouble.of(127)).requireWholeNumbers(true);
    private BooleanProperty showGrid = new BooleanProperty(Component.literal("Показывать сетку"), true);
    private BooleanProperty isDebug = new BooleanProperty(Component.literal("Режим отладки"), true);
    private byte mathPrecision = -23;
    private SortingType lastSortingType = SortingType.BY_EDITING_TIME;
    private boolean lastSortingOrder = true;

    public Settings() {
        theme.layoutOverride = PROPERTY_LAYOUT;
        displayPresicion.layoutOverride = PROPERTY_LAYOUT;
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
        return new PropertyLayout(List.of(theme, displayPresicion, showGrid, isDebug), styles, null, true);
    }

    public boolean isDebug() {
        return isDebug.getValue();
    }

    public void fromNbt(CompoundTag nbt) {
        if (nbt == null) return;
        try {
            this.theme.setValue(nbt.getInt("theme").intValue() == 1 ? DynamicPlanimetry.THEME_DARK : DynamicPlanimetry.THEME_LIGHT);
            this.displayPresicion.setValue(nbt.getByte("display_precision").doubleValue());
            this.mathPrecision = nbt.getByte("math_precision").byteValue();
            this.showGrid.setValue(nbt.getByte("show_grid").byteValue() != 0);
            this.lastSortingType = SortingType.valueOf(nbt.getString("last_sorting_type").getValue().toUpperCase());
            this.lastSortingOrder = nbt.getByte("is_reverse_sort").byteValue() != 0;
            this.isDebug.setValue(nbt.getByte("debug_mode").byteValue() != 0);
        } catch (Exception e) {
            Notifications.addNotification("Error when loading settings: " + e.getMessage(), 5000);
            e.printStackTrace();
            return;
        }
    }

    public void toNbt(File file) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("theme", this.theme.getValue() == DynamicPlanimetry.THEME_DARK ? 1 : 0);
        nbt.putByte("display_precision", displayPresicion.getValue().byteValue());
        nbt.putByte("math_precision", mathPrecision);
        nbt.putByte("show_grid", showGrid.getValue() ? (byte)1 : (byte)0);
        nbt.putString("last_sorting_type", lastSortingType.name().toLowerCase());
        nbt.putByte("is_reverse_sort", lastSortingOrder ? (byte)1 : (byte)0);
        nbt.putByte("debug_mode", isDebug.getValue() ? (byte)1 : (byte)0);
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
}
