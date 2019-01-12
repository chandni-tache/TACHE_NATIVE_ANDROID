package com.tache.rest.models.errors;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ujjwal on 8/13/16.
 */

public class BaseError {
    @SerializedName("non_field_errors")
    @Expose
    private ArrayList<String> nonFieldErrors = new ArrayList<>();

    public ArrayList<String> getNonFieldErrors() {
        return nonFieldErrors;
    }

    public void setNonFieldErrors(ArrayList<String> nonFieldErrors) {
        this.nonFieldErrors = nonFieldErrors;
    }
}
