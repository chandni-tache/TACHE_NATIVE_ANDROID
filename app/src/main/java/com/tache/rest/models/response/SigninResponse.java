package com.tache.rest.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ujjwal on 8/13/16.
 */

public class SigninResponse {
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("user")
    @Expose
    private User user;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}