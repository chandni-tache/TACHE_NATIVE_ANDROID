package com.tache.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.tache.R;
import com.tache.adapter.NonSwipeableViewPager;
import com.tache.adapter.UniversalStatePagerAdapter;
import com.tache.events.EditProfileEvent;
import com.tache.events.NavigateToEvent;
import com.tache.events.NotificationEvent;
import com.tache.events.ProfileUpdatedEvent;
import com.tache.events.ViewPagerPositionChangeEvent;
import com.tache.fragments.EditProfileFragment;
import com.tache.fragments.MoreFragment;
import com.tache.fragments.NotificationsFragment;
import com.tache.fragments.ProfileFragment;
import com.tache.fragments.SurveyFragment;
import com.tache.fragments.TasksFragment;
import com.tache.fragments.WebViewFragment;
import com.tache.rest.ApiUtils;
import com.tache.rest.models.request.Gcm;
import com.tache.rest.models.response.User;
import com.tache.rest.services.DeviceService;
import com.tache.rest.services.LinksService;
import com.tache.utils.Constants;
import com.tache.utils.Helper;
import com.tache.utils.LocationHelper;
import com.tache.utils.SharedPrefsUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements WebViewFragment.OnWebViewComplete {
    private static final int MY_LOCATION_REQUEST_CODE = 1;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_main_view_pager)
    NonSwipeableViewPager viewPager;
    @BindView(R.id.bottomBar)
    BottomBar bottomBar;
    @BindView(R.id.city)
    TextView balance;

    private SharedPrefsUtils sharedPrefsUtils;
    private UniversalStatePagerAdapter adapter;
    private ProfileFragment profileFragment;
    private SurveyFragment surveyFragment;
    private EditProfileFragment editProfileFragment;
    private EditProfileFragment createProfileFragment;
    private boolean isEditingProfile;
    private LocationHelper locationHelper;
    private String title;
    private boolean exit, showLetsStart = true;

    private Toast exitToast;
    private Handler exitHandler = new Handler();
    private final Runnable exitRunnable = new Runnable() {
        @Override
        public void run() {
            exit = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sharedPrefsUtils = new SharedPrefsUtils(this);

        Intent intent = getIntent();
        if (intent.getBooleanExtra("referral", false))
            dialogClaimReferral();

        exitToast = Toast.makeText(this, "Tap back again to exit", Toast.LENGTH_SHORT);
        boolean hasProfile = sharedPrefsUtils.getBooleanPreference(SharedPrefsUtils.USER_HAS_PROFILE, false);

        setSupportActionBar(toolbar);
        title = "Feeds";
        toolbar.setTitle(title);

        surveyFragment = new SurveyFragment();
        profileFragment = new ProfileFragment();
        adapter = new UniversalStatePagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new TasksFragment(), "Tasks");
        if (hasProfile) {
            adapter.addFrag(surveyFragment, "Survey");
          //  adapter.addFrag(new TasksFragment(), "Tasks");
           // adapter.addFrag(profileFragment, "Profile");
        } else {
            createProfileFragment = EditProfileFragment.newInstance(null);
            adapter.addFrag(createProfileFragment, "Surveys");
        }
        adapter.addFrag(new NotificationsFragment(), "Notifications");
        adapter.addFrag(profileFragment, "Profile");
        adapter.addFrag(new MoreFragment(), "More");

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_home:
                        viewPager.setCurrentItem(0);
                        title = "Mystery audit";
                        setCityNameVisible(true);
                        break;

                    case R.id.tab_trending:
                        setCityNameVisible(createProfileFragment == null);
                        if (createProfileFragment != null && showLetsStart) {
                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (which == Dialog.BUTTON_NEGATIVE) goToPage(0);
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Let's start with Survey")
                                    .setMessage("To continue with panel section you will need to provide some of the required details in subject to provide you best related survey to be fill by you. So let's start\n")
                                    .setNegativeButton("BACK", onClickListener)
                                    .setPositiveButton("CONTINUE", onClickListener)
                                    .setCancelable(false)
                                    .show().getButton(Dialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(MainActivity.this, R.color.textColorSecondary));
                        }
                        viewPager.setCurrentItem(1);
                        title = "Online panel";
                        break;

                    case R.id.tab_add_new:
                        viewPager.setCurrentItem(2);
                        title = "Notifications";
                        setCityNameVisible(false);
                        break;

                    case R.id.tab_leaderboard:
                        /*setCityNameVisible(createProfileFragment == null);
                        if (createProfileFragment != null && showLetsStart) {
                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (which == Dialog.BUTTON_NEGATIVE) goToPage(0);
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Let's start with Survey")
                                    .setMessage("To continue with survey you will need to provide some of the required details in subject to provide you best related survey to be fill by you. So let's start\n")
                                    .setNegativeButton("BACK", onClickListener)
                                    .setPositiveButton("CONTINUE", onClickListener)
                                    .setCancelable(false)
                                    .show().getButton(Dialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(MainActivity.this, R.color.textColorSecondary));
                        }*/


                        viewPager.setCurrentItem(3);
                        title = "Profile";
                        setCityNameVisible(false);
                        break;

                    case R.id.tab_profile:
                        viewPager.setCurrentItem(4);
                        title = "More";
                        setCityNameVisible(false);
                        break;
                }
                toolbar.setTitle(title);
                ((TextView) findViewById(R.id.title)).setText(title);
            }
        });

        setBalance(String.valueOf(sharedPrefsUtils.getFloatPreference(SharedPrefsUtils.TOTAL_EARNINGS, 0)));
        locationHelper = new LocationHelper(this);
