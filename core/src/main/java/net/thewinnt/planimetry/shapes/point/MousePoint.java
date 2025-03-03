package net.thewinnt.planimetry.shapes.point;

import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.Gdx;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.Theme;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class MousePoint extends PointProvider {
    public MousePoint(Drawing drawing) {
        super(drawing);
    }

    @Override
    public Vec2 getPosition() {
        DrawingBoard board = DynamicPlanimetry.getInstance().editorScreen.getBoard();
        return positionFilter(new Vec2(board.xb(Gdx.input.getX()), board.yb(Gdx.input.getY())));
    }

    protected Vec2 positionFilter(Vec2 mouse) {
        return mouse;
    }

    @Override
    public boolean canMove() {
        return false;
    }

    @Override
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        drawer.filledCircle(board.boardToGlobal(getPosition()).toVector2f(), this.getThickness(board.getScale()) * 2, Theme.current().pointSelected());
    }

    @Override public void move(Vec2 delta) {}
    @Override public void move(double dx, double dy) {}

    @Override
    public Collection<Property<?>> moreProperties() {
        return List.of();
    }

    @Override
    protected boolean shouldAutoAssingnName() {
        return false;
    }

    @Override
    public ShapeDeserializer<MousePoint> type() {
        return ShapeData.MOUSE_POINT;
    }

    @Override
    public CompoundTag writeNbt(SavingContext context) {
        return new CompoundTag();
    }

    public static MousePoint readNbt(CompoundTag nbt, LoadingContext context) {
        return new MousePoint(context.getDrawing());
    }
}
