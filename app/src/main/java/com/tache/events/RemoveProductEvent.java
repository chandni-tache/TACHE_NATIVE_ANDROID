package com.tache.events;

/**
 * Created by mayank on 15/11/16.
 */

public class RemoveProductEvent {
    private int pos;

    public RemoveProductEvent(int pos) {
        this.pos = pos;
    }

    public int getPos() {
        return pos;
    }
}
