package net.thewinnt.planimetry.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Notifications extends Actor implements Disposable {
    private static final List<Notifications> INSTANCES = new ArrayList<>();
    private static final List<Notification> NOTIFICATIONS = new ArrayList<>();
    private final ShapeDrawer drawer;
    private final FontProvider font;
    private final GlyphLayout layout = new GlyphLayout();
    private final List<PositionedText> caches = new ArrayList<>();

    public Notifications(ShapeDrawer drawer, FontProvider font) {
        super();
        INSTANCES.add(this);
        this.drawer = drawer;
        this.font = font;
        updateCaches();
        setTouchable(Touchable.disabled);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        final float x1 = 20;
        final float x2 = Gdx.graphics.getWidth() - 20;
        float y = Gdx.graphics.getHeight() - 20;
        float targ_y = y;
        List<Notification> removals = new ArrayList<>();
        for (int i = 0; i < NOTIFICATIONS.size(); i++) {
            PositionedText cache = caches.get(i);
            Notification notification = NOTIFICATIONS.get(i);
            targ_y = y;
            long dt = System.currentTimeMillis() - notification.timestamp;
            if (dt < 500) {
                y = MathUtils.lerp(Gdx.graphics.getHeight() + cache.height + 20, targ_y, 1f - (float)Math.pow(1 - dt / 500f, 5));
            } else if (dt > notification.lengthMillis + 500) {
                removals.add(notification);
                continue;
            } else if (dt > notification.lengthMillis) {
                y = MathUtils.lerp(targ_y, Gdx.graphics.getHeight() + cache.height + 20, 1f - (float)Math.pow((notification.lengthMillis - dt + 500) / 500f, 5));
                targ_y = y;
            } else {
                y = targ_y;
            }
            cache.cache.setPosition(0, y - cache.y);
            drawer.setColor(Theme.current().outline());
            // drawer.filledRectangle(10, y - cache.height - 15, x2 + 5, cache.height + 20, DynamicPlanimetry.COLOR_BUTTON);
            drawer.sector(x1, y, 10, (float)Math.toRadians(90), (float)Math.toRadians(90), Theme.current().button(), Theme.current().button());
            drawer.sector(x2, y, 10, 0, (float)Math.toRadians(90), Theme.current().button(), Theme.current().button());
            drawer.sector(x1, y - cache.height - 10, 10, (float)Math.toRadians(180), (float)Math.toRadians(90), Theme.current().button(), Theme.current().button());
            drawer.sector(x2, y - cache.height - 10, 10, (float)Math.toRadians(270), (float)Math.toRadians(90), Theme.current().button(), Theme.current().button());
            drawer.filledRectangle(x1 - 10, y - cache.height - 10, 10, cache.height + 10, Theme.current().button());
            drawer.filledRectangle(x2, y - cache.height - 10, 10, cache.height + 10, Theme.current().button());
            drawer.filledRectangle(x1, y, x2 - x1, 10, Theme.current().button());
            drawer.filledRectangle(x1, y - cache.height - 20, x2 - x1, 10, Theme.current().button());
            drawer.filledRectangle(x1, y - cache.height - 10, x2 - x1, cache.height + 10, Theme.current().button());
            drawer.arc(x1, y, 10, (float)Math.toRadians(90), (float)Math.toRadians(90), 4);
            drawer.arc(x2, y, 10, 0, (float)Math.toRadians(90), 4);
            drawer.arc(x1, y - cache.height - 10, 10, (float)Math.toRadians(180), (float)Math.toRadians(90), 4);
            drawer.arc(x2, y - cache.height - 10, 10, (float)Math.toRadians(270), (float)Math.toRadians(90), 4);
            drawer.line(x1 - 10, y - cache.height - 10, x1 - 10, y, 4);
            drawer.line(x2 + 10, y - cache.height - 10, x2 + 10, y, 4);
            drawer.line(x1, y + 10, x2, y + 10, 4);
            drawer.line(x1, y - cache.height - 20, x2, y - cache.height - 20, 4);
            // drawer.setColor(Color.GREEN);
            drawer.line(x1 - 8, y - cache.height - 16, Math.max(MathUtils.lerp(x1 - 8, x2 + 8, 1 - dt / (float)notification.lengthMillis), x1 - 10), y - cache.height - 16, 4);
            if (DynamicPlanimetry.DEBUG_MODE) {
                font.getFont(40, Color.GREEN).draw(batch, String.valueOf(dt), x1, y);
            }
            cache.draw(batch);
            y = targ_y - cache.height - 40;
        }
        if (removals.size() > 0) {
            NOTIFICATIONS.removeAll(removals);
            updateCaches();
        }

    }

    public void updateCaches() {
        caches.clear();
        float y = Gdx.graphics.getHeight() - 20;
        BitmapFont font = this.font.getFont(Gdx.graphics.getHeight() / 20, Theme.current().textButton());
        for (Notification i : NOTIFICATIONS) {
            this.layout.setText(font, i.entry, 0, i.entry.length(), Theme.current().textButton(), getWidth() - 40, Align.center, true, null);
            BitmapFontCache cache = font.newFontCache();
            cache.setText(layout, 20, y);
            this.caches.add(new PositionedText(cache, y, layout.height));
            y -= layout.height;
            y -= 40;
        }
    }

    public static void addNotification(String entry, int lengthMillis) {
        NOTIFICATIONS.add(new Notification(entry, lengthMillis, System.currentTimeMillis()));
        for (Notifications i : INSTANCES) {
            i.updateCaches();
        }
    }

    @Override
    public void dispose() {
        INSTANCES.remove(this);
    }

    private static record Notification(String entry, int lengthMillis, long timestamp) {}
    private static record PositionedText(BitmapFontCache cache, float y, float height) {
        public void draw(Batch batch) {
            cache.draw(batch);
        }
    }
}
