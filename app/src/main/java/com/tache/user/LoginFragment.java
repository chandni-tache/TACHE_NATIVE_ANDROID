package com.tache.user;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.PatternsCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.tache.R;
import com.tache.activity.MainActivity;
import com.tache.animation.FlipAnimation;
import com.tache.receivers.ConnectivityReceiver;
import com.tache.rest.ApiUtils;
import com.tache.rest.models.request.SigninRequest;
import com.tache.rest.models.response.SigninResponse;
import com.tache.rest.services.UserService;
import com.tache.utils.Constants;
import com.tache.utils.Helper;
import com.tache.utils.SharedPrefsUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginFragment extends Fragment {
    private EditText emailView;
    private EditText passwordView;

    private View signinView;
    private View progressView;
    private LoginActivity.LoginSignUpSwitcher loginSignUpSwitcher;
    private Unbinder unbinder;
    private ImageView passwordViewVisibility;

    public static LoginFragment newInstance(@NonNull LoginActivity.LoginSignUpSwitcher loginSignUpSwitcher) {
        LoginFragment fragment = new LoginFragment();
        fragment.loginSignUpSwitcher = loginSignUpSwitcher;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, view);

        progressView = view.findViewById(R.id.frag_login_progress_bar);
        emailView = (EditText) view.findViewById(R.id.frag_login_email);
        passwordView = (EditText) view.findViewById(R.id.frag_login_password);
        View forgetPassword = view.findViewById(R.id.switch_forget_password);
        View signUp = view.findViewById(R.id.switch_signup);

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginSignUpSwitcher != null)
                    loginSignUpSwitcher.switchForgetPassword("Forgot password?");
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginSignUpSwitcher != null)
                    loginSignUpSwitcher.switchToSignUp();
            }
        });

        Typeface roboto = Typeface.createFromAsset(getContext().getAssets(), "font/Roboto-Regular.ttf");
        emailView.setTypeface(roboto);
        passwordView.setTypeface(roboto);

        passwordViewVisibility = (ImageView) view.findViewById(R.id.imagePassVisibility);
        passwordViewVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordView.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                    passwordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordView.setSelection(passwordView.getText().length());
                    passwordViewVisibility.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_visibility_off_gray_18dp));
                    //passwordView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_gray_18dp, 0, R.drawable.ic_visibility_off_gray_18dp, 0);
                } else {
                    passwordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordView.setSelection(passwordView.getText().length());
                    passwordViewVisibility.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_visibility_gray_18dp));
                    //passwordView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_gray_18dp, 0, R.drawable.ic_visibility_gray_18dp, 0);
                }
                passwordView.requestFocus();
            }
        });

        signinView = view.findViewById(R.id.frag_login_signin);
        signinView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ConnectivityReceiver.isConnected())
                    validateAndLogin();
                else
                    Toast.makeText(getContext(), R.string.check_internet, Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private void validateAndLogin() {
        if (TextUtils.isEmpty(emailView.getText()) && !PatternsCompat.EMAIL_ADDRESS.matcher(emailView.getText()).matches()) {
            emailView.setError(getString(R.string.error_email_required));
            return;
        }
        if (TextUtils.isEmpty(passwordView.getText())) {
            passwordView.setError(getString(R.string.error_password_required));
            return;
        }
        SigninRequest signinRequest = new SigninRequest(emailView.getText().toString(), passwordView.getText().toString());
        doSignin(signinRequest);
    }

    public void switchToSignUp() {
        if (loginSignUpSwitcher != null) loginSignUpSwitcher.switchToSignUp();
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return FlipAnimation.create(FlipAnimation.RIGHT, enter, 300);
    }

    private void doSignin(final SigninRequest signinRequest) {
        showProgress(true);
        final Retrofit retrofit = ApiUtils.retrofitInstance();
        UserService userService = retrofit.create(UserService.class);
        Call<SigninResponse> signinCall = userService.signin(signinRequest);

        Callback<SigninResponse> callback = new Callback<SigninResponse>() {
            @Override
            public void onResponse(Call<SigninResponse> call, Response<SigninResponse> response) {
                showProgress(false);
                if (response.isSuccessful()) {
                    Helper.saveSignInResponse(getContext(), Constants.LOGIN_TYPE_EMAIL, response.body());
                        SharedPrefsUtils.getInstance(getContext()).setBooleanPreference("login",true);
                    Intent intent = new Intent(getContext(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getContext().startActivity(intent);
                    getActivity().finish();
                   /* if (response.body().getUser().is_mobile_verified()) {
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        if (!response.body().getUser().getReferral_applied() && !SharedPrefsUtils.getInstance(getContext()).getBooleanPreference(SharedPrefsUtils.USER_REFERRAL_SKIP, false))
                            intent.putExtra("referral", true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getContext().startActivity(intent);
                        getActivity().finish();
                    } else {
                        if (loginSignUpSwitcher != null)
                            loginSignUpSwitcher.switchConfirmAccount("Verify account", !response.body().getUser().is_mobile_verified());
                    }*/

                } else {
                    Toast.makeText(getContext(), getString(R.string.error_on_sign_in), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SigninResponse> call, Throwable t) {
                showProgress(false);
                Toast.makeText(getContext(), getString(R.string.error_on_request_failed), Toast.LENGTH_LONG).show();
            }
        };
        signinCall.enqueue(callback);
    }


    /**
     * Shows the progress UI and hides the signup button.
     */
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        signinView.setEnabled(!show);

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}