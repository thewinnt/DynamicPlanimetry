package net.thewinnt.planimetry.ui.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.data.TreeNode;
import net.thewinnt.planimetry.ui.ListSwitch;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.ui.text.Component;

public class PropertyLayout extends WidgetGroup {
    private final Container<VerticalGroup> pane;
    public final ListSwitch list;
    private final List<PropertyLayout> children = new ArrayList<>();
    private int id = -1;
    private boolean open;

    public PropertyLayout(Collection<? extends Property<?>> properties, StyleSet styles, Component name, Size size, boolean open) {
        this(properties, styles, name, size, open, StyleSet::getButtonStyleToggleable);
    }

    public PropertyLayout(Collection<? extends Property<?>> properties, StyleSet styles, Component name, Size size, boolean open, BiFunction<StyleSet, Size, Button.ButtonStyle> styleGetter) {
        super();
        this.pane = new Container<>();
        if (name != null) {
            this.list = new ListSwitch(name, styles, size, styleGetter);
            list.setChecked(open);
            list.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (list.isChecked()) {
                        PropertyLayout.this.addActor(pane);
                        PropertyLayout.this.open = true;
                    } else {
                        PropertyLayout.this.removeActor(pane);
                        PropertyLayout.this.open = false;
                    }
                    if (DynamicPlanimetry.getInstance().editorScreen != null) {
                        DynamicPlanimetry.getInstance().editorScreen.layout();
                    }
                }
            });
        } else {
            this.list = null;
        }

        // pane.setupOverscroll(Gdx.graphics.getHeight() / Size.MEDIUM.getFactor() * 1.1f, 10, 200);

        VerticalGroup propertyList = new VerticalGroup().top().left().expand().fill().pad(2, 5, 2, 0);
        propertyList.setFillParent(true);
        int idCounter = 0;
        for (Property<?> i : properties) {
            PropertyEntry entry = new PropertyEntry(i, styles, size);
            propertyList.addActor(entry);
            if (entry.getPropertySetup() instanceof PropertyLayout layout) {
                this.children.add(layout);
                layout.id = idCounter++;
            }
        }
        this.pane.setActor(propertyList);
        this.pane.align(Align.bottomLeft);
        this.pane.fill();

        this.open = open;
        if (list != null) this.addActor(list);
        if (open) this.addActor(pane);
    }

    @Override
    public void layout() {
        if (list == null) {
            this.pane.setBounds(0, 0, getWidth(), getHeight());
        } else {
            this.list.setBounds(0, getHeight() - list.getPrefHeight(), getWidth(), list.getPrefHeight());
            this.pane.setBounds(0, 0, getWidth(), getHeight() - list.getPrefHeight() - 2);
        }
    }

    @Override
    public float getPrefHeight() {
        if (this.list == null) {
            return this.pane.getPrefHeight();
        } else if (this.list.isChecked()) {
            return this.list.getPrefHeight() + this.pane.getPrefHeight();
        } else {
            return this.list.getPrefHeight();
        }
    }

    @Override
    public float getPrefWidth() {
        if (this.list == null) {
            return this.pane.getPrefWidth();
        }
        return Math.max(this.list.getPrefWidth(), this.pane.getPrefWidth());
    }

    @Override
    public float getMinHeight() {
        if (this.list != null && this.list.isChecked()) {
            return this.list.getPrefHeight() * 2;
        } else {
            return this.list.getPrefHeight();
        }
    }

    @Override
    public float getMinWidth() {
        return this.pane.getMinWidth();
    }

    public TreeNode<OpenStatus> getOpenChildren() {
        if (children.isEmpty()) {
            return new TreeNode<>(this.getStatus(), List.of());
        }
        List<TreeNode<OpenStatus>> children = new ArrayList<>();
        for (var i : this.children) {
            children.add(i.getOpenChildren());
        }
        return new TreeNode<>(this.getStatus(), children);
    }

    public void applyOpenStatus(TreeNode<OpenStatus> tree) {
        tree.visit(branch -> {
            PropertyLayout i = this;
            if (branch.size() > 1) {
                for (int j = 1; j < branch.size(); j++) {
                    i = i.getChildEntry(branch.get(j).id);
                    if (i == null) return;
                }
            }
            i.setOpen(branch.get(branch.size() - 1).open);
        });
    }

    private OpenStatus getStatus() {
        return new OpenStatus(this.id, this.open);
    }

    private PropertyLayout getChildEntry(int id) {
        if (id >= this.children.size()) return null;
        return this.children.get(id);
    }

    public void setOpen(boolean open) {
        if (this.list != null) {
            this.list.setChecked(open);
        }
        if (open) {
            this.addActor(pane);
        } else {
            this.removeActor(pane);
        }
        this.open = open;
        DynamicPlanimetry.getInstance().editorScreen.layout();
    }

    public record OpenStatus(int id, boolean open) {}
}
