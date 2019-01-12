package com.tache.services;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.tache.rest.ApiUtils;
import com.tache.rest.models.request.Gcm;
import com.tache.rest.services.DeviceService;
import com.tache.utils.Constants;
import com.tache.utils.Helper;
import com.tache.utils.SharedPrefsUtils;


import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by mayank on 2/9/16.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        checkAndRefresh(refreshedToken);
    }

    private void checkAndRefresh(String refreshedToken) {
        final SharedPrefsUtils sharedPrefsUtils = SharedPrefsUtils.getInstance(this);
        String saveToken = sharedPrefsUtils.getStringPreference(SharedPrefsUtils.REGISTERED_DEVICE_TOKEN);
        if (!TextUtils.isEmpty(saveToken) && !saveToken.equals(refreshedToken) && Helper.isLoggedIn(this)) {
            Gcm gcm = new Gcm(refreshedToken, true);
            DeviceService deviceService = ApiUtils.retrofitInstance().create(DeviceService.class);
            deviceService.updateGcm(Helper.getAuthHeader(this), saveToken, gcm).enqueue(new Callback<Gcm>() {
                @Override
                public void onResponse(Call<Gcm> call, Response<Gcm> response) {
                    if (response.isSuccessful()) {
                        sharedPrefsUtils.setStringPreference(SharedPrefsUtils.REGISTERED_DEVICE_TOKEN, response.body().getRegistration_id());
                    } else
                        Log.e("CHECK", "GCM update error");
                }

                @Override
                public void onFailure(Call<Gcm> call, Throwable t) {

                }
            });
        }
    }
}
