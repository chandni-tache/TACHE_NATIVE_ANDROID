package com.tache.rest.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by a_man on 4/11/2017.
 */

public class MissionHistory {
    @SerializedName("approval_status")
    @Expose
    private int approval_status;

    @SerializedName("survey")
    @Expose
    private Mission mission;

    public int getApproval_status() {
        return approval_status;
    }

    public void setApproval_status(int approval_status) {
        this.approval_status = approval_status;
    }

    public Mission getMission() {
        return mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }
}
