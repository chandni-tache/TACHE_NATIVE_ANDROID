package com.tache.rest.models.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ujjwal on 8/13/16.
 */

public class UserUpdateRequest{

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("profile_image")
    @Expose
    private String profile_image;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("is_mobile_verified")
    @Expose
    private Boolean isMobileVerified;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public Boolean getIs_mobile_verified() {
        return isMobileVerified;
    }

    public void setIs_mobile_verified(Boolean isMobileVerified) {
        this.isMobileVerified = isMobileVerified;
    }
}
