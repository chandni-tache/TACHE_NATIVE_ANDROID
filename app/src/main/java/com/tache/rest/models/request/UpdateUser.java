package com.tache.rest.models.request;

/**
 * Created by a_man on 4/12/2017.
 */

public class UpdateUser {
    private String mobile;
    private String name;
    private String image_url;
    private boolean is_mobile_verified;
    
    public boolean is_mobile_verified() {
        return is_mobile_verified;
    }

    public void setIs_mobile_verified(boolean is_mobile_verified) {
        this.is_mobile_verified = is_mobile_verified;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
