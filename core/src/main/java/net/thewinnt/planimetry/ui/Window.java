package net.thewinnt.planimetry.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.text.Component;

import java.util.function.Consumer;

public class Window extends WidgetGroup {
    private final StyleSet styles;
    private final Component name;
    private Vector2 nameSize;
    private Actor actor;
    private final Button minimize;
    private final Button close;
    private boolean open;
    private DragAction dragging;
    private ResizeDirection resizeDirection;
    private Consumer<Window> dockListener;
    private Consumer<Actor> restyle;

    public Window(StyleSet styles, Component name, boolean closeable) {
        this.styles = styles;
        this.name = name;
        this.nameSize = name == null ? new Vector2(0, Size.MEDIUM.lines(1)) : name.getSize(styles.font, Size.MEDIUM).withY(Size.MEDIUM.lines(1)).toVector2f();
        this.minimize = new Button(styles.getMinimizeStyle(Size.MEDIUM, true));
        this.minimize.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setOpen(!open);
                if (dockListener != null) dockListener.accept(Window.this);
            }
        });

        if (closeable) {
            this.close = new Button(styles.getCloseStyle(Size.MEDIUM, true));
            this.close.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Window.this.remove();
                }
            });
        } else {
            this.close = null;
        }

        final int hitSize = 20;
        this.addListener(new ActorGestureListener() {
            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (open) {
                    if (y < hitSize) {
                        dragging = DragAction.RESIZE;
                        resizeDirection = ResizeDirection.DOWN;
                    } else if (y > getHeight() - hitSize) {
                        dragging = DragAction.RESIZE;
                        resizeDirection = ResizeDirection.UP;
                    }
                    if (x < hitSize) {
                        dragging = DragAction.RESIZE;
                        resizeDirection = switch (resizeDirection) {
                            case DOWN -> ResizeDirection.LEFT_DOWN;
                            case UP -> ResizeDirection.LEFT_UP;
                            case null, default -> ResizeDirection.LEFT;
                        };
                    } else if (x > getWidth() - hitSize) {
                        dragging = DragAction.RESIZE;
                        resizeDirection = switch (resizeDirection) {
                            case DOWN -> ResizeDirection.RIGHT_DOWN;
                            case UP -> ResizeDirection.RIGHT_UP;
                            case null, default -> ResizeDirection.RIGHT;
                        };
                    }
                }
                if (dragging == null && y > getHeight() - nameSize.y) {
                    dragging = DragAction.DRAG;
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dragging = null;
                resizeDirection = null;
            }

            @Override
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                if (dragging == DragAction.DRAG) {
                    Gdx.app.log("Window", String.format("Dragging window: %s / %s", deltaX, deltaY));
                    moveBy(deltaX, deltaY);
                    event.stop();
                } else if (dragging == DragAction.RESIZE && resizeDirection != null) {
                    Gdx.app.log("Window", String.format("Resizing window to %s: %s / %s", resizeDirection, deltaX, deltaY));
                    setSize(getWidth() + deltaX * resizeDirection.x, getHeight() + deltaY * resizeDirection.y);
                    moveBy(deltaX * ((-resizeDirection.x + 1) / 2), deltaY * ((-resizeDirection.y + 1) / 2));
                    event.stop();
                }
            }
        });
        this.addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                if (open) {
                    ResizeDirection direction = null;
                    if (y < hitSize) {
                        direction = ResizeDirection.DOWN;
                    } else if (y > getHeight() - hitSize) {
                        direction = ResizeDirection.UP;
                    }
                    if (x < hitSize) {
                        direction = switch (direction) {
                            case DOWN -> ResizeDirection.LEFT_DOWN;
                            case UP -> ResizeDirection.LEFT_UP;
                            case null, default -> ResizeDirection.LEFT;
                        };
                    } else if (x > getWidth() - hitSize) {
                        direction = switch (direction) {
                            case DOWN -> ResizeDirection.RIGHT_DOWN;
                            case UP -> ResizeDirection.RIGHT_UP;
                            case null, default -> ResizeDirection.RIGHT;
                        };
                    }
                    Gdx.graphics.setSystemCursor(switch (direction) {
                        case UP, DOWN -> Cursor.SystemCursor.VerticalResize;
                        case LEFT, RIGHT -> Cursor.SystemCursor.HorizontalResize;
                        case LEFT_DOWN, RIGHT_UP -> Cursor.SystemCursor.NESWResize;
                        case LEFT_UP, RIGHT_DOWN -> Cursor.SystemCursor.NWSEResize;
                        case null -> Cursor.SystemCursor.Arrow;
                    });
                }
                return super.mouseMoved(event, x, y);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }
        });

        super.addActor(this.minimize);
        if (closeable) super.addActor(this.close);

        invalidateHierarchy();
    }

    public void setActor(Actor actor) {
        if (this.actor != null) {
            super.removeActor(actor);
        }
        this.actor = actor;
        super.addActor(actor);
        this.open = true;
    }

    public Actor getActor() {
        return actor;
    }

    public void setDockListener(Consumer<Window> dockListener) {
        this.dockListener = dockListener;
    }

    public void setOpen(boolean open) {
        this.open = open;
        if (open) {
            moveBy(0, -actor.getHeight());
        } else {
            float offset = getHeight() - nameSize.y;
            setHeight(nameSize.y);
            moveBy(0, offset);
        }
        minimize.setStyle(open ? styles.getMinimizeStyle(Size.MEDIUM, true) : styles.getMaximizeStyle(Size.MEDIUM, true));
        if (open) {
            super.addActorAt(close == null ? 1 : 2, actor);
        } else {
            super.removeActor(actor);
        }
    }

    /** Do not use - use {@link #setActor(Actor)} instead! */
    @Override
    @Deprecated
    public void addActor(Actor actor) {
        throw new UnsupportedOperationException();
    }

    /** Allows children to add actors to the window */
    protected final void addActorAnyways(Actor actor) {
        super.addActor(actor);
    }

    @Override
    public float getMinWidth() {
        if (open && actor instanceof Layout layout) {
            return layout.getMinWidth() + Size.MEDIUM.lines(2);
        }
        return Size.MEDIUM.lines(2);
    }

    @Override
    public float getMinHeight() {
        if (open && actor instanceof Layout layout) {
            return layout.getMinHeight() + nameSize.y;
        }
        return nameSize.y;
    }

    @Override
    public float getPrefWidth() {
        if (open && actor instanceof Layout layout) {
            return Math.max(layout.getPrefWidth(), nameSize.x);
        }
        return nameSize.x;
    }

    @Override
    public float getPrefHeight() {
        if (open && actor instanceof Layout layout) {
            return Math.max(layout.getPrefHeight(), nameSize.x);
        }
        return nameSize.y;
    }

    public void setRestyler(Consumer<Actor> restyle) {
        this.restyle = restyle;
    }

    @Override
    public void layout() {
        this.nameSize = name == null ? new Vector2(0, Size.MEDIUM.lines(1)) : name.getSize(styles.font, Size.MEDIUM).withY(Size.MEDIUM.lines(1)).toVector2f();
        if (restyle != null) restyle.accept(actor);
        if (this.getWidth() < this.getMinWidth()) {
            this.setWidth(getMinWidth());
        }
        if (this.getHeight() < this.getMinHeight()) {
            this.setHeight(getMinHeight());
        }
        if (open) {
            this.actor.setBounds(0, 0, this.getWidth(), this.getHeight() - nameSize.y);
            if (this.actor.getWidth() > this.getWidth() || this.actor.getHeight() > this.getHeight() - nameSize.y) {
                this.setPosition(actor.getX(), actor.getY());
                this.setSize(Math.max(actor.getWidth(), this.getWidth()), Math.max(actor.getHeight(), this.getHeight() + nameSize.y));
            }
        }
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        if (this.getX() < 0) this.setX(0);
        if (this.getY() < 0) this.setY(0);
        if (this.getX() + this.getWidth() > width) this.setX(width - this.getWidth());
        if (this.getY() + this.getHeight() > height) this.setY(height - this.getHeight());
        if (close != null) {
            close.setBounds(getWidth() - nameSize.y, getHeight() - nameSize.y, nameSize.y, nameSize.y);
            minimize.setBounds(getWidth() - nameSize.y * 2, getHeight() - nameSize.y, nameSize.y, nameSize.y);
        } else {
            minimize.setBounds(getWidth() - nameSize.y, getHeight() - nameSize.y, nameSize.y, nameSize.y);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (open) {
            styles.normal.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }
        styles.pressed.draw(batch, this.getX(), this.getY() + this.getHeight() - nameSize.y, this.getWidth(), nameSize.y);
        if (name != null) {
            name.draw(batch, styles.font, Size.MEDIUM, Theme.current().textUI(), this.getX() + 2, this.getY() + this.getHeight() - 3);
        }
        super.draw(batch, parentAlpha);
        // TODO window manager
        // TODO split user interface
    }

    public enum DragAction {
        DRAG,
        RESIZE
    }

    public enum ResizeDirection {
        LEFT(-1, 0),
        RIGHT(1, 0),
        UP(0, 1),
        DOWN(0, -1),
        LEFT_UP(-1, 1),
        LEFT_DOWN(-1, -1),
        RIGHT_DOWN(1, -1),
        RIGHT_UP(1, 1);

        public final int x;
        public final int y;

        ResizeDirection(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
