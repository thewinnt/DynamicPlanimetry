package net.thewinnt.planimetry;

import com.badlogic.gdx.Gdx;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.Language;
import net.thewinnt.planimetry.data.NbtUtil;
import net.thewinnt.planimetry.settings.AngleType;
import net.thewinnt.planimetry.settings.AntialiasingType;
import net.thewinnt.planimetry.settings.ShapeMovementPredicate;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.ui.*;
import net.thewinnt.planimetry.ui.SaveEntry.SortingType;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.PropertyHelper;
import net.thewinnt.planimetry.ui.properties.PropertyLayout;
import net.thewinnt.planimetry.ui.properties.layout.CustomLayout;
import net.thewinnt.planimetry.ui.properties.types.ActionProperty;
import net.thewinnt.planimetry.ui.properties.types.BooleanProperty;
import net.thewinnt.planimetry.ui.properties.types.NumberProperty;
import net.thewinnt.planimetry.ui.properties.types.OptionProperty;
import net.thewinnt.planimetry.ui.properties.types.SelectionProperty;
import net.thewinnt.planimetry.ui.text.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Settings {
    public static final CustomLayout PROPERTY_LAYOUT = (actor, entry) -> actor.setBounds((Gdx.graphics.getWidth() - 20) * 3f / 4, 2, (Gdx.graphics.getWidth() - 20) / 4f - 10, entry.getHeight() - 4);
    private final SelectionProperty<GuiTheme> guiTheme = new SelectionProperty<>(GuiTheme.THEME_GUI_LIGHT, Component.translatable("settings.theme.gui"), GuiTheme.ORDERED_LIST);
    private final SelectionProperty<BoardTheme> boardTheme = new SelectionProperty<>(BoardTheme.THEME_BOARD_WHITE, Component.translatable("settings.theme.board"), BoardTheme.ORDERED_LIST);
    private final NumberProperty displayPresicion = new NumberProperty(Component.translatable("settings.display_precision"), 3).withMin(1).withMax(127).requireWholeNumbers(true);
    private final SelectionProperty<AngleType> angleUnits = new SelectionProperty<>(AngleType.DEGREES, Component.translatable("settings.angle_unit"), AngleType.values());
    private final SelectionProperty<ShapeMovementPredicate> moveShapes = new SelectionProperty<>(ShapeMovementPredicate.ONLY_POINTS, Component.translatable("settings.move_without_selection"), ShapeMovementPredicate.values());
    private final BooleanProperty showGrid = new BooleanProperty(Component.translatable("settings.show_grid"), true);
    private final BooleanProperty isDebug = new BooleanProperty(Component.translatable("settings.debug_mode"), false);
    private final BooleanProperty fullscreen = new BooleanProperty(Component.translatable("settings.fullscreen"), false);
    private final NumberProperty displayScaling = new NumberProperty(Component.translatable("settings.scaling"), 1).withMin(0.125).withMax(8).noLiveUpdates();
    private final NumberProperty editPanelScale = new NumberProperty(Component.translatable("settings.edit_pane_scale"), 1).withMin(0.1);
    private final SelectionProperty<AntialiasingType> antialiasing = new SelectionProperty<>(Component.translatable("settings.antialiasing"), AntialiasingType.values());
    private SelectionProperty<Language> language;
    private final ActionProperty reloadLanguages = new ActionProperty(Component.translatable("settings.reload_languages"), Component.translatable("settings.reload_languages.action"), () -> {
        if (Gdx.app != null) {
            DynamicPlanimetry app = DynamicPlanimetry.getInstance();
            app.reloadLanguages();
            app.setScreen(DynamicPlanimetry.MAIN_MENU);
            app.setScreen(DynamicPlanimetry.SETTINGS_SCREEN);
        }
    });
    public final OptionProperty ctrlSelection = new OptionProperty(Component.translatable("settings.use_ctrl_for_selection"), true).setOnTrue(Component.translatable("settings.use_ctrl_for_selection.true")).setOnFalse(Component.translatable("settings.use_ctrl_for_selection.false"));
    private String currentLanguage;
    private double mathPrecision = 12;
    private double lowerBound = 1 - Math.pow(10, -mathPrecision);
    private double upperBound = 1 + Math.pow(10, -mathPrecision);
    private SortingType lastSortingType = SortingType.BY_EDITING_TIME;
    private boolean lastSortingOrder = true;
    private boolean showFilenames = true;

    public Settings() {
        guiTheme.layoutOverride = PROPERTY_LAYOUT;
        boardTheme.layoutOverride = PROPERTY_LAYOUT;
        displayPresicion.layoutOverride = PROPERTY_LAYOUT;
        angleUnits.layoutOverride = PROPERTY_LAYOUT;
        moveShapes.layoutOverride = PROPERTY_LAYOUT;
        reloadLanguages.layoutOverride = PROPERTY_LAYOUT;
        displayScaling.layoutOverride = PROPERTY_LAYOUT;
        editPanelScale.layoutOverride = PROPERTY_LAYOUT;
        antialiasing.layoutOverride = PROPERTY_LAYOUT;
        guiTheme.addValueChangeListener(theme -> {
            if (Gdx.app != null) {
                DynamicPlanimetry app = DynamicPlanimetry.getInstance();
                if (!app.screenByIds.isEmpty()) {
                    app.setScreen(DynamicPlanimetry.MAIN_MENU);
                    app.setScreen(DynamicPlanimetry.SETTINGS_SCREEN);
                }
            }
        });
        fullscreen.addValueChangeListener(isFullscreen -> {
            if (isFullscreen) {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            } else {
                Gdx.graphics.setWindowedMode(1280, 720);
            }
        });
        displayScaling.addValueChangeListener(value -> {
            DynamicPlanimetry.setDisplayScaling(value.floatValue());
            if (Gdx.app != null) {
                DynamicPlanimetry.getInstance().settingsScreen.show();
            }
        });
        antialiasing.addValueChangeListener(t -> Notifications.addNotification(DynamicPlanimetry.translate("settings.restart_needed"), 2000));
    }

    public GuiTheme getGuiTheme() {
        return guiTheme.getValue();
    }

    public void setGuiTheme(GuiTheme guiTheme) {
        this.guiTheme.setValue(guiTheme);
    }

    public BoardTheme getBoardTheme() {
        return boardTheme.getValue();
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

    public double getMathPrecision() {
        return mathPrecision;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public void setMathPrecision(double mathPrecision) {
        if (mathPrecision > 0) {
            this.mathPrecision = mathPrecision;
            this.upperBound = 1 + Math.pow(10, -mathPrecision);
            this.lowerBound = 1 - Math.pow(10, -mathPrecision);
        }
    }

    public boolean shouldShowGrid() {
        return showGrid.getValue();
    }

    public void setShowGrid(boolean value) {
        showGrid.setValue(value);
    }

    public float getEditPaneScale() {
        return editPanelScale.getValue().floatValue();
    }

    public void setEditPanelScale(double value) {
        editPanelScale.setValue(value);
    }

    public PropertyLayout getLayout(StyleSet styles) {
        ArrayList<Property<?>> properties = new ArrayList<>(List.of(guiTheme, boardTheme, displayPresicion, angleUnits, moveShapes, showGrid, fullscreen, language, displayScaling, editPanelScale, antialiasing));
        if (isDebug() || DynamicPlanimetry.platform().forceShowDebug()) {
            properties.add(isDebug);
            if (isDebug()) {
                properties.add(reloadLanguages);
                NumberProperty mathPrecision = PropertyHelper.setter(new NumberProperty(Component.translatable("settings.math_precision"), this.mathPrecision), this::setMathPrecision);
                mathPrecision.layoutOverride = PROPERTY_LAYOUT;
                properties.add(mathPrecision);
            }
        }
        return new PropertyLayout(properties, styles, null, Size.MEDIUM, true);
    }

    public boolean isDebug() {
        return isDebug.getValue();
    }

    void setDebug() {
        isDebug.setValue(true);
    }

    public void toggleFullscreen() {
        fullscreen.setValue(!fullscreen.getValue());
    }

    public boolean showFilenames() {
        return showFilenames;
    }

    public void setShowFilenames(boolean showFilenames) {
        this.showFilenames = showFilenames;
    }

    void initLanguages(Map<String, Language> languages) {
        Language.setFallbackLanguage(languages.get("en_us"));
        this.language = new SelectionProperty<>(Component.translatable("settings.language"), languages.values().toArray(new Language[0]));

        DynamicPlanimetry.getInstance().setLanguage(languages.get(this.currentLanguage));
        this.language.setValue(DynamicPlanimetry.getInstance().getCurrentLanguage());
        this.language.addValueChangeListener(lang -> {
            if (Gdx.app != null) {
                DynamicPlanimetry app = DynamicPlanimetry.getInstance();
                app.setLanguage(lang);
                app.setScreen(DynamicPlanimetry.MAIN_MENU);
                app.setScreen(DynamicPlanimetry.SETTINGS_SCREEN);
                currentLanguage = lang.getId();
            }
        });
        this.language.layoutOverride = PROPERTY_LAYOUT;
    }

    public void fromNbt(CompoundTag nbt) {
        if (nbt == null) nbt = new CompoundTag(); // needed to load defaults
        try {
            this.guiTheme.setValueSilent(DynamicPlanimetry.guiThemeFromNbt(nbt, "theme"));
            this.boardTheme.setValueSilent(DynamicPlanimetry.boardThemeFromNbt(nbt, "theme_board"));
            this.displayPresicion.setValueSilent((double)NbtUtil.getOptionalByte(nbt, "display_precision", (byte)3));
            this.angleUnits.setValueSilent(AngleType.valueOf(NbtUtil.getOptionalString(nbt, "angle_units", "degrees").toUpperCase()));
            this.moveShapes.setValueSilent(ShapeMovementPredicate.valueOf(NbtUtil.getOptionalString(nbt, "shape_movement_predicate", "only_points").toUpperCase()));
            this.setMathPrecision(NbtUtil.getOptionalDouble(nbt, "math_precision", 12));
            this.showGrid.setValueSilent(NbtUtil.getOptionalBoolean(nbt, "show_grid", true));
            this.lastSortingType = SortingType.valueOf(NbtUtil.getOptionalString(nbt, "last_sorting_type", "by_editing_time").toUpperCase());
            this.lastSortingOrder = NbtUtil.getOptionalBoolean(nbt, "is_reverse_sort", true);
            this.currentLanguage = NbtUtil.getOptionalString(nbt, "language", "ru_ru");
            this.isDebug.setValueSilent(NbtUtil.getOptionalBoolean(nbt, "debug_mode", false));
            this.displayScaling.setValueSilent(NbtUtil.getOptionalDouble(nbt, "display_scaling", 1));
            this.ctrlSelection.setValueSilent(NbtUtil.getOptionalBoolean(nbt, "ctrl_selection", true));
            this.editPanelScale.setValueSilent(NbtUtil.getOptionalDouble(nbt, "edit_pane_scale", 1));
            DynamicPlanimetry.setDisplayScaling(displayScaling.getValue().floatValue());
            this.antialiasing.setValueSilent(AntialiasingType.valueOf(NbtUtil.getOptionalByte(nbt, "antialiasing", (byte)4)));
            this.showFilenames = NbtUtil.getOptionalBoolean(nbt, "show_filenames", true);
        } catch (Exception e) {
            Notifications.addNotification(DynamicPlanimetry.translate("error.settings.load_failed", e.getMessage()), 15000);
            e.printStackTrace();
        }
    }

    public void toNbt(File file) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("theme", this.guiTheme.getValue().id().toString());
        nbt.putString("theme_board", this.boardTheme.getValue().id().toString());
        nbt.putByte("display_precision", displayPresicion.getValue().byteValue());
        nbt.putString("angle_units", angleUnits.getValue().name().toLowerCase());
        nbt.putString("shape_movement_predicate", moveShapes.getValue().name().toLowerCase());
        nbt.putDouble("math_precision", mathPrecision);
        NbtUtil.writeBoolean(nbt, "show_grid", showGrid.getValue());
        nbt.putString("last_sorting_type", lastSortingType.name().toLowerCase());
        NbtUtil.writeBoolean(nbt, "is_reverse_sort", lastSortingOrder);
        NbtUtil.writeBoolean(nbt, "debug_mode", isDebug.getValue());
        nbt.putString("language", currentLanguage);
        nbt.putDouble("display_scaling", displayScaling.getValue());
        NbtUtil.writeBoolean(nbt, "ctrl_selection", ctrlSelection.getValue());
        nbt.putDouble("edit_pane_scale", editPanelScale.getValue());
        nbt.putByte("antialiasing", antialiasing.getValue().samples);
        NbtUtil.writeBoolean(nbt, "show_filenames", showFilenames);
        try {
            NBTUtil.write(nbt, file);
        } catch (IOException e) {
            Notifications.addNotification(DynamicPlanimetry.translate("error.settings.save_failed", e.getMessage()), 5000);
            e.printStackTrace();
        }
    }

    public static Settings get() {
        return DynamicPlanimetry.SETTINGS;
    }
}
