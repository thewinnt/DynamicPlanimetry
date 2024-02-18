package net.thewinnt.planimetry;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.OptionalDouble;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.data.NbtUtil;
import net.thewinnt.planimetry.settings.ShapeMovementPredicate;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.ui.Notifications;
import net.thewinnt.planimetry.ui.SaveEntry.SortingType;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.ui.Theme;
import net.thewinnt.planimetry.ui.properties.PropertyEntry;
import net.thewinnt.planimetry.ui.properties.PropertyLayout;
import net.thewinnt.planimetry.ui.properties.layout.CustomLayout;
import net.thewinnt.planimetry.ui.properties.types.BooleanProperty;
import net.thewinnt.planimetry.ui.properties.types.NumberProperty;
import net.thewinnt.planimetry.ui.properties.types.SelectionProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.settings.AngleType;

public class Settings {
    public static final CustomLayout PROPERTY_LAYOUT = new CustomLayout() {
        @Override
        public void layout(Actor actor, PropertyEntry entry) {
            actor.setBounds((Gdx.graphics.getWidth() - 20) * 3 / 4, 2, (Gdx.graphics.getWidth() - 20) / 4 - 10, entry.getHeight() - 4);
        }
    };
    private final SelectionProperty<Theme> theme = new SelectionProperty<>(DynamicPlanimetry.THEME_LIGHT, Component.literal("Тема"), DynamicPlanimetry.BUILT_IN_THEMES);
    private final NumberProperty displayPresicion = new NumberProperty(Component.literal("Точность отображения чисел (знаки после запятой)"), 3).withMin(OptionalDouble.of(1)).withMax(OptionalDouble.of(127)).requireWholeNumbers(true);
    private final SelectionProperty<AngleType> angleUnits = new SelectionProperty<>(AngleType.DEGREES, Component.literal("Единица измерения углов"), AngleType.values());
    private final SelectionProperty<ShapeMovementPredicate> moveShapes = new SelectionProperty<>(ShapeMovementPredicate.ONLY_POINTS, Component.literal("Перемещать без выделения"), ShapeMovementPredicate.values());
    private final BooleanProperty showGrid = new BooleanProperty(Component.literal("Показывать сетку"), true);
    private final BooleanProperty isDebug = new BooleanProperty(Component.literal("Режим отладки"), false);
    private final BooleanProperty fullscreen = new BooleanProperty(Component.literal("Полный экран"), false);
    private byte mathPrecision = -23;
    private SortingType lastSortingType = SortingType.BY_EDITING_TIME;
    private boolean lastSortingOrder = true;

    public Settings() {
        theme.layoutOverride = PROPERTY_LAYOUT;
        displayPresicion.layoutOverride = PROPERTY_LAYOUT;
        angleUnits.layoutOverride = PROPERTY_LAYOUT;
        moveShapes.layoutOverride = PROPERTY_LAYOUT;
        theme.addValueChangeListener(theme -> {
            if (Gdx.app != null) {
                DynamicPlanimetry app = DynamicPlanimetry.getInstance();
                app.setScreen(DynamicPlanimetry.MAIN_MENU);
                app.setScreen(DynamicPlanimetry.SETTINGS_SCREEN);
            }
        });
        fullscreen.addValueChangeListener(isFullscreen -> {
            if (isFullscreen) {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            } else {
                Gdx.graphics.setWindowedMode(1280, 720);
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

    public double toUnit(double radians) {
        return angleUnits.getValue().toUnit(radians);
    }

    public double toRadians(double unitval) {
        return angleUnits.getValue().toRadians(unitval);
    }

    public AngleType getAngleUnit() {
        return angleUnits.getValue();
    }

    public boolean canMove(Shape shape) {
        return moveShapes.getValue().test(shape);
    }

    public ShapeMovementPredicate getShapeMovementPredicate() {
        return moveShapes.getValue();
    }

    public void setMoveShapes(ShapeMovementPredicate predicate) {
        this.moveShapes.setValue(predicate);
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
        return new PropertyLayout(List.of(theme, displayPresicion, angleUnits, moveShapes, showGrid, fullscreen), styles, null, Size.MEDIUM, true);
    }

    public boolean isDebug() {
        return isDebug.getValue();
    }

    public void toggleFullscreen() {
        fullscreen.setValue(!fullscreen.getValue());
    }

    public void fromNbt(CompoundTag nbt) {
        if (nbt == null) return;
        try {
            this.theme.setValue(NbtUtil.getOptionalInt(nbt, "theme", 0) == 1 ? DynamicPlanimetry.THEME_DARK : DynamicPlanimetry.THEME_LIGHT);
            this.displayPresicion.setValue((double)NbtUtil.getOptionalByte(nbt, "display_precision", (byte)3));
            this.angleUnits.setValue(AngleType.valueOf(NbtUtil.getOptionalString(nbt, "angle_units", "degrees").toUpperCase()));
            this.moveShapes.setValue(ShapeMovementPredicate.valueOf(NbtUtil.getOptionalString(nbt, "shape_movement_predicate", "only_points").toUpperCase()));
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
        nbt.putString("shape_movement_predicate", moveShapes.getValue().name().toLowerCase());
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
}
