package net.thewinnt.planimetry.ui;

import java.util.Collection;
import java.util.function.Function;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.util.FontProvider;

public class ComponentSelectBox<T> extends SelectBox<T> {
    private Collection<T> items;
    private Function<T, Component> textGetter;
    private float prefWidth;
    private float prefHeight;

    @SuppressWarnings("unchecked")
    public ComponentSelectBox(SelectBoxStyle style, Collection<T> items, Function<T, Component> textGetter) {
        super(style);
        this.setItems((T[])(items.toArray()));
        this.items = items;
        ((ComponentList)this.getList()).elements = items;
        ((ComponentList)this.getList()).components = textGetter;
        this.textGetter = textGetter;
    }

    @Override
    protected SelectBoxScrollPane<T> newScrollPane() {
        return new ComponentSelectBoxScrollPane(this);
    }

    @Override
    public void layout() {
        if (getSelectedPrefWidth()) {
            Vec2 size = textGetter.apply(getSelected()).getSize(DynamicPlanimetry.getInstance()::getBoldFont, Size.MEDIUM);
            prefWidth = (float)size.x + 4;
            prefHeight = (float)size.y + 4;
        } else {
            prefWidth = 0;
            prefHeight = 0;
            for (T i : items) {
                Vec2 result = textGetter.apply(i).getSize(DynamicPlanimetry.getInstance()::getBoldFont, Size.MEDIUM);
                prefHeight = Math.max(prefHeight, (float)result.y + 4);
                prefWidth = Math.max(prefWidth, (float)result.x + 4);
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        validate();
        if (getBackgroundDrawable() != null) {
            getBackgroundDrawable().draw(batch, getX(), getY(), getWidth(), getHeight());
        }
        textGetter.apply(getSelected()).draw(batch, DynamicPlanimetry.getInstance()::getBoldFont, Size.MEDIUM, Theme.current().textButton(), getX() + 2, getY() + getHeight() - 5);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setItems(T... newItems) {
        super.setItems(newItems);
        this.items = java.util.List.of(newItems);
    }

    @Override
    public float getPrefWidth() {
        return prefWidth;
    }

    @Override
    public float getPrefHeight() {
        return prefHeight;
    }

    @Override protected void onShow(Actor scrollPane, boolean below) {}

    @Override protected void onHide(Actor scrollPane) {
        scrollPane.addAction(Actions.removeActor());
    }

    public class ComponentSelectBoxScrollPane extends SelectBoxScrollPane<T> {
        public ComponentSelectBoxScrollPane(SelectBox<T> selectBox) {
            super(selectBox);
        }

        @Override
        protected List<T> newList() {
            return new ComponentList(ComponentSelectBox.this.getStyle().listStyle, items, textGetter, DynamicPlanimetry.getInstance()::getBoldFont, Size.MEDIUM);
        }
    }

    public class ComponentList extends List<T> {
        private Collection<T> elements;
        private Function<T, Component> components;
        private final FontProvider font;
        private final Size size;
        private float prefWidth;
        private float prefHeight;

        public ComponentList(ListStyle style, Collection<T> elements, Function<T, Component> components, FontProvider font, Size size) {
            super(style);
            this.elements = elements;
            this.components = components;
            this.font = font;
            this.size = size;
        }

        @Override
        public void layout() {
            prefWidth = 0;
            prefHeight = 0;
            for (T i : elements) {
                Vec2 result = components.apply(i).getSize(font, size);
                prefWidth += (float)result.x + 4;
                prefHeight = Math.max(prefHeight, (float)result.y + 4);
            }
        }

        @Override
        public float getPrefHeight() {
            return prefHeight;
        }

        @Override
        public float getPrefWidth() {
            return prefWidth;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            validate();
            drawBackground(batch, parentAlpha);
            float x = getX();
            float y = getY() + getHeight();
            float mx = Gdx.input.getX();
            float my = Gdx.graphics.getHeight() - Gdx.input.getY();
            for (T i : getItems()) {
                Drawable entryBackground = null;
                Component text = components.apply(i);
                ListStyle style = getStyle();
                Vec2 size = text.getSize(font, this.size);
                Color color = style.fontColorUnselected;
                if (mx > x && mx < x + getWidth() && my > y - size.y && my < y) {
                    entryBackground = Gdx.input.isButtonPressed(0) ? style.down : style.over;
                } else if (i == getSelected()) {
                    entryBackground = style.selection;
                    color = style.fontColorSelected;
                }
                if (entryBackground != null) {
                    entryBackground.draw(batch, x, y - (float)size.y, getWidth(), (float)size.y);
                }
                text.draw(batch, font, this.size, color, x, y);
            }
        }
    }
}
