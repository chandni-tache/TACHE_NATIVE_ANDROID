package com.tache.rest.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by a_man on 4/4/2017.
 */

public class Surveys {
    @SerializedName("category")
    @Expose
    private int category;

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("responses_received")
    @Expose
    private int responses_received;

    @SerializedName("responses_required")
    @Expose
    private int responses_required;

    @SerializedName("company_name")
    @Expose
    private String company_name;

    @SerializedName("company_logo")
    @Expose
    private String company_logo;

    @SerializedName("date_from")
    @Expose
    private String date_from;

    @SerializedName("date_to")
    @Expose
    private String date_to;

    @SerializedName("survey")
    @Expose
    private Survey survey;

    public int getResponses_required() {
        return responses_required;
    }

    public void setResponses_required(int responses_required) {
        this.responses_required = responses_required;
    }

    public Survey getSurvey() {
        return survey;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getResponses_received() {
        return responses_received;
    }

    public void setResponses_received(int responses_received) {
        this.responses_received = responses_received;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_logo() {
        return company_logo;
    }

    public void setCompany_logo(String company_logo) {
        this.company_logo = company_logo;
    }

    public String getDate_from() {
        return date_from;
    }

    public void setDate_from(String date_from) {
        this.date_from = date_from;
    }

    public String getDate_to() {
        return date_to;
    }

    public void setDate_to(String date_to) {
        this.date_to = date_to;
    }
}
