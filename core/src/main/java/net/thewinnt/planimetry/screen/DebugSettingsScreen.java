package net.thewinnt.planimetry.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.data.registry.Identifier;
import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.data.registry.Registry;
import net.thewinnt.planimetry.settings.DebugFlag;
import net.thewinnt.planimetry.ui.Notifications;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.properties.PropertyLayout;
import net.thewinnt.planimetry.ui.properties.types.BooleanProperty;
import net.thewinnt.planimetry.ui.properties.types.DisplayProperty;
import net.thewinnt.planimetry.ui.properties.types.PropertyGroup;
import net.thewinnt.planimetry.ui.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DebugSettingsScreen extends FlatUIScreen {
    public static final CharSequence EXIT = Component.translatable("ui.settings.exit");
    public static final Component FLAGS = Component.translatable("settings.debug.flags");
    public static final Component REGISTRY_DATA = Component.translatable("settings.debug.registry_data");
    private Label goBack;
    private ScrollPane flags;
    private ScrollPane registryData;
    private Supplier<PropertyLayout> flags1;
    private List<PropertyGroup> registryData1;

    public DebugSettingsScreen(DynamicPlanimetry app) {
        super(app);
    }

    @Override
    public void addActorsBelowFps() {
        this.goBack = new Label(EXIT, styles.getLabelStyle(StyleSet.Size.MEDIUM));

        List<BooleanProperty> list = new ArrayList<>();
        for (DebugFlag i : DebugFlag.values()) {
            list.add(DebugFlag.getOrCreateFlag(i));
        }
        flags1 = () -> new PropertyLayout(list, styles, FLAGS, StyleSet.Size.MEDIUM, true, StyleSet::getButtonStyleNoBg);

        registryData1 = new ArrayList<>();
        for (Registry<?> i : Registries.ROOT.elements()) {
            List<DisplayProperty> properties = new ArrayList<>();
            for (Identifier j : i.ids()) {
                properties.add(new DisplayProperty(Component.literal(j.toString()), Component.literal(i.byName(j).toString())));
            }
            registryData1.add(new PropertyGroup(Component.literal(i.id().toString()), properties));
        }

        this.goBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(DynamicPlanimetry.SETTINGS_SCREEN);
            }
        });

        this.flags = new ScrollPane(null);
        this.registryData = new ScrollPane(null);

        stage.addActor(this.goBack);
        stage.addActor(this.flags);
        stage.addActor(this.registryData);
    }

    @Override
    public void show() {
        super.show();
        Notifications.addNotification("Debug flags don't save at the moment", 2000);
        this.goBack.setStyle(styles.getLabelStyle(StyleSet.Size.MEDIUM));
        this.goBack.setPosition(10, Gdx.graphics.getHeight() - 10, Align.topLeft);

        flags.setPosition(10, Gdx.graphics.getHeight() / 2f - 5);
        flags.setSize(Gdx.graphics.getWidth() - 20, Gdx.graphics.getHeight() / 2f - 15 - this.goBack.getPrefHeight());

        registryData.setPosition(10, 10);
        registryData.setSize(Gdx.graphics.getWidth() - 20, Gdx.graphics.getHeight() / 2f - 15);

        this.flags.setActor(flags1.get());
        this.registryData.setActor(new PropertyLayout(registryData1, styles, REGISTRY_DATA, StyleSet.Size.MEDIUM, true));
    }

    @Override public void customRender() {}
    @Override public void addActorsAboveFps() {}
}
