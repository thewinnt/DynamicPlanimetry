package net.thewinnt.planimetry.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
                    if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) && amountX == 0) {
                        // shift + mouse wheel = horizontal scrolling
                        InputEvent horizontal = new InputEvent();
                        horizontal.setType(InputEvent.Type.scrolled);
                        horizontal.setStageX(event.getStageX());
                        horizontal.setStageY(event.getStageY());
                        horizontal.setScrollAmountX(event.getScrollAmountY());
                        return actor.fire(horizontal);
                    } else {
                        return actor.fire(event);
                    }
                }
                return false;
            }
        });
    }
}
