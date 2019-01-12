package com.tache.rest.services;


import com.google.gson.JsonObject;
import com.tache.rest.models.BaseListModel;
import com.tache.rest.models.request.Gcm;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by mayank on 01/12/16.
 */

public interface DeviceService {

    @POST("/device/gcm/")
    Call<Gcm> postGcm(@Header("Authorization") String apiKey, @Body Gcm gcm);

    @PATCH("/device/gcm/{registration_id}")
    Call<Gcm> updateGcm(@Header("Authorization") String apiKey,
                        @Path("registration_id") String registrationId,
                        @Body Gcm gcm);

    @GET("/device/gcm/{registration_id}")
    Call<Gcm> getGcm(@Header("Authorization") String apiKey, @Path("registration_id") String registrationId);

    @DELETE("/device/gcm/{registration_id}/")
    Call<JsonObject> deleteGcm(@Header("Authorization") String apiKey, @Path("registration_id") String registrationId);
}
