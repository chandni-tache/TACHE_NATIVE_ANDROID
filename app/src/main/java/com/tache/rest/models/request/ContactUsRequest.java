package com.tache.rest.models.request;

/**
 * Created by a_man on 3/31/2017.
 */

public class ContactUsRequest {
    private String message, name, email;

    public ContactUsRequest(String message, String name, String email) {
        this.message = message;
        this.name = name;
        this.email = email;
    }
}
