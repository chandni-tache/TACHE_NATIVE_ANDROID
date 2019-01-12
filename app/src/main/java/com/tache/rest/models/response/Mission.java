package com.tache.rest.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by a_man on 3/31/2017.
 */

public class Mission implements Parcelable {
    @SerializedName("category")
    @Expose
    private int category;

    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("price")
    @Expose
    private float price;

    @SerializedName("company_name")
    @Expose
    private String company_name;

    @SerializedName("company_logo")
    @Expose
    private String company_logo;

    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("location_lat")
    @Expose
    private double location_lat;

    @SerializedName("location_long")
    @Expose
    private double location_long;

    @SerializedName("date_from")
    @Expose
    private String date_from;

    @SerializedName("date_to")
    @Expose
    private String date_to;

    @SerializedName("day_allowed")
    @Expose
    private String day_allowed;

    @SerializedName("about")
    @Expose
    private String about;

    @SerializedName("ensure_things")
    @Expose
    private String ensure_things;

    @SerializedName("key_points")
    @Expose
    private String key_points;

    @SerializedName("survey")
    @Expose
    private Survey survey;

    protected Mission(Parcel in) {
        category = in.readInt();
        status = in.readInt();
        id = in.readInt();
        price = in.readFloat();
        company_name = in.readString();
        company_logo = in.readString();
        location = in.readString();
        location_lat = in.readDouble();
        location_long = in.readDouble();
        date_from = in.readString();
        date_to = in.readString();
        day_allowed = in.readString();
        about = in.readString();
        ensure_things = in.readString();
        key_points = in.readString();
        survey = in.readParcelable(Survey.class.getClassLoader());
    }

    public static final Creator<Mission> CREATOR = new Creator<Mission>() {
        @Override
        public Mission createFromParcel(Parcel in) {
            return new Mission(in);
        }

        @Override
        public Mission[] newArray(int size) {
            return new Mission[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLocation_lat() {
        return location_lat;
    }

    public void setLocation_lat(double location_lat) {
        this.location_lat = location_lat;
    }

    public double getLocation_long() {
        return location_long;
    }

    public void setLocation_long(double location_long) {
        this.location_long = location_long;
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

    public String getDay_allowed() {
        return day_allowed;
    }

    public void setDay_allowed(String day_allowed) {
        this.day_allowed = day_allowed;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getEnsure_things() {
        return ensure_things;
    }

    public void setEnsure_things(String ensure_things) {
        this.ensure_things = ensure_things;
    }

    public String getKey_points() {
        return key_points;
    }

    public void setKey_points(String key_points) {
        this.key_points = key_points;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public Mission() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(category);
        dest.writeInt(status);
        dest.writeInt(id);
        dest.writeFloat(price);
        dest.writeString(company_name);
        dest.writeString(company_logo);
        dest.writeString(location);
        dest.writeDouble(location_lat);
        dest.writeDouble(location_long);
        dest.writeString(date_from);
        dest.writeString(date_to);
        dest.writeString(day_allowed);
        dest.writeString(about);
        dest.writeString(ensure_things);
        dest.writeString(key_points);
        dest.writeParcelable(survey, flags);
    }
}
