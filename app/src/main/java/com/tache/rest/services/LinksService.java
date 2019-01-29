package com.tache.rest.services;

import com.google.gson.JsonObject;
import com.tache.rest.models.response.CategorySearch;
import com.tache.rest.models.BaseListModel;
import com.tache.rest.models.response.BankDetails;
import com.tache.rest.models.request.ContactUsRequest;
import com.tache.rest.models.response.Mission;
import com.tache.rest.models.response.MissionHistory;
import com.tache.rest.models.response.NotificationResponse;
import com.tache.rest.models.response.Setting;
import com.tache.rest.models.response.Surveys;
import com.tache.rest.models.response.SurveysHistory;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by a_man on 3/31/2017.
 */

public interface LinksService {
   // String TASK_URL = "http://52.66.195.101/links/app/start-survey/%s/%s/";
    String TASK_URL = "http://13.233.186.205/links/app/start-survey/%s/%s/";
//http://13.233.186.205/
    @GET("/links/missions/")
    Call<BaseListModel<Mission>> searchTask(@Header("Authorization") String token,
                                            @Query("q") String query,
                                            @Query("reward_min") String reward_min,
                                            @Query("reward_max") String reward_max);

    @GET("/links/surveys/")
    Call<BaseListModel<Surveys>> searchSurveys(@Header("Authorization") String token,
                                               @Query("q") String query,
                                               @Query("reward_min") String reward_min,
                                               @Query("reward_max") String reward_max);

    @GET("/links/notifications/")
    Call<BaseListModel<NotificationResponse>> getNotification(@Header("Authorization") String token);

    @GET()
    Call<BaseListModel<NotificationResponse>> getNotificationNext(@Header("Authorization") String token, @Url String nextUrl);

    @POST("/links/contact-us/")
    Call<JsonObject> contactUs(@Header("Authorization") String token, @Body ContactUsRequest contactUsRequest);

    @POST("/links/bank-details/create/")
    Call<BankDetails> createBankDetails(@Header("Authorization") String token, @Body BankDetails bankDetails);

    @PATCH("/links/bank-details/")
    Call<BankDetails> updateBankDetail(@Header("Authorization") String token, @Body BankDetails bankDetails);

    @GET("/links/bank-details/")
    Call<BankDetails> getBankDetails(@Header("Authorization") String token);

    @GET("/links/missions/")
    Call<BaseListModel<Mission>> missions(@Header("Authorization") String token);

    @GET("/links/missions/request/approved/")
    Call<BaseListModel<Mission>> missionsApproved(@Header("Authorization") String token);

    @GET("/links/mission/history/")
    Call<BaseListModel<MissionHistory>> missionsHistory(@Header("Authorization") String token);

    @GET()
    Call<BaseListModel<Mission>> missionsNext(@Header("Authorization") String token, @Url String nextUrl);

    @GET()
    Call<BaseListModel<MissionHistory>> missionsHistoryNext(@Header("Authorization") String token, @Url String nextUrl);

    @GET("/links/surveys/")
    Call<BaseListModel<Surveys>> surveys(@Header("Authorization") String token);

    @GET("/links/surveys/history/")
    Call<BaseListModel<SurveysHistory>> surveysHistory(@Header("Authorization") String token);

    @GET()
    Call<BaseListModel<Surveys>> surveysNext(@Header("Authorization") String token, @Url String nextUrl);

    @GET()
    Call<BaseListModel<SurveysHistory>> surveysHistoryNext(@Header("Authorization") String token, @Url String nextUrl);

    @GET("/links/categories/")
    Call<BaseListModel<CategorySearch>> categories(@Header("Authorization") String token);

    @GET()
    Call<BaseListModel<CategorySearch>> categories(@Header("Authorization") String token, @Url String nextUrl);

    @FormUrlEncoded
    @POST("/links/missions/request/")
    Call<JsonObject> missionApply(@Header("Authorization") String token, @Field("mission") int missionId);

    @FormUrlEncoded
    @POST("/links/refer/")
    Call<JsonObject> applyReferralCode(@Header("Authorization") String token, @Field("referral_code") String referralCode);

    @FormUrlEncoded
    @POST("/links/redeem/")
    Call<JsonObject> redeem(@Header("Authorization") String token, @Field("amount") float amount);

    @GET("/links/settings/")
    Call<BaseListModel<Setting>> getSettings(@Header("Authorization") String token);
}
