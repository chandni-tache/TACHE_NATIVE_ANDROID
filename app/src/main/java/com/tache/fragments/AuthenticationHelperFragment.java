package com.tache.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.tache.R;
import com.tache.rest.ApiUtils;
import com.tache.rest.models.response.SigninResponse;
import com.tache.rest.services.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticationHelperFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String fragmentName;
    private AuthenticationHelperInteractionListener mListener;

    private TextView message, backToLogin;
    private EditText textToSubmit;
    private Button submit;


    public AuthenticationHelperFragment() {
        // Required empty public constructor
    }

    public static AuthenticationHelperFragment newInstance(String name) {
        AuthenticationHelperFragment fragment = new AuthenticationHelperFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fragmentName = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authentication_helper, container, false);
        message = (TextView) view.findViewById(R.id.message);
        backToLogin = (TextView) view.findViewById(R.id.backToLogin);
        textToSubmit = (EditText) view.findViewById(R.id.textToSubmit);
        submit = (Button) view.findViewById(R.id.submit);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switch (fragmentName) {
            case "Forget Password":
                message.setText(R.string.forget_password_message);
                textToSubmit.setHint("Email");
                textToSubmit.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                textToSubmit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email_24dp, 0, 0, 0);
                submit.setText("Send mail");
                break;
            case "Verify Email":
                message.setText(R.string.confirm_account_message);
                textToSubmit.setHint("Enter pin");
                textToSubmit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                textToSubmit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pin, 0, 0, 0);
                submit.setText("Verify");
                break;
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(textToSubmit.getText())) {
                    textToSubmit.setError("Field cannot be empty.");
                } else {
                    switch (fragmentName) {
                        case "Forget Password":
                            requestResetPasswordEmail(textToSubmit.getText().toString());
                            break;
                        case "Verify Email":
                            verifyEmail(textToSubmit.getText().toString());
                            break;
                        case "Verify Mobile":
                            break;
                    }
                }
            }
        });

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.switchToLogin();
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AuthenticationHelperInteractionListener) {
            mListener = (AuthenticationHelperInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement AuthenticationHelperInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void verifyEmail(String key) {
        UserService userService = ApiUtils.retrofitInstance().create(UserService.class);
        userService.verifyEmail(key).enqueue(new Callback<SigninResponse>() {
            @Override
            public void onResponse(Call<SigninResponse> call, Response<SigninResponse> response) {
                if (response.isSuccessful()) {
//                    Helper.saveSignInResponse(getContext(), Constants.LOGIN_TYPE_EMAIL, response.body());
                } else {
                    Toast.makeText(getContext(), "That is not valid", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SigninResponse> call, Throwable t) {

            }
        });
    }

    private void requestResetPasswordEmail(String email) {
        UserService userService = ApiUtils.retrofitInstance().create(UserService.class);
        userService.resetPassword(email).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful())
                    Toast.makeText(getContext(), "Password reset email requested", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();

                if (mListener != null)
                    mListener.switchToLogin();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    public interface AuthenticationHelperInteractionListener {
        void switchToLogin();
    }

}
