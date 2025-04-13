package net.thewinnt.planimetry.shapes.point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Color;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.definition.point.PointPlacement;
import net.thewinnt.planimetry.definition.point.PointPlacementType;
import net.thewinnt.planimetry.definition.point.placement.MousePlacement;
import net.thewinnt.planimetry.definition.point.placement.StaticPlacement;
import net.thewinnt.planimetry.math.AABB;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.SegmentLike;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.ui.properties.PropertyHelper;
import net.thewinnt.planimetry.ui.properties.types.BooleanProperty;
import net.thewinnt.planimetry.ui.Theme;
import net.thewinnt.planimetry.ui.properties.types.NameComponentProperty;
import net.thewinnt.planimetry.ui.properties.types.RegistryElementProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.NameComponent;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class PointProvider extends Shape {
    public static final double ANGLE_STEP = 0.1;
    protected final List<Consumer<Vec2>> movementListeners = new ArrayList<>();
    private PointPlacement placement;
    private Vec2 fallback;
    protected NameComponent name;
    protected final BooleanProperty shouldRender = new BooleanProperty(Component.translatable(getPropertyName("should_render")), true);
    private final NameComponentProperty nameProperty;
    private Vec2 lastBestPlace;
    private Vec2 lastPos;
    private double lastSpace;

    public PointProvider(Drawing drawing, PointPlacement placement) {
        super(drawing);
        this.placement = placement;
        this.placement.setSource(this);
        this.name = drawing.generateName(drawing.shouldUseDashesForNaming());
        this.nameProperty = PropertyHelper.setter(new NameComponentProperty(Component.translatable(getPropertyName("name")), name), this::setName);
        this.dependencies.clear();
        this.dependencies.addAll(this.placement.dependencies());
        this.dependencies.forEach(t -> t.addDepending(this));
    }

    public PointProvider(Drawing drawing, PointPlacement placement, NameComponent name) {
        super(drawing);
        this.placement = placement;
        this.placement.setSource(this);
        this.name = name;
        this.nameProperty = PropertyHelper.setter(new NameComponentProperty(Component.translatable(getPropertyName("name")), name), this::setName);
        this.dependencies.clear();
        this.dependencies.addAll(this.placement.dependencies());
        this.dependencies.forEach(t -> t.addDepending(this));
    }

    @Override
    public void rebuildProperties() {
        properties.clear();
        RegistryElementProperty<PointPlacementType<?>> type = new RegistryElementProperty<>(placement.type(), Component.translatable(getPropertyName("placement")), Registries.POINT_PLACEMENT_TYPE, PointPlacementType.SELECTABLE);
        properties.add(type);
        properties.addAll(placement.properties());
        properties.add(shouldRender);
        properties.add(nameProperty);
        type.addValueChangeListener(t -> {
            try {
                this.placement = t.convert(this.placement, drawing);
                this.placement.setSource(this);
                this.dependencies.clear();
                this.dependencies.addAll(this.placement.dependencies());
                this.dependencies.forEach(shape -> shape.addDepending(this));
                rebuildProperties();
                DynamicPlanimetry.getInstance().editorScreen.show();
            } catch (RuntimeException e) {
                type.setValueSilent(t);
            }
        });
    }

    public Vec2 getPosition() {
        return Objects.requireNonNullElse(placement.get(), fallback);
    }

    public double getX() {
        return getPosition().x;
    }

    public double getY() {
        return getPosition().y;
    }

    @Override
    public boolean contains(Vec2 point) {
        return MathHelper.roughlyEquals(point, getPosition());
    }

    @Override
    public boolean contains(double x, double y) {
        Vec2 pos = getPosition();
        return MathHelper.roughlyEquals(pos.x, x) && MathHelper.roughlyEquals(pos.y, y);
    }

    @Override
    public double distanceToMouse(Vec2 point, DrawingBoard board) {
        return getPosition().distanceTo(point);
    }

    @Override
    public double distanceToMouse(double x, double y, DrawingBoard board) {
        return getPosition().distanceTo(x, y);
    }

    @Override
    public boolean intersects(AABB aabb) {
        return aabb.contains(getPosition());
    }

    @Override
    public Collection<Vec2> intersections(Shape other) {
        Vec2 pos = this.getPosition();
        if (other.contains(pos)) {
            return List.of(pos);
        }
        return List.of();
    }

    @Override
    public Collection<Vec2> intersections(SegmentLike other) {
        Vec2 pos = this.getPosition();
        if (other.contains(pos)) {
            return List.of(pos);
        }
        return List.of();
    }

    @Override
    public Collection<SegmentLike> asSegments() {
        return List.of();
    }

    @Override
    public void move(double dx, double dy) {
        this.placement.move(dx, dy);
    }

    @Override
    public void move(Vec2 delta) {
        this.placement.move(delta);
    }

    @Override
    public boolean canMove() {
        return placement.canMove();
    }

    public PointPlacement getPlacement() {
        return placement;
    }

    public void setPlacement(PointPlacement placement) {
        Vec2 fallback = this.placement.get();
        if (fallback != null) {
            this.fallback = fallback;
        }
        this.placement = placement;
    }

    public void addMovementListener(Consumer<Vec2> listener) {
        this.movementListeners.add(listener);
    }

    public boolean removeMovementListener(Consumer<Vec2> listener) {
        return this.movementListeners.remove(listener);
    }

    @Override
    public boolean shouldRender() {
        return shouldRender.getValue();
    }

    public void setShouldRender(boolean shouldRender) {
        this.shouldRender.setValue(shouldRender);
    }

    @Override
    public Component getName() {
        if (nameOverride != null) return nameOverride;
        return Component.translatable(getTypeName(), name);
    }

    public Component getNameComponent() {
        if (nameOverride != null) return Component.empty();
        return name;
    }

    public NameComponent getNameComponentNullable() {
        return name;
    }

    public void setName(NameComponent name) {
        this.name = name;
    }

    @Override
    public void replaceShape(Shape old, Shape neo) {
        // TODO PointPlacement#replaceShape
    } // most points don't have dependencies

    /** Makes a point static */
    public void freeze() {
        this.placement = new StaticPlacement(placement.get());
    }

    @Override
    protected CompoundTag writeNbt(SavingContext context) {
        CompoundTag nbt = new CompoundTag();
        nbt.put("placement", placement.toNbt(context));
        nbt.put("name", name.toNbt());
        return nbt;
    }

    public static PointProvider readNbt(CompoundTag nbt, LoadingContext context) {
        return new PointProvider(
            context.getDrawing(),
            PointPlacement.fromNbt(nbt.getCompoundTag("placement"), context),
            NameComponent.readNbt(nbt.getCompoundTag("name"))
        );
    }

    @Override
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        Vec2 position = placement.get();
        boolean isFallback = position == null;
        if (position == null) {
            position = Objects.requireNonNullElse(fallback, Vec2.ZERO);
        }
        if (!board.hasShape(this)) {
            drawer.setColor(switch (selection) {
                case HOVERED -> Theme.current().utilityPointHovered();
                case SELECTED -> Theme.current().utilityPointSelected();
                default -> Theme.current().utilityPoint();
            });
            drawer.circle(board.bx(position.x), board.by(position.y), this.getThickness(board.getScale()) * 2, 2);
        } else if (isFallback) {
            drawer.setColor(switch (selection) {
                case HOVERED -> Theme.current().undefinedPointHovered();
                case SELECTED -> Theme.current().undefinedPointSelected();
                default -> Theme.current().undefinedPoint();
            });
            drawer.circle(board.bx(position.x), board.by(position.y), this.getThickness(board.getScale()) * 2, 2);
        } else {
            Color color = switch (selection) {
                case HOVERED -> Theme.current().pointHovered();
                case SELECTED -> Theme.current().pointSelected();
                default -> Theme.current().point();
            };
            drawer.filledCircle(board.boardToGlobal(position).toVector2f(), this.getThickness(board.getScale()) * 2, color);
        }
        if (this.name != null) {
            Vec2 neededSpace = this.name.getSize(font, Size.MEDIUM).mul(0.5);
            Vec2 boardNeeded = neededSpace.mul(1 / board.getScale());
            if (position.x + boardNeeded.x < board.minX() ||
                position.x > board.maxX() ||
                position.y + boardNeeded.y < board.minY() ||
                position.y > board.maxY()) {
                lastPos = position;
                return;
            }
            if (position.equals(lastPos) && lastBestPlace != null && board.getFreeSpace(lastBestPlace.x, lastBestPlace.y) >= lastSpace - 1) {
                name.draw(drawer.getBatch(), font, Size.MEDIUM, Theme.current().textUI(), board.bx(lastBestPlace.x), (float)(board.by(lastBestPlace.y) + neededSpace.y / 2));
                lastPos = position;
                return;
            }
            double minRadius = (Math.max(neededSpace.x, neededSpace.y)) / board.getScale();
            double bestSpace = 0;
            // double _worstSpace = Double.MAX_VALUE;
            Vec2 bestPos = null;
            for (double angle = 0; angle < Math.PI; angle += ANGLE_STEP) {
                Vec2 test = MathHelper.continueFromAngle(position, angle, minRadius);
                double space = board.getFreeSpace(test.x, test.y);
                if (space >= bestSpace) {
                    bestSpace = space;
                    bestPos = test;
                }
                // if (space <= _worstSpace) {
                //     _worstSpace = space;
                // }
            }
            for (double angle = 0; angle < Math.PI; angle += ANGLE_STEP) {
                Vec2 test = MathHelper.continueFromAngle(position, angle, -minRadius);
                double space = board.getFreeSpace(test.x, test.y);
                if (space >= bestSpace) {
                    bestSpace = space;
                    bestPos = test;
                }
                // if (space <= _worstSpace) {
                //     _worstSpace = space;
                // }
            }
            // for (double angle = 0; angle < Math.PI; angle += 0.4) {
            //     Vec2 test = MathHelper.continueFromAngle(start, angle, -minRadius);
            //     double space = board.getFreeSpace(test.x, test.y);
            //     drawer.filledCircle(board.boardToGlobal(test).toVector2f(), 2, Color.RED.cpy().lerp(Color.GREEN, (float)((space - _worstSpace) / (bestSpace - _worstSpace))));
            //     test = MathHelper.continueFromAngle(start, angle, minRadius);
            //     space = board.getFreeSpace(test.x, test.y);
            //     drawer.filledCircle(board.boardToGlobal(test).toVector2f(), 2, Color.RED.cpy().lerp(Color.GREEN, (float)((space - _worstSpace) / (bestSpace - _worstSpace))));
            // }
            if (bestPos == null)
                bestPos = MathHelper.continueFromAngle(position, 90, -minRadius);
            lastBestPlace = bestPos;
            lastSpace = bestSpace;
            name.draw(drawer.getBatch(), font, Size.MEDIUM, Theme.current().textUI(), board.bx(bestPos.x), (float) (board.by(bestPos.y) + neededSpace.y / 2));
        }
        lastPos = position;
    }

    @Override
    public ShapeDeserializer<?> type() {
        return ShapeData.POINT;
    }

    public static PointProvider simple(Drawing drawing, Vec2 pos) {
        return new PointProvider(drawing, new StaticPlacement(pos));
    }

    public static PointProvider mouse(Drawing drawing) {
        return new PointProvider(drawing, new MousePlacement());
    }
}
