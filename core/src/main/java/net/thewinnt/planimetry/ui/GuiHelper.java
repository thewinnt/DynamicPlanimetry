package net.thewinnt.planimetry.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.text.Component;

public class GuiHelper {
    public static TextButton createTextButton(String key, StyleSet style, Size size, Runnable action) {
        TextButton output = new TextButton(DynamicPlanimetry.translate(key), style.getButtonStyle(size, true));
        output.addListener(new ButtonListener(action));
        return output;
    }

    public static class ButtonListener extends ChangeListener {
        private final Runnable action;

        public ButtonListener(Runnable action) {
            this.action = action;
        }

        @Override
        public void changed(ChangeEvent event, Actor actor) {
            action.run();
        }
    }
}