//        locationHelper.setOnLocationReceivedListener(new LocationHelper.OnLocationReceivedListener() {
//            @Override
//            public void onLocationReceivedSuccess(LatLng latLng) {
//                setBalance();
//            }
//
//            @Override
//            public void onLocationReceivedFailed() {
//                getSupportActionBar().setDisplayShowTitleEnabled(true);
//                findViewById(R.id.cityContainer).setVisibility(View.GONE);
//            }
//        });
        requestLatLong();
        registerFCMId(FirebaseInstanceId.getInstance().getToken());

        if (getIntent().getBooleanExtra("notification", false)) {
            if (viewPager.getCurrentItem() != 2)
                goToPage(2);
        }

    }

    private void registerFCMId(String token) {
        Gcm gcm = new Gcm(token, true);
        DeviceService deviceService = ApiUtils.retrofitInstance().create(DeviceService.class);
        deviceService.postGcm(Helper.getAuthHeader(this), gcm).enqueue(new Callback<Gcm>() {
            @Override
            public void onResponse(Call<Gcm> call, Response<Gcm> response) {
                if (response.isSuccessful())
                    sharedPrefsUtils.setStringPreference(SharedPrefsUtils.REGISTERED_DEVICE_TOKEN, response.body().getRegistration_id());


            }

            @Override
            public void onFailure(Call<Gcm> call, Throwable t) {

            }
        });

    }

    private void dialogClaimReferral() {
        final Dialog myDialog1 = new Dialog(this, R.style.DialogBox);
        myDialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        myDialog1.setCancelable(false);
        myDialog1.setContentView(R.layout.activity_refer);

        final EditText referralCode = (EditText) myDialog1.findViewById(R.id.referral_code);
        TextView referralMessage = (TextView) myDialog1.findViewById(R.id.referral_message);
        TextView submit = (TextView) myDialog1.findViewById(R.id.submit);
        TextView skip = (TextView) myDialog1.findViewById(R.id.skip);

        referralMessage.setText(String.format(getString(R.string.redeem_message), sharedPrefsUtils.getStringPreference(SharedPrefsUtils.REFERRAL_AMOUNT)));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(referralCode.getText())) {
                    LinksService linksService = ApiUtils.retrofitInstance().create(LinksService.class);
                    linksService.applyReferralCode(Helper.getAuthHeader(MainActivity.this), referralCode.getText().toString()).enqueue(referralCallback);
                    myDialog1.dismiss();
                } else
                    Toast.makeText(MainActivity.this, "Enter valid referral code.", Toast.LENGTH_SHORT).show();
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPrefsUtils.setBooleanPreference(SharedPrefsUtils.USER_REFERRAL_SKIP, true);
                myDialog1.dismiss();
            }
        });

        myDialog1.show();
    }

    Callback<JsonObject> referralCallback = new Callback<JsonObject>() {
        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

            if (response.isSuccessful()) {
                profileFragment.initializeUserCall();
                Toast.makeText(MainActivity.this, "Referral code applied.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {

        }
    };

    private void setBalance(String bal) {
        balance.setText("₹" + bal);
    }

    private void setCityNameVisible(boolean visible) {
        getSupportActionBar().setDisplayShowTitleEnabled(!visible);
        findViewById(R.id.cityContainer).setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void requestLatLong() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initLocationHelper();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_LOCATION_REQUEST_CODE);
        }
    }

    private void initLocationHelper() {
        locationHelper.build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_LOCATION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            initLocationHelper();
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    && !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                Toast.makeText(this, "Location permissions are needed in order to function properly", Toast.LENGTH_SHORT).show();
                requestLatLong();
            }
        }
    }

    public void goToPage(int position) {
        if (position >= 0 && position < viewPager.getAdapter().getCount()) {
            viewPager.setCurrentItem(position, true);
            bottomBar.selectTabAtPosition(position);
            showLetsStart = true;
        }
    }


    @Override
    public void onBackPressed() {
        if (adapter.getItem(1).getClass().equals(EditProfileFragment.class)) {
            adapter.replaceFrag(editProfileFragment, surveyFragment);
            profileFragment.initializeNetworkCall();
        } else if (viewPager.getCurrentItem() != 1) {
            goToPage(1);
        } else {
            if (exit) {
                exitToast.cancel();
                super.onBackPressed();
            }

            exit = true;
            exitToast.show();

            exitHandler.postDelayed(exitRunnable, 2000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exitHandler != null)
            exitHandler.removeCallbacks(exitRunnable);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        sharedPrefsUtils.setBooleanPreference(SharedPrefsUtils.IN_APP, true);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        sharedPrefsUtils.setBooleanPreference(SharedPrefsUtils.IN_APP, false);
    }

    @Subscribe
    public void onViewPagerPositionChangeEvent(ViewPagerPositionChangeEvent viewPagerPositionChangeEvent) {
        int position = viewPagerPositionChangeEvent.getPosition();
        if (position < bottomBar.getTabCount()) {
            bottomBar.selectTabAtPosition(position);
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateBalance(User user) {
        setBalance(String.valueOf(user.getTotal_earnings()));
    }

    @Subscribe
    public void onEditProfileEvent(EditProfileEvent editProfileEvent) {
        if (editProfileEvent == null) return;
        isEditingProfile = true;
        if (editProfileEvent.isTypeEdit()) {
            editProfileFragment = EditProfileFragment.newInstance(editProfileEvent.getProfileInfo());
            editProfileFragment.moveTo(editProfileEvent.getTo());
            adapter.replaceFrag(profileFragment, editProfileFragment);
        } else {
            int index = adapter.getItemPosition(surveyFragment);
            if (index >= 0) {
                createProfileFragment = EditProfileFragment.newInstance(null);
                adapter.replaceFrag(surveyFragment, createProfileFragment);
            } else {
                index = adapter.getItemPosition(createProfileFragment);
            }
            showLetsStart = false;
            goToPage(index);
            Timber.d("called");
        }
    }

    @Subscribe
    public void onNavigateTo(NavigateToEvent navigateToEvent) {
        switch (navigateToEvent.getNavigateTo()) {
            case "MissionHistory":
                goToPage(0);
                ((TasksFragment) adapter.getItem(0)).lastTab();
                break;
            case "SurveyHistory":
                goToPage(1);
                ((SurveyFragment) adapter.getItem(1)).lastTab();
                break;
        }
    }

    @Subscribe
    public void onWebViewFragment(WebViewFragment webViewFragment) {
        adapter.replaceFrag(surveyFragment, webViewFragment);
    }

    @Subscribe
    public void onNotificationEvent(NotificationEvent notificationEvent) {
        if (viewPager.getCurrentItem() != 2)
            goToPage(2);
    }

    @Override
    public void closeWebView(Fragment fragment) {
        adapter.replaceFrag(fragment, surveyFragment);
    }

    @Subscribe
    public void onProfileUpdatedEvent(ProfileUpdatedEvent profileUpdatedEvent) {
        isEditingProfile = false;
        adapter.replaceFrag(editProfileFragment, profileFragment);
        if (profileUpdatedEvent.isUpdated())
            profileFragment.initializeProfileCall();
        adapter.replaceFrag(createProfileFragment, surveyFragment);
        // TODO: 31/3/17 perform network call manually
        editProfileFragment = null;
        createProfileFragment = null;
    }
}
