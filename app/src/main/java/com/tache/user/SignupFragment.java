package com.tache.user;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.PatternsCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tache.R;
import com.tache.activity.MainActivity;
import com.tache.animation.FlipAnimation;
import com.tache.exceptions.UnhandledErrorResponseException;
import com.tache.receivers.ConnectivityReceiver;
import com.tache.rest.ApiUtils;
import com.tache.rest.models.errors.SignupError;
import com.tache.rest.models.request.SignupRequest;
import com.tache.rest.models.response.SigninResponse;
import com.tache.rest.services.UserService;
import com.tache.utils.Constants;
import com.tache.utils.Helper;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignupFragment extends Fragment {

    @BindView(R.id.frag_signup_password)
    EditText passwordView;

    @BindView(R.id.frag_signup_password_confirm)
    EditText passwordConfirmView;

    @BindView(R.id.frag_signup_name)
    EditText nameView;

    @BindView(R.id.frag_signup_mobile)
    EditText mobileView;

    @BindView(R.id.frag_signup_email)
    EditText emailView;

    @BindView(R.id.frag_signup_progress_bar)
    View progressView;

    @BindView(R.id.frag_signup_signup)
    View signUpView;

    @BindView(R.id.imagePasswordView)
    ImageView passwordPeek;
    @BindView(R.id.imagePasswordViewConfirm)
    ImageView passwordConfirmPeek;
    @BindView(R.id.checkbox)
    CheckBox termsCheck;

    private LoginActivity.LoginSignUpSwitcher loginSignUpSwitcher;
    private Unbinder unbinder;

    public static SignupFragment newInstance(@NonNull LoginActivity.LoginSignUpSwitcher loginSignUpSwitcher) {
        SignupFragment fragment = new SignupFragment();
        fragment.loginSignUpSwitcher = loginSignUpSwitcher;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        unbinder = ButterKnife.bind(this, view);

        Typeface roboto = Typeface.createFromAsset(getContext().getAssets(), "font/Roboto-Regular.ttf");
        emailView.setTypeface(roboto);
        passwordView.setTypeface(roboto);
        passwordConfirmView.setTypeface(roboto);
        mobileView.setTypeface(roboto);
        nameView.setTypeface(roboto);

        TextView terms = (TextView) view.findViewById(R.id.terms);
        SpannableString styledString = new SpannableString(terms.getText().toString());
        styledString.setSpan(new ForegroundColorSpan(Color.GREEN), 15, terms.getText().toString().length(), 0);

        passwordPeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordView.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                    passwordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordView.setSelection(passwordView.getText().length());
                    passwordPeek.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_visibility_off_gray_18dp));
                    //passwordView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_outline_white_24dp, 0, R.drawable.ic_visibility_off, 0);
                } else {
                    passwordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordView.setSelection(passwordView.getText().length());
                    passwordPeek.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_visibility_gray_18dp));
                    //passwordView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_outline_white_24dp, 0, R.drawable.ic_visibility, 0);
                }
            }
        });

        passwordConfirmPeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordConfirmView.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                    passwordConfirmView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordConfirmView.setSelection(passwordConfirmView.getText().length());
                    passwordConfirmPeek.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_visibility_off_gray_18dp));
                    //passwordView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_outline_white_24dp, 0, R.drawable.ic_visibility_off, 0);
                } else {
                    passwordConfirmView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordConfirmView.setSelection(passwordConfirmView.getText().length());
                    passwordConfirmPeek.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_visibility_gray_18dp));
                    //passwordView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_outline_white_24dp, 0, R.drawable.ic_visibility, 0);
                }
            }
        });

        signUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ConnectivityReceiver.isConnected())
                    validateAndSignup();
                else
                    Toast.makeText(getContext(), R.string.check_internet, Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private void validateAndSignup() {
        if (TextUtils.isEmpty(emailView.getText()) && !PatternsCompat.EMAIL_ADDRESS.matcher(emailView.getText()).matches()) {
            emailView.setError(getString(R.string.error_email_required));
            return;
        }
        if (TextUtils.isEmpty(nameView.getText())) {
            nameView.setError(getString(R.string.error_name_required));
            return;
        }
        if (nameView.getText().toString().trim().length() < 4) {
            nameView.setError(getString(R.string.error_name_short_required));
            return;
        }
        if (TextUtils.isEmpty(mobileView.getText()) && !Patterns.PHONE.matcher(mobileView.getText()).matches()) {
            mobileView.setError(getString(R.string.error_mobile_required));
            return;
        }
        if (TextUtils.isEmpty(passwordView.getText())) {
            passwordView.setError(getString(R.string.error_password_required));
            return;
        }
        if (TextUtils.isEmpty(passwordConfirmView.getText())) {
            passwordConfirmView.setError(getString(R.string.error_password_confirm_required));
            return;
        }
        if (!passwordView.getText().toString().equals(passwordConfirmView.getText().toString())) {
            passwordView.setError(getString(R.string.error_password_match));
            passwordConfirmView.setError(getString(R.string.error_password_match));
            return;
        }
        if (!termsCheck.isChecked()) {
            Toast.makeText(getContext(), "You must agree to the terms.", Toast.LENGTH_LONG).show();
            return;
        }

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail(emailView.getText().toString());
        signupRequest.setName(nameView.getText().toString());
        signupRequest.setMobile(mobileView.getText().toString());
        signupRequest.setPassword1(passwordView.getText().toString());
        signupRequest.setPassword2(passwordView.getText().toString());

        doSignup(signupRequest);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.scrollTo(0, 0);
        // Build.VERSION.SDK_INT <=27 &&
        if ( ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Grant permission for automatic OTP verification", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECEIVE_SMS}, 99);
        }
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return FlipAnimation.create(FlipAnimation.RIGHT, enter, 300);
    }


    private void doSignup(final SignupRequest signupRequest) {
        showProgress(false);
        final Retrofit retrofit = ApiUtils.retrofitInstance();
        UserService userService = retrofit.create(UserService.class);
        Call<SigninResponse> signinCall = userService.signup(signupRequest);

        Callback<SigninResponse> callback = new Callback<SigninResponse>() {
            @Override
            public void onResponse(Call<SigninResponse> call, Response<SigninResponse> response) {
                showProgress(false);
                if (response.isSuccessful()) {
                    Helper.saveSignInResponse(getContext(), Constants.LOGIN_TYPE_EMAIL, response.body());
                    Intent i = new Intent(getContext(), MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getContext().startActivity(i);
                    getActivity().finish();
                   /* if (loginSignUpSwitcher != null)
                        loginSignUpSwitcher.switchConfirmAccount("Verify account", !response.body().getUser().is_mobile_verified());*/


                } else {
                    try {
                        if (response.code() >= 500) {
                            throw new UnhandledErrorResponseException("Internal server error");
                        }
                        if(response.code()==400){
                            throw new UnhandledErrorResponseException("User Already Exist");
                        }
                        Converter<ResponseBody, SignupError> errorConverter = retrofit.responseBodyConverter(SignupError.class, new Annotation[0]);
                        try {
                            SignupError error = errorConverter.convert(response.errorBody());
                            if (error.getEmail().size() > 0) {
                                emailView.setError(error.getEmail().get(0));
                            } else if (error.getMobile().size() > 0) {
                                mobileView.setError(error.getMobile().get(0));
                            } else if (error.getName().size() > 0) {
                                nameView.setError(error.getName().get(0));
                            } else if (error.getNonFieldErrors().size() > 0) {
                                Toast.makeText(getContext(), error.getNonFieldErrors().get(0), Toast.LENGTH_LONG).show();
                            } else {
                                throw new UnhandledErrorResponseException("Unrecognized error");
                            }
                        } catch (IOException e) {
                            throw new UnhandledErrorResponseException("Unrecognized error");
                        }
                    } catch (UnhandledErrorResponseException ex) {
                        Toast.makeText(getContext(), getString(R.string.error_on_sign_up), Toast.LENGTH_LONG).show();
                        Toast.makeText(getActivity(), "User Already Exist", Toast.LENGTH_SHORT).show();
                    }
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

        signUpView.setVisibility(show ? View.GONE : View.VISIBLE);
        signUpView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                signUpView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

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

    //    private void attemptVerification() {
//
//        // Reset errors.
//        otpCodeView.setError(null);
//
//        // Store values at the time of the verification attempt.
//        String otp = otpCodeView.getText().toString();
//
//        String serverOtpCode = signinResponse.getUser().getOtpCode();
//
//        // Check for a valid name
//        if(TextUtils.isEmpty(otp) || otp.length() != 4) {
//            otpCodeView.setError("Code is not valid");
//        } else if (!TextUtils.isEmpty(serverOtpCode) && !serverOtpCode.equals(otpCodeView.getText().toString())){
//            otpCodeView.setError("Code does not match");
//        } else {
//            updateMobileVerifyStatus();
//        }
//    }
//
}
