package com.tache.events;

/**
 * Created by mayank on 31/3/17.
 */

public class ProfileUpdatedEvent {
    boolean updated;

    public ProfileUpdatedEvent(boolean b) {
        this.updated = b;
    }

    public boolean isUpdated() {
        return updated;
    }
}
