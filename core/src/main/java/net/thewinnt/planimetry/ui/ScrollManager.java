package net.thewinnt.planimetry.ui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

/** Captures scroll events and sends them to the actor the mouse is hovering over */
public class ScrollManager extends Actor {
    public ScrollManager() {
        this.addListener(new InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                Stage stage = getStage();
                if (stage != null) {
                    Actor actor = stage.hit(x, y, true);
                    if (actor == null) return false;
                    return actor.fire(event);
                }
                return false;
            }
        });
    }
}
