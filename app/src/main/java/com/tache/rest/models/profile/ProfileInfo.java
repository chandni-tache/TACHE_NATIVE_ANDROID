package com.tache.rest.models.profile;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileInfo implements Parcelable {
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("pincode")
    @Expose
    private String pincode;
    @SerializedName("area")
    @Expose
    private String area;
    @SerializedName("area_lat")
    @Expose
    private Double areaLat;
    @SerializedName("area_long")
    @Expose
    private Double areaLong;
    @SerializedName("qualification")
    @Expose
    private String qualification;
    @SerializedName("employment")
    @Expose
    private String employment;
    @SerializedName("designation")
    @Expose
    private String designation;
    @SerializedName("work_experience")
    @Expose
    private String workExperience;
    @SerializedName("annual_income")
    @Expose
    private String annualIncome;
    @SerializedName("industry")
    @Expose
    private String industry;
    @SerializedName("designation_other")
    @Expose
    private String designation_other;

    public String getDesignation_other() {
        return designation_other;
    }

    public void setDesignation_other(String designation_other) {
        this.designation_other = designation_other;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Double getAreaLat() {
        return areaLat;
    }

    public void setAreaLat(Double areaLat) {
        this.areaLat = areaLat;
    }

    public Double getAreaLong() {
        return areaLong;
    }

    public void setAreaLong(Double areaLong) {
        this.areaLong = areaLong;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getEmployment() {
        return employment;
    }

    public void setEmployment(String employment) {
        this.employment = employment;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(String workExperience) {
        this.workExperience = workExperience;
    }

    public String getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(String annualIncome) {
        this.annualIncome = annualIncome;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.gender);
        dest.writeString(this.dob);
        dest.writeString(this.pincode);
        dest.writeString(this.area);
        dest.writeValue(this.areaLat);
        dest.writeValue(this.areaLong);
        dest.writeString(this.qualification);
        dest.writeString(this.employment);
        dest.writeString(this.designation);
        dest.writeString(this.workExperience);
        dest.writeString(this.annualIncome);
        dest.writeString(this.industry);
        dest.writeString(this.designation_other);
    }

    public ProfileInfo() {
    }

    protected ProfileInfo(Parcel in) {
        this.gender = in.readString();
        this.dob = in.readString();
        this.pincode = in.readString();
        this.area = in.readString();
        this.areaLat = (Double) in.readValue(Double.class.getClassLoader());
        this.areaLong = (Double) in.readValue(Double.class.getClassLoader());
        this.qualification = in.readString();
        this.employment = in.readString();
        this.designation = in.readString();
        this.workExperience = in.readString();
        this.annualIncome = in.readString();
        this.industry = in.readString();
        this.designation_other = in.readString();
    }

    public static final Parcelable.Creator<ProfileInfo> CREATOR = new Parcelable.Creator<ProfileInfo>() {
        @Override
        public ProfileInfo createFromParcel(Parcel source) {
            return new ProfileInfo(source);
        }

        @Override
        public ProfileInfo[] newArray(int size) {
            return new ProfileInfo[size];
        }
    };
}