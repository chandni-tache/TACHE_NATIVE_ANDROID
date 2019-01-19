package com.tache.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.gson.JsonObject;
import com.tache.activity.MainActivity;
import com.tache.rest.ApiUtils;
import com.tache.rest.models.BaseListModel;
import com.tache.rest.models.response.Setting;
import com.tache.rest.models.response.SigninResponse;
import com.tache.rest.models.response.User;
import com.tache.rest.services.DeviceService;
import com.tache.rest.services.LinksService;
import com.tache.user.LoginActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ujjwal on 8/13/16.
 */

public class Helper {
    public static void saveSignInResponse(Context context, String loginType, SigninResponse signinResponse) {
        SharedPrefsUtils sharedPrefsUtils = SharedPrefsUtils.getInstance(context);
        if (signinResponse != null) {
            sharedPrefsUtils.setStringPreference(SharedPrefsUtils.USER_AUTH_KEY, signinResponse.getKey());
            sharedPrefsUtils.setStringPreference(SharedPrefsUtils.USER_LOGIN_TYPE, loginType);
            saveUser(signinResponse.getUser(), context, sharedPrefsUtils);
            if (!sharedPrefsUtils.getBooleanPreference(SharedPrefsUtils.HAVE_SETTINGS, false))
                getSettings(buildAuthHeader(sharedPrefsUtils.getStringPreference(SharedPrefsUtils.USER_AUTH_KEY)), sharedPrefsUtils);
        }
    }

    public static void saveUser(User body, Context context, SharedPrefsUtils spu) {
        SharedPrefsUtils sharedPrefsUtils = spu;
        if (sharedPrefsUtils == null)
            sharedPrefsUtils = SharedPrefsUtils.getInstance(context);
        sharedPrefsUtils.setIntegerPreference(SharedPrefsUtils.USER_ID, body.getPk());
        sharedPrefsUtils.setFloatPreference(SharedPrefsUtils.TOTAL_EARNINGS, body.getTotal_earnings());
        sharedPrefsUtils.setIntegerPreference(SharedPrefsUtils.TOTAL_MISSIONS_COMPLETED, body.getTotal_missions_completed());
        sharedPrefsUtils.setIntegerPreference(SharedPrefsUtils.TOTAL_SURVEY_COMPLETED, body.getTotal_survey_completed());
        sharedPrefsUtils.setStringPreference(SharedPrefsUtils.USER_MOBILE, body.getMobile());
        sharedPrefsUtils.setStringPreference(SharedPrefsUtils.USER_EMAIL, body.getEmail());
        sharedPrefsUtils.setStringPreference(SharedPrefsUtils.USER_NAME, body.getName());
        sharedPrefsUtils.setStringPreference(SharedPrefsUtils.USER_IMAGE, body.getImage_url());
        sharedPrefsUtils.setBooleanPreference(SharedPrefsUtils.USER_HAS_PROFILE, body.getHas_profile());
        sharedPrefsUtils.setBooleanPreference(SharedPrefsUtils.USER_REFERRAL_APPLIED, body.getReferral_applied());
        sharedPrefsUtils.setBooleanPreference(SharedPrefsUtils.USER_EMAIL_VERIFIED, body.is_email_verified());
        sharedPrefsUtils.setBooleanPreference(SharedPrefsUtils.USER_MOBILE_VERIFIED, body.is_mobile_verified());
        sharedPrefsUtils.setBooleanPreference(SharedPrefsUtils.USER_HAS_BANK, body.isHas_bank_details());
        sharedPrefsUtils.setStringPreference(SharedPrefsUtils.REFERRAL_CODE, body.getReferral_code());
    }

