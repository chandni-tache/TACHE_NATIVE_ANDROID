package com.tache.model;

import java.util.ArrayList;

/**
 * Created by sanjay on 26/8/16.
 */
public class TodayModel
{
    private String intNumber="";
    private String Image="";
    private String name="";
    private String email="";
    private String secImage="";
    private String getIntNumber="";

    public String getIntNumber() {
        return intNumber;
    }

    public void setIntNumber(String intNumber) {
        this.intNumber = intNumber;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSecImage() {
        return secImage;
    }

    public void setSecImage(String secImage) {
        this.secImage = secImage;
    }

    public String getGetIntNumber() {
        return getIntNumber;
    }

    public void setGetIntNumber(String getIntNumber) {
        this.getIntNumber = getIntNumber;
    }

    public static ArrayList<TodayModel> getDemoData() {
        ArrayList<TodayModel> todayModels = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final TodayModel sched = new TodayModel();
            sched.setEmail("@ammysm ");
            sched.setGetIntNumber("9015264");
            sched.setIntNumber("04" );
            sched.setName("Amit Sharma");
            todayModels.add( sched );
        }
        return todayModels;
    }
}
