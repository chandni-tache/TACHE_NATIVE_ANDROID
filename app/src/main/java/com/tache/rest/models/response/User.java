package com.tache.rest.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ujjwal on 8/13/16.
 */

public class User {
    @SerializedName("pk")
    @Expose
    private int pk;

    @SerializedName("total_missions_completed")
    @Expose
    private int total_missions_completed;

    @SerializedName("total_survey_completed")
    @Expose
    private int total_survey_completed;

    @SerializedName("total_earnings")
    @Expose
    private float total_earnings;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("mobile")
    @Expose
    private String mobile;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("referral_code")
    @Expose
    private String referral_code;

    @SerializedName("image_url")
    @Expose
    private String image_url;

    @SerializedName("has_profile")
    @Expose
    private boolean has_profile;

    @SerializedName("referral_applied")
    @Expose
    private boolean referral_applied;

    @SerializedName("is_mobile_verified")
    @Expose
    private boolean is_mobile_verified;

    @SerializedName("is_email_verified")
    @Expose
    private boolean is_email_verified;

    @SerializedName("has_bank_details")
    @Expose
    private boolean has_bank_details;

    public boolean isHas_bank_details() {
        return has_bank_details;
    }

    public void setHas_bank_details(boolean has_bank_details) {
        this.has_bank_details = has_bank_details;
    }

    public boolean is_mobile_verified() {
        return is_mobile_verified;
    }

    public void setIs_mobile_verified(boolean is_mobile_verified) {
        this.is_mobile_verified = is_mobile_verified;
    }

    public boolean is_email_verified() {
        return is_email_verified;
    }

    public void setIs_email_verified(boolean is_email_verified) {
        this.is_email_verified = is_email_verified;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public boolean isHas_profile() {
        return has_profile;
    }

    public void setHas_profile(boolean has_profile) {
        this.has_profile = has_profile;
    }

    public boolean isReferral_applied() {
        return referral_applied;
    }

    public void setReferral_applied(boolean referral_applied) {
        this.referral_applied = referral_applied;
    }

    public Boolean getReferral_applied() {
        return referral_applied;
    }

    public void setReferral_applied(Boolean referral_applied) {
        this.referral_applied = referral_applied;
    }

    public String getReferral_code() {
        return referral_code;
    }

    public void setReferral_code(String referral_code) {
        this.referral_code = referral_code;
    }

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public int getTotal_missions_completed() {
        return total_missions_completed;
    }

    public void setTotal_missions_completed(int total_missions_completed) {
        this.total_missions_completed = total_missions_completed;
    }

    public int getTotal_survey_completed() {
        return total_survey_completed;
    }

    public void setTotal_survey_completed(int total_survey_completed) {
        this.total_survey_completed = total_survey_completed;
    }

    public float getTotal_earnings() {
        return total_earnings;
    }

    public void setTotal_earnings(float total_earnings) {
        this.total_earnings = total_earnings;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Boolean getHas_profile() {
        return has_profile;
    }

    public void setHas_profile(Boolean has_profile) {
        this.has_profile = has_profile;
    }
}
