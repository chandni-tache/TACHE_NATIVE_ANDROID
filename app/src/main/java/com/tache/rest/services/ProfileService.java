package com.tache.rest.services;

import com.tache.rest.models.profile.ProfileInfo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by mayank on 30/3/17.
 */

public interface ProfileService {

    @GET("/links/profile/")
    Call<ProfileInfo> getProfileInfo(@Header("Authorization") String token);

    @POST("/links/profile/create/")
    Call<ProfileInfo> createProfile(@Header("Authorization") String token,
                                    @Body ProfileInfo profileInfo);

    @PATCH("/links/profile/")
    Call<ProfileInfo> updateProfile(@Header("Authorization") String token,
                                    @Body ProfileInfo profileInfo);
}
