package com.tache.rest.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by a_man on 3/31/2017.
 */

public class Survey implements Parcelable {
    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("tool_survey_id")
    @Expose
    private long tool_survey_id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("id")
    @Expose
    private String id;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTool_survey_id() {
        return tool_survey_id;
    }

    public void setTool_survey_id(long tool_survey_id) {
        this.tool_survey_id = tool_survey_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status);
        dest.writeLong(this.tool_survey_id);
        dest.writeString(this.title);
        dest.writeString(this.id);
    }

    public Survey() {
    }

    protected Survey(Parcel in) {
        this.status = in.readInt();
        this.tool_survey_id = in.readLong();
        this.title = in.readString();
        this.id = in.readString();
    }

    public static final Parcelable.Creator<Survey> CREATOR = new Parcelable.Creator<Survey>() {
        @Override
        public Survey createFromParcel(Parcel source) {
            return new Survey(source);
        }

        @Override
        public Survey[] newArray(int size) {
            return new Survey[size];
        }
    };
}
