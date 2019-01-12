package com.tache.rest.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by a_man on 4/11/2017.
 */

public class SurveysHistory {
    @SerializedName("survey")
    @Expose
    private Surveys surveys;

    public Surveys getSurveys() {
        return surveys;
    }

    public void setSurveys(Surveys surveys) {
        this.surveys = surveys;
    }
}
