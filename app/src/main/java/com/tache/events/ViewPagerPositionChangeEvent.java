package com.tache.events;

/**
 * Created by mayank on 12/10/16.
 */

public class ViewPagerPositionChangeEvent {
    private int position;

    public ViewPagerPositionChangeEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
