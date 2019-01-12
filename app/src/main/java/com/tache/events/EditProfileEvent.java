package com.tache.events;

import com.tache.rest.models.profile.ProfileInfo;

/**
 * Created by mayank on 31/3/17.
 */

public class EditProfileEvent {

    private ProfileInfo profileInfo;
    private boolean isTypeEdit;
    private int to;

    public EditProfileEvent(ProfileInfo profileInfo, boolean isTypeEdit, int index) {
        this.profileInfo = profileInfo;
        this.isTypeEdit = isTypeEdit;
        this.to = index;
    }

    public ProfileInfo getProfileInfo() {
        return profileInfo;
    }

    public boolean isTypeEdit() {
        return isTypeEdit;
    }

    public int getTo() {
        return to;
    }
}
