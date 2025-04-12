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
import net.thewinnt.planimetry.data.registry.TagKey;
import net.thewinnt.planimetry.settings.DebugFlag;
import net.thewinnt.planimetry.ui.Notifications;
import net.thewinnt.planimetry.ui.Size;
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
    public static final Component TAGS = Component.translatable("settings.debug.registry_data.tags");
    public static final Component ELEMENTS = Component.translatable("settings.debug.registry_data.elements");
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
        this.goBack = new Label(EXIT, styles.getLabelStyle(Size.MEDIUM));

        List<BooleanProperty> list = new ArrayList<>();
        for (DebugFlag i : DebugFlag.values()) {
            list.add(DebugFlag.getOrCreateFlag(i));
        }
        flags1 = () -> new PropertyLayout(list, styles, FLAGS, Size.MEDIUM, true, StyleSet::getButtonStyleNoBg);

        registryData1 = new ArrayList<>();
        for (Registry<?> registry : Registries.ROOT.elements()) {
            PropertyGroup group = new PropertyGroup(Component.literal(registry.id().toString()));

            List<DisplayProperty> elements = new ArrayList<>();
            for (Identifier id : registry.ids()) {
                elements.add(new DisplayProperty(Component.literal(id.toString()), Component.literal(registry.get(id).toString())));
            }
            group.addProperty(new PropertyGroup(ELEMENTS, elements));


            List<PropertyGroup> tags = new ArrayList<>();
            for (TagKey<?> tag : registry.getAllTags()) {
                PropertyGroup contents = new PropertyGroup(Component.literal(tag.id().toString()));
                for (Identifier id : registry.getTagContents(tag)) {
                    contents.addProperty(new DisplayProperty(Component.literal(id.toString()), Component.literal(registry.get(id).toString())));
                }
                tags.add(contents);
            }
            group.addProperty(new PropertyGroup(TAGS, tags));

            registryData1.add(group);
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
        Notifications.addNotification("Debug flags don't work at the moment", 2000);
        this.goBack.setStyle(styles.getLabelStyle(Size.MEDIUM));
        this.goBack.setPosition(10, Gdx.graphics.getHeight() - 10, Align.topLeft);

        flags.setPosition(10, Gdx.graphics.getHeight() / 2f - 5);
        flags.setSize(Gdx.graphics.getWidth() - 20, Gdx.graphics.getHeight() / 2f - 15 - this.goBack.getPrefHeight());

        registryData.setPosition(10, 10);
        registryData.setSize(Gdx.graphics.getWidth() - 20, Gdx.graphics.getHeight() / 2f - 15);

        this.flags.setActor(flags1.get());
        this.registryData.setActor(new PropertyLayout(registryData1, styles, REGISTRY_DATA, Size.MEDIUM, true));
    }

    @Override public void customRender() {}
    @Override public void addActorsAboveFps() {}
}
