package com.tache.rest.services;

import com.google.gson.JsonObject;
import com.tache.rest.models.BaseListModel;
import com.tache.rest.models.request.Contact;
import com.tache.rest.models.request.SigninRequest;
import com.tache.rest.models.request.SignupRequest;
import com.tache.rest.models.request.UpdateUser;
import com.tache.rest.models.request.UserUpdateRequest;
import com.tache.rest.models.response.SigninResponse;
import com.tache.rest.models.response.User;
import com.tache.rest.models.users.GetUser;
import com.tache.rest.models.users.UserStats;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by ujjwal on 8/13/16.
 */

public interface UserService {
    @POST("/rest-auth/registration/")
    Call<SigninResponse> signup(@Body SignupRequest signupRequest);

    @FormUrlEncoded
    @POST("/rest-auth/password/reset/")
    Call<JsonObject> resetPassword(@Field("email") String email);

    @POST("/rest-auth/registration/verify-email/")
    @FormUrlEncoded
    Call<SigninResponse> verifyEmail(@Field("key") String key);

    @POST("/rest-auth/login/")
    Call<SigninResponse> signin(@Body SigninRequest signinRequest);

    @GET("/rest-auth/user/")
    Call<User> getUser(@Header("Authorization") String token);

    @PATCH("/rest-auth/user/")
    Call<User> updateUser(@Header("Authorization") String token, @Body UpdateUser updateUserRequest);

    @FormUrlEncoded
    @POST("/users/rest-auth/facebook/")
    Call<SigninResponse> doFacebookLogin(@Field("access_token") String accessToken, @Field("code") String code);

    @FormUrlEncoded
    @POST("/users/rest-auth/google/")
    Call<SigninResponse> doGoogleLogin(@Field("code") String code);

    @PUT("/user/verifymobile/")
    Call<SigninResponse> verifyMobile(@Header("Authorization") String token, @Body UserUpdateRequest userUpdateRequest);

    @PUT("/user/updatemobile/")
    Call<User> updateMobile(@Header("Authorization") String token, @Body UserUpdateRequest userUpdateRequest);

    @GET("/user/resendotp/")
    Call<User> resendOtp(@Header("Authorization") String token);

    @GET("/user/stats/")
    Call<UserStats> getUserStats(@Header("Authorization") String token);

    @FormUrlEncoded
    @PUT("/user/")
    Call<GetUser> updateUserDetails(@Header("Authorization") String token,
                                    @Field("name") String name,
                                    @Field("profile_image") String profileImage,
                                    @Field("mobile") String mobile,
                                    @Field("is_mobile_verified") Boolean isMobileVerified);

    @GET("/user/{pk}")
    Call<GetUser> getUserDetails(@Header("Authorization") String token,
                                 @Path("pk") int pk);

    @GET("/user/follow/{to_follow}")
    Call<JSONObject> followUser(@Header("Authorization") String token,
                                @Path("to_follow") int toFollow);

    @GET("/user/{pk}/followers")
    Call<BaseListModel<GetUser>> getFollowers(@Header("Authorization") String token,
                                              @Path("pk") int pk);

    @GET("/user/{pk}/following")
    Call<BaseListModel<GetUser>> getFollowings(@Header("Authorization") String token,
                                               @Path("pk") int pk);

    @GET
    Call<BaseListModel<GetUser>> getFollowers(@Header("Authorization") String apiKey,
                                              @Url String url);

    @POST("/users/contacts/")
    Call<JSONObject> postContacts(@Header("Authorization") String apiKey,
                                  @Body List<Contact> contactsList);
}
