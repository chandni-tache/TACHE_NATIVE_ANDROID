package com.tache.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tache.R;
import com.tache.activity.MainActivity;
import com.tache.adapter.UniversalStatePagerAdapter;
import com.tache.fragments.AuthenticationHelperFragment;
import com.tache.rest.ApiUtils;
import com.tache.rest.models.request.UpdateUser;
import com.tache.rest.models.response.SigninResponse;
import com.tache.rest.models.response.User;
import com.tache.rest.services.UserService;
import com.tache.utils.Constants;
import com.tache.utils.Helper;
import com.tache.utils.SharedPrefsUtils;
import com.tache.utils.ZoomOutPageTransformer;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity implements AuthenticationHelperFragment.AuthenticationHelperInteractionListener, SocialLoginFragment.SocialLoginInteractionListener {

    @BindView(R.id.activity_login_viewpager)
    ViewPager viewPagerLogin;
    @BindView(R.id.frame_verify)
    FrameLayout frame_verify;
    @BindView(R.id.activity_login_login_title)
    TextView loginTitle;
    @BindView(R.id.activity_login_signup_title)
    TextView signupTitle;

    private static final String TAG_FORGET_PASSWORD = "Forget Password";
    //private static final String TAG_VERIFY_EMAIL = "Verify Email";
    private static final String TAG_VERIFY_MOBILE = "Verify Mobile";

    private SharedPrefsUtils sharedPreference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        sharedPreference = SharedPrefsUtils.getInstance(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Signing in");
        progressDialog.setMessage("just a moment..");
        progressDialog.setCancelable(false);

        final UniversalStatePagerAdapter universalStatePagerAdapter = new UniversalStatePagerAdapter(getSupportFragmentManager());
        universalStatePagerAdapter.addFrag(LoginFragment.newInstance(loginSignUpSwitcher));
        universalStatePagerAdapter.addFrag(SignupFragment.newInstance(loginSignUpSwitcher));
        viewPagerLogin.setAdapter(universalStatePagerAdapter);
        setPagerAnimations();
        viewPagerLogin.setPageTransformer(false, new ZoomOutPageTransformer());

        loginTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPagerLogin.getCurrentItem() != 0)
                    viewPagerLogin.setCurrentItem(0);
            }
        });

        signupTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPagerLogin.getCurrentItem() != 1)
                    viewPagerLogin.setCurrentItem(1);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!loginTitle.getText().toString().contentEquals("Login"))
            switchLoginSignup();
        else
            super.onBackPressed();
    }

    private void setPagerAnimations() {
        viewPagerLogin.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset == 0) {
                    if (position == 0) {
                        signupTitle.setTranslationX(signupTitle.getWidth() / 2);
                        signupTitle.setAlpha(0.5f);
                        signupTitle.setScaleX(0.85f);
                        signupTitle.setScaleY(0.85f);
                    } else {
                        loginTitle.setTranslationX(-loginTitle.getWidth() / 2);
                        loginTitle.setAlpha(0.5f);
                        loginTitle.setScaleX(0.85f);
                        loginTitle.setScaleY(0.85f);
                    }
                    return;
                }

                float translation = loginTitle.getWidth() / 2 * positionOffset;
                loginTitle.setTranslationX(-translation);
                translation = signupTitle.getWidth() / 2 * (positionOffset - 1);
                signupTitle.setTranslationX(-translation);

                float alpha = positionOffset / 2;
                loginTitle.setAlpha(1 - alpha);
                signupTitle.setAlpha(0.5f + alpha);

                float scaleFactor = Math.max(0.85f, 1 - Math.abs(positionOffset));
                loginTitle.setScaleX(scaleFactor);
                loginTitle.setScaleY(scaleFactor);
                scaleFactor = Math.max(0.85f, Math.abs(positionOffset));
                signupTitle.setScaleX(scaleFactor);
                signupTitle.setScaleY(scaleFactor);
            }
        });
    }

    private LoginSignUpSwitcher loginSignUpSwitcher = new LoginSignUpSwitcher() {

        @Override
        public void switchToLogin() {
            viewPagerLogin.setCurrentItem(0);
        }

        @Override
        public void switchForgetPassword(String heading) {
            replaceFragment(AuthenticationHelperFragment.newInstance(TAG_FORGET_PASSWORD), TAG_FORGET_PASSWORD);

            if (viewPagerLogin.getCurrentItem() == 0)
                switchView(loginTitle, signupTitle, heading);
            else
                switchView(signupTitle, loginTitle, heading);
        }

        @Override
        public void switchConfirmAccount(String heading, boolean mobile) {
            if (mobile)
                startMobileVerification();

            if (viewPagerLogin.getCurrentItem() == 0)
                switchView(loginTitle, signupTitle, heading);
            else
                switchView(signupTitle, loginTitle, heading);
        }

        @Override
        public void switchToSignUp() {
            viewPagerLogin.setCurrentItem(1);
        }
    };

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame_verify, fragment, tag);
        fragmentTransaction.commit();
    }

    private void switchView(TextView tv1, TextView tv2, String s) {
        tv1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
        tv1.setText(s);
        tv2.setVisibility(View.GONE);
        viewPagerLogin.setVisibility(View.GONE);
        frame_verify.setVisibility(View.VISIBLE);
    }

    private void switchLoginSignup() {
        viewPagerLogin.setCurrentItem(0);
        loginTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
        loginTitle.setText("Login");
        signupTitle.setVisibility(View.VISIBLE);
        viewPagerLogin.setVisibility(View.VISIBLE);
        frame_verify.setVisibility(View.GONE);
    }

    @Override
    public void switchToLogin() {
        switchLoginSignup();
    }

    Callback<User> updateUserMobile = new Callback<User>() {
        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            if (response.isSuccessful()) {
                progressDialog.dismiss();
                Helper.saveUser(response.body(), LoginActivity.this, null);
                EventBus.getDefault().postSticky(response.body());
                exitToMainActivity(true);
            }
        }

        @Override
        public void onFailure(Call<User> call, Throwable t) {
            progressDialog.dismiss();
        }
    };

    private void exitToMainActivity(boolean referral) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("referral", referral);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void handleResult(SigninResponse signinResponse, UpdateUser updateUser) {
        if (TextUtils.isEmpty(signinResponse.getUser().getName()) || (TextUtils.isEmpty(signinResponse.getUser().getImage_url()) && !TextUtils.isEmpty(updateUser.getImage_url()))) {
            progressDialog.show();
            updateUser(updateUser).enqueue(updateNameAndImage);
        } else
            checkMobileVerification(signinResponse.getUser());
    }

    Callback<User> updateNameAndImage = new Callback<User>() {
        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            progressDialog.dismiss();
            if (response.isSuccessful()) {
                sharedPreference.setStringPreference(SharedPrefsUtils.USER_NAME, response.body().getName());
                sharedPreference.setStringPreference(SharedPrefsUtils.USER_IMAGE, response.body().getImage_url());
                checkMobileVerification(response.body());
            }
        }

        @Override
        public void onFailure(Call<User> call, Throwable t) {
            progressDialog.dismiss();
        }
    };

    private void checkMobileVerification(User user) {
        if (user.is_mobile_verified()) {
            exitToMainActivity(!user.getReferral_applied() && !sharedPreference.getBooleanPreference(SharedPrefsUtils.USER_REFERRAL_SKIP, false));
        } else {
            startMobileVerification();

            if (viewPagerLogin.getCurrentItem() == 0)
                switchView(loginTitle, signupTitle, "Verify account");
            else
                switchView(signupTitle, loginTitle, "Verify account");
        }
    }

    private void startMobileVerification() {
        DigitsAuthButton digitsButton = new DigitsAuthButton(this);
        digitsButton.setAuthTheme(R.style.AppTheme);
        Digits.clearActiveSession();
        digitsButton.setCallback(authCallback);
        AuthConfig.Builder authConfigBuilder = new AuthConfig.Builder()
                .withAuthCallBack(authCallback)
                .withPhoneNumber(sharedPreference.getStringPreference(SharedPrefsUtils.USER_MOBILE));
        Digits.authenticate(authConfigBuilder.build());
    }

    AuthCallback authCallback = new AuthCallback() {
        @Override
        public void success(DigitsSession session, String phoneNumber) {
            verifiedMobile(phoneNumber);
        }

        @Override
        public void failure(DigitsException exception) {
            switchToLogin();
        }
    };

    public void verifiedMobile(String mobile) {
        switchLoginSignup();
        progressDialog.show();

        UpdateUser updateUser = new UpdateUser();
        if (TextUtils.isEmpty(sharedPreference.getStringPreference(SharedPrefsUtils.USER_MOBILE)))
            updateUser.setMobile(mobile);
        updateUser.setIs_mobile_verified(true);
        updateUser(updateUser).enqueue(updateUserMobile);
    }

    private Call<User> updateUser(UpdateUser updateUser) {
        UserService userService = ApiUtils.retrofitInstance().create(UserService.class);
        return userService.updateUser(Helper.getAuthHeader(this), updateUser);
    }

    public interface LoginSignUpSwitcher {
        void switchToLogin();

        void switchForgetPassword(String heading);

        void switchConfirmAccount(String heading, boolean mobile);

        void switchToSignUp();
    }
}