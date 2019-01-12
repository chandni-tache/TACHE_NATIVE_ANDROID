package com.tache.events;

/**
 * Created by a_man on 4/11/2017.
 */

public class NavigateToEvent {
    String navigateTo;

    public NavigateToEvent(String surveyHistory) {
        this.navigateTo = surveyHistory;
    }

    public String getNavigateTo() {
        return navigateTo;
    }
}
