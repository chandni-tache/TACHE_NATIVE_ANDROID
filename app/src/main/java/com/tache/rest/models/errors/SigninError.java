package com.tache.rest.models.errors;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ujjwal on 8/13/16.
 */

public class SigninError extends BaseError{
    @SerializedName("error")
    @Expose
    private String error = "";

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
