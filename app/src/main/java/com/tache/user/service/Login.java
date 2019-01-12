package com.tache.user.service;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.tache.rest.ApiUtils;
import com.tache.rest.models.request.UpdateUser;
import com.tache.rest.models.response.SigninResponse;
import com.tache.rest.models.response.User;
import com.tache.rest.services.UserService;
import com.tache.user.SocialLoginFragment;
import com.tache.utils.Constants;
import com.tache.utils.Helper;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by ujjwal on 9/2/16.
 */

public class Login {
    private final SocialLoginFragment.SocialLoginInteractionListener socialLoginInteractionListener;
    private Context context;
    private Retrofit retrofit;
    private String name, image;

    public Login(Context context, SocialLoginFragment.SocialLoginInteractionListener socialLoginInteractionListener) {
        this.context = context;
        retrofit = ApiUtils.retrofitInstance();
        this.socialLoginInteractionListener = socialLoginInteractionListener;
    }

    private void socialLogin(Call<SigninResponse> call, final String loginType) {
        call.enqueue(new Callback<SigninResponse>() {
            @Override
            public void onResponse(Call<SigninResponse> call, Response<SigninResponse> response) {
                if (response.isSuccessful()) {
                    Helper.saveSignInResponse(context, loginType, response.body());

                    UpdateUser updateUser = new UpdateUser();
                    updateUser.setName(name);
                    updateUser.setImage_url(image);

                    socialLoginInteractionListener.handleResult(response.body(), updateUser);
                } else {
                    Toast.makeText(context, "Email is either not registered or is not verified!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SigninResponse> call, Throwable t) {
                Toast.makeText(context, "Failed!!!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void facebookLogin(LoginResult loginResult) {
        UserService userService = retrofit.create(UserService.class);
        System.out.println("    hhhhhhhhhhhhhhhhhhhhhhhh     ");
        socialLogin(userService.doFacebookLogin(loginResult.getAccessToken().getToken(), "facebook"), Constants.LOGIN_TYPE_FACEBOOK);
    }

    public void googleLogin(GoogleSignInAccount googleSignInAccount) {
        UserService userService = retrofit.create(UserService.class);
        System.out.println("   gggggggggggggggggggggggg     ");
        socialLogin(userService.doGoogleLogin(googleSignInAccount.getServerAuthCode()), Constants.LOGIN_TYPE_GOOGLE);
    }
}
