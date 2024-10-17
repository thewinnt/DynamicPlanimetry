package net.thewinnt.planimetry.event;

public class Event {
    private boolean canceled;

    public Event() {}

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
