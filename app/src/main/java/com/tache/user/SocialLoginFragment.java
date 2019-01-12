package com.tache.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.JsonObject;
import com.tache.R;
import com.tache.receivers.ConnectivityReceiver;
import com.tache.rest.ApiUtils;
import com.tache.rest.models.request.ContactUsRequest;
import com.tache.rest.models.request.UpdateUser;
import com.tache.rest.models.response.SigninResponse;
import com.tache.rest.models.response.User;
import com.tache.rest.services.LinksService;
import com.tache.user.service.Login;
import com.tache.utils.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Fragment for handling social login.
 * Reference: https://developers.google.com/identity/sign-in/android/start-integrating,
 * Facebook Quickstart
 */
public class SocialLoginFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "SocialLoginFragment";
    private GoogleApiClient googleApiClient;
    private int RC_GOOGLE_SIGN_IN = 1;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private SocialLoginInteractionListener mListener;
    private ProgressDialog progressDialog;

    public SocialLoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_social_login, container, false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Signing in");
        progressDialog.setMessage("just a moment..");
        progressDialog.setCancelable(false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestServerAuthCode(getString(R.string.google_server_client_id))
                .build();
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Toast.makeText(getContext(), "Google Api Failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // facebook
        loginButton = (LoginButton) view.findViewById(R.id.frag_social_login_fb_login_button);
        loginButton.setReadPermissions("email");
        // If using in a fragment
        loginButton.setFragment(this);
        // Other app specific specialization

        // Callback registration
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                progressDialog.dismiss();
                                try {
                                    if (!object.has("email"))
                                        Toast.makeText(getContext(), "Email access denied!", Toast.LENGTH_LONG).show();
                                    else {
                                        Login login = new Login(getContext(), mListener);
                                        if (object.has("picture") && object.getJSONObject("picture").has("data") && object.getJSONObject("picture").getJSONObject("data").has("url"))
                                            login.setImage(object.getJSONObject("picture").getJSONObject("data").getString("url"));
                                        if (object.has("name"))
                                            login.setName(object.getString("name"));
                                        login.facebookLogin(loginResult);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Timber.e("FB_L" + e.getMessage());
                                    sendToContactUs("FB_L" + e.getMessage());
                                }
                            }
                        });
                // TODO: 4/13/2017 move this logic to server
                Bundle parameters = new Bundle();
                parameters.putString("fields", "name,email,picture.type(normal)");
                request.setParameters(parameters);
                request.executeAsync();
                progressDialog.show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getContext(), "Cancelled!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                Timber.e("FB_L" + exception.getMessage());
                sendToContactUs("FB_L" + exception.getMessage());
            }
        });

        // proxy buttons
        View proxyFacebookButton = view.findViewById(R.id.frag_social_login_proxy_btn_facebook);
        proxyFacebookButton.setOnClickListener(this);
        View proxyGoogleButton = view.findViewById(R.id.frag_social_login_proxy_btn_google);
        proxyGoogleButton.setOnClickListener(this);

        return view;
    }

    private void sendToContactUs(String s) {
        LinksService linksService = ApiUtils.retrofitInstance().create(LinksService.class);
        linksService.contactUs("Token f151b5083a4f34677218eafbbdb31ad056156c41", new ContactUsRequest(s, "error", "some@error.com")).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SocialLoginInteractionListener) {
            mListener = (SocialLoginInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement SocialLoginInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frag_social_login_proxy_btn_google:
                if (ConnectivityReceiver.isConnected())
                    googleSignIn();
                else
                    Toast.makeText(getContext(), R.string.check_internet, Toast.LENGTH_LONG).show();
                break;
            case R.id.frag_social_login_proxy_btn_facebook:
                if (ConnectivityReceiver.isConnected())
                    loginButton.performClick();
                else
                    Toast.makeText(getContext(), R.string.check_internet, Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            if (TextUtils.isEmpty(acct.getEmail()))
                Toast.makeText(getContext(), "Email access denied!", Toast.LENGTH_LONG).show();
            else {
                Login login = new Login(getContext(), mListener);
                login.setName(acct.getDisplayName());
                if (acct.getPhotoUrl() != null)
                    login.setImage(acct.getPhotoUrl().toString());
                login.googleLogin(acct);
            }
        } else {
            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
            Timber.e("G_L " + "Status: " + result.getStatus() + " String: " + result.toString());
            sendToContactUs("G_L " + "Status: " + result.getStatus() + " String: " + result.toString());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        googleApiClient.stopAutoManage(getActivity());
        googleApiClient.disconnect();
    }

    public interface SocialLoginInteractionListener {
        void handleResult(SigninResponse signinResponse, UpdateUser user);
    }

}