    public static void onLogout(Context context, boolean clearPrefs) {
        if (clearPrefs) {
            final SharedPrefsUtils sharedPrefsUtils = SharedPrefsUtils.getInstance(context);
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut();
            }

            String saveToken = sharedPrefsUtils.getStringPreference(SharedPrefsUtils.REGISTERED_DEVICE_TOKEN);
            if (!TextUtils.isEmpty(saveToken)) {
                DeviceService deviceService = ApiUtils.retrofitInstance().create(DeviceService.class);
                deviceService.deleteGcm(getAuthHeader(context), saveToken).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            sharedPrefsUtils.removePreference(SharedPrefsUtils.REGISTERED_DEVICE_TOKEN);
                        } else
                            Log.e("CHECK", "GCM delete error");
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });
            }

            sharedPrefsUtils.removePreference(SharedPrefsUtils.USER_AUTH_KEY);
            sharedPrefsUtils.removePreference(SharedPrefsUtils.USER_ID);
            sharedPrefsUtils.removePreference(SharedPrefsUtils.TOTAL_SURVEY_COMPLETED);
            sharedPrefsUtils.removePreference(SharedPrefsUtils.TOTAL_MISSIONS_COMPLETED);
            sharedPrefsUtils.removePreference(SharedPrefsUtils.TOTAL_EARNINGS);
            sharedPrefsUtils.removePreference(SharedPrefsUtils.USER_MOBILE);
            sharedPrefsUtils.removePreference(SharedPrefsUtils.USER_EMAIL);
            sharedPrefsUtils.removePreference(SharedPrefsUtils.USER_NAME);
            sharedPrefsUtils.removePreference(SharedPrefsUtils.USER_IMAGE);
            sharedPrefsUtils.removePreference(SharedPrefsUtils.USER_REFERRAL_APPLIED);
            sharedPrefsUtils.removePreference(SharedPrefsUtils.USER_MOBILE_VERIFIED);
            sharedPrefsUtils.removePreference(SharedPrefsUtils.USER_EMAIL_VERIFIED);
            sharedPrefsUtils.removePreference(SharedPrefsUtils.USER_HAS_BANK);
            sharedPrefsUtils.removePreference(SharedPrefsUtils.USER_HAS_PROFILE);
            sharedPrefsUtils.removePreference(SharedPrefsUtils.REFERRAL_CODE);
        }

        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        ActivityCompat.finishAffinity((Activity) context);
//        ((Activity) context).finish();
    }

    public static boolean isLoggedIn(Context context) {
        SharedPrefsUtils sharedPrefsUtils = new SharedPrefsUtils(context);
        String authKey = sharedPrefsUtils.getStringPreference(SharedPrefsUtils.USER_AUTH_KEY);
//        boolean mobile = sharedPrefsUtils.getBooleanPreference(SharedPrefsUtils.USER_MOBILE_VERIFIED, false);
        boolean islogin = sharedPrefsUtils.getBooleanPreference("login",false);
        return authKey != null && islogin;
    }

    private static void getSettings(String token, final SharedPrefsUtils sharedPrefsUtils) {
        LinksService linksService = ApiUtils.retrofitInstance().create(LinksService.class);
        linksService.getSettings(token).enqueue(new Callback<BaseListModel<Setting>>() {
            @Override
            public void onResponse(Call<BaseListModel<Setting>> call, Response<BaseListModel<Setting>> response) {
                if (response.isSuccessful()) {
                    sharedPrefsUtils.setBooleanPreference(SharedPrefsUtils.HAVE_SETTINGS, true);
                    for (Setting s : response.body().getResults()) {
                        if (s.getKey().equals(SharedPrefsUtils.REFERRAL_AMOUNT)) {
                            sharedPrefsUtils.setStringPreference(SharedPrefsUtils.REFERRAL_AMOUNT, s.getValue());
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseListModel<Setting>> call, Throwable t) {

            }
        });
    }

    public static String getAuthHeader(Context context) {
        SharedPrefsUtils sharedPrefsUtils = SharedPrefsUtils.getInstance(context);
        String key = sharedPrefsUtils.getStringPreference(SharedPrefsUtils.USER_AUTH_KEY);
        return buildAuthHeader(key);
    }

    public static String getAuthKey(Context context) {
        SharedPrefsUtils sharedPrefsUtils = SharedPrefsUtils.getInstance(context);
        String key = sharedPrefsUtils.getStringPreference(SharedPrefsUtils.USER_AUTH_KEY);
        return key;
    }

    public static String buildAuthHeader(String key) {
        return "Token " + key;
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static int pxToDp(Context context, int px) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (px * density);
    }
}
