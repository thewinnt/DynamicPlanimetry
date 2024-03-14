package net.thewinnt.planimetry.ui;

import java.util.Collection;
import java.util.function.Function;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.ui.drawable.DynamicIcon;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.util.FontProvider;

public class ComponentSelectBox<T> extends SelectBox<T> {
    private final Function<T, Component> textGetter;
    private final Size size;
    private Collection<T> items;
    private float prefWidth;
    private float prefHeight;
    private boolean open;
    public float iconSizeOverride = 0;

    @SuppressWarnings("unchecked")
    public ComponentSelectBox(SelectBoxStyle style, Collection<T> items, Function<T, Component> textGetter, Size size) {
        super(style);
        this.size = size;
        super.setItems((T[])(items.toArray()));
        this.items = items;
        ((ComponentList)this.getList()).finish(items, textGetter, size);
        this.textGetter = textGetter;
        this.layout();
    }

    @Override
    protected SelectBoxScrollPane<T> newScrollPane() {
        return new ComponentSelectBoxScrollPane(this);
    }

    @Override
    public void layout() {
        if (getSelectedPrefWidth()) {
            Vec2 size = textGetter.apply(getSelected()).getSize(DynamicPlanimetry.getInstance()::getBoldFont, this.size);
            prefWidth = (float)size.x + 4;
            prefHeight = (float)size.y;
        } else {
            prefWidth = 0;
            prefHeight = 0;
            for (T i : items) {
                Vec2 result = textGetter.apply(i).getSize(DynamicPlanimetry.getInstance()::getBoldFont, this.size);
                prefHeight = Math.max(prefHeight, (float)result.y);
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
        if (iconSizeOverride == 0) {
            if (this.open) {
                DynamicIcon.DOWN_TRIANGLE.draw(batch, getX(), getY() + 2, getHeight() - 4, getHeight() - 4);
            } else {
                DynamicIcon.RIGHT_TRIANGLE.draw(batch, getX() + 2, getY() + 2, getHeight() - 4, getHeight() - 4);
            }
            textGetter.apply(getSelected()).draw(batch, DynamicPlanimetry.getInstance()::getBoldFont, this.size, Theme.current().textButton(), getX() + getHeight(), getY() + getHeight() * 3 / 4);
        } else {
            if (this.open){
                DynamicIcon.DOWN_TRIANGLE.draw(batch, getX(), getY() + iconSizeOverride / 2, iconSizeOverride, iconSizeOverride);
            } else {
                DynamicIcon.RIGHT_TRIANGLE.draw(batch, getX(), getY() + iconSizeOverride / 2, iconSizeOverride, iconSizeOverride);
            }
            textGetter.apply(getSelected()).draw(batch, DynamicPlanimetry.getInstance()::getBoldFont, this.size, Theme.current().textButton(), getX() + iconSizeOverride, getY() + getHeight() * 3 / 4);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setItems(T... newItems) {
        super.setItems(newItems);
        this.items = java.util.List.of(newItems);
    }

    @Override
    public float getPrefWidth() {
        return prefWidth + prefHeight + 4;
    }

    @Override
    public float getPrefHeight() {
        return prefHeight;
    }

    @Override protected void onShow(Actor scrollPane, boolean below) {
        this.open = true;
    }

    @Override protected void onHide(Actor scrollPane) {
        scrollPane.addAction(Actions.removeActor());
        this.open = false;
    }

    public boolean isOpen() {
        return open;
    }

    public class ComponentSelectBoxScrollPane extends SelectBoxScrollPane<T> {
        public ComponentSelectBoxScrollPane(SelectBox<T> selectBox) {
            super(selectBox);
        }

        @Override
        protected List<T> newList() {
            return new ComponentList(ComponentSelectBox.this.getStyle().listStyle, DynamicPlanimetry.getInstance()::getBoldFont, ComponentSelectBox.this.size);
        }
    }

    public class ComponentList extends List<T> {
        private Collection<T> elements;
        private Function<T, Component> components;
        private final FontProvider font;
        private Size size;
        private float pw = 200;
        private float pht = 400;

        public ComponentList(ListStyle style, FontProvider font, Size size) {
            super(style);
            this.font = font;
            this.size = size;
        }

        @SuppressWarnings("unchecked")
        public void finish(Collection<T> elements, Function<T, Component> components, Size size) {
            this.elements = elements;
            this.components = components;
            this.size = size;
            this.setItems((T[])this.elements.toArray());
            layout();
            invalidateHierarchy();
        }

        @Override
        public void layout() {
            this.pw = 0;
            this.pht = 0;
            if (elements == null) return;
            for (T i : elements) {
                Vec2 result = components.apply(i).getSize(font, size);
                this.pw = Math.max(pw, (float)result.x + 4);
                this.pht += (float)result.y + 4;
            }
        }

        @Override
        public float getPrefHeight() {
            return this.pht;
        }

        @Override
        public float getPrefWidth() {
            return this.pw;
        }

        @Override
        public float getItemHeight() {
            return this.pht / elements.size();
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            validate();
            drawBackground(batch, parentAlpha);
            float x = getX();
            float y = getY();
            float mx = Gdx.input.getX();
            float my = Gdx.graphics.getHeight() - ComponentSelectBox.this.getScrollPane().getY() - Gdx.input.getY();
            for (T i : getItems()) {
                Drawable entryBackground = null;
                Component text = components.apply(i);
                ListStyle style = getStyle();
                Vec2 size = text.getSize(font, this.size);
                Color color = style.fontColorUnselected;
                Vector2 mousePos = this.screenToLocalCoordinates(new Vector2(mx, my));
                if (mousePos.x > x && mousePos.x < x + getWidth() && my < y + size.y - 2 && my > y + 2) {
                    entryBackground = Gdx.input.isButtonPressed(0) ? style.down : style.over;
                    this.setSelectedIndex(getItems().indexOf(i, true));
                } else if (i == getItems().get(getSelectedIndex())) {
                    entryBackground = style.selection;
                    color = style.fontColorSelected;
                }
                if (entryBackground != null) {
                    entryBackground.draw(batch, x, y - 2, getWidth(), (float)size.y + 4);
                }
                y += text.drawGetSize(batch, font, this.size, color, x + 2, y + (float)size.y - 4).y + 4;
            }
        }
    }
}
