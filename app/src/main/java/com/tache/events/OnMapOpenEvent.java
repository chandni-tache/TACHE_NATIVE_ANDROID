package com.tache.events;

/**
 * Created by mayank on 3/4/17.
 */

public class OnMapOpenEvent {

    private String fragName;

    public OnMapOpenEvent(String fragName) {
        this.fragName = fragName;
    }

    public String getFragName() {
        return fragName;
    }
}
