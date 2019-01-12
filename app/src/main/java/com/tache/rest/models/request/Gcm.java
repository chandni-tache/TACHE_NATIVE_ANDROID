package com.tache.rest.models.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mayank on 01/12/16.
 */

public class Gcm {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("registration_id")
    @Expose
    private String registration_id;

    @SerializedName("device_id")
    @Expose
    private String device_id;

    @SerializedName("active")
    @Expose
    private Boolean active;

    @SerializedName("date_created")
    @Expose
    private String date_created;

    public Gcm(String registration_id, Boolean active) {
        this.registration_id = registration_id;
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistration_id() {
        return registration_id;
    }

    public void setRegistration_id(String registration_id) {
        this.registration_id = registration_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }
}