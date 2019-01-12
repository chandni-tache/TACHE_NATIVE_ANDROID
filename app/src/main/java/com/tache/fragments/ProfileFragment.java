package com.tache.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.tache.R;
import com.tache.adapter.UniversalFragmentPagerAdapter;
import com.tache.events.EditProfileEvent;
import com.tache.receivers.ConnectivityReceiver;
import com.tache.rest.ApiUtils;
import com.tache.rest.models.profile.ProfileInfo;
import com.tache.rest.models.response.User;
import com.tache.rest.services.ProfileService;
import com.tache.rest.services.UserService;
import com.tache.utils.Helper;
import com.tache.utils.SharedPrefsUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {

    @BindView(R.id.frag_profile_user_image_circle)
    CircularImageView circleUserImage;
    @BindView(R.id.frag_profile_name)
    TextView name;
    @BindView(R.id.frag_profile_email)
    TextView email;
    @BindView(R.id.frag_profile_tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.frag_profile_view_pager)
    ViewPager viewPager;
    @BindView(R.id.frag_profile_mobile)
    TextView mobile;
    @BindView(R.id.frag_profile_progress_container)
    FrameLayout progressBarContainer;
    @BindView(R.id.frag_profile_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.frag_profile_error_view)
    TextView errorView;
    @BindView(R.id.frag_profile_set_profile_info)
    Button setProfileInfo;

    private ProfileService profileService;
    private Unbinder unbinder;
    private boolean isAlreadyConnected = false;
    private Call<ProfileInfo> baseListModelCall;
    private boolean hasLoadedOnce = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        BasicInfoFragment basicInfoFragment = new BasicInfoFragment();
        PersonalInfoFragment personalInfoFragment = new PersonalInfoFragment();
        ProfessionalInfoFragment professionalInfoFragment = new ProfessionalInfoFragment();
        UniversalFragmentPagerAdapter adapter = new UniversalFragmentPagerAdapter(getChildFragmentManager());
        adapter.addFrag(basicInfoFragment, "Basic Info");
        adapter.addFrag(personalInfoFragment, "Personal");
        adapter.addFrag(professionalInfoFragment, "Professional");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(adapter.getCount());

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = LayoutInflater.from(getContext()).inflate(R.layout.tab_custom_layout, null);
            TextView tabText = ((TextView) tab.findViewById(R.id.tabText));
            tabText.setText(adapter.getPageTitle(i));
            if (i == 0)
                tabText.setTextColor(Color.WHITE);
            tabLayout.getTabAt(i).setCustomView(tab);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView().findViewById(R.id.tabText)).setTextColor(Color.WHITE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView().findViewById(R.id.tabText)).setTextColor(ContextCompat.getColor(getActivity(), R.color.textTabColor));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        SharedPrefsUtils sharedPrefsUtils = SharedPrefsUtils.getInstance(getContext());
        name.setText(sharedPrefsUtils.getStringPreference(SharedPrefsUtils.USER_NAME));
        email.setText(sharedPrefsUtils.getStringPreference(SharedPrefsUtils.USER_EMAIL));
        mobile.setText(sharedPrefsUtils.getStringPreference(SharedPrefsUtils.USER_MOBILE));
        Glide.with(getContext()).load(sharedPrefsUtils.getStringPreference(SharedPrefsUtils.USER_IMAGE))
                .placeholder(ContextCompat.getDrawable(getContext(), R.drawable.image_placeholder))
                .dontAnimate()
                .into(circleUserImage);
        return view;
    }

    public void initializeNetworkCall() {
        initializeProfileCall();
        initializeUserCall();
    }

    public void initializeProfileCall() {
        profileService = ApiUtils.retrofitInstance().create(ProfileService.class);
        baseListModelCall = profileService.getProfileInfo(Helper.getAuthHeader(getContext()));
        if (ConnectivityReceiver.isConnected()) {
            initialize();
            isAlreadyConnected = true;
        }
    }

    public void initializeUserCall() {
        UserService userService = ApiUtils.retrofitInstance().create(UserService.class);
        userService.getUser(Helper.getAuthHeader(getContext())).enqueue(getUserCallback);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Timber.d("in visible");
        if (this.isVisible()) {
            Timber.d("visible");
            // we check that the fragment is becoming visible
            if (isVisibleToUser && !hasLoadedOnce) {
                //run your async task here since the user has just focused on your fragment
                initializeNetworkCall();
                hasLoadedOnce = true;
            }
        }
    }

    Callback<User> getUserCallback = new Callback<User>() {
        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            if (response.isSuccessful()) {
                Helper.saveUser(response.body(), getContext(), null);
                EventBus.getDefault().postSticky(response.body());
                if (!TextUtils.isEmpty(response.body().getImage_url())) {
                    Glide.with(getContext()).load(response.body().getImage_url()).dontAnimate().placeholder(ContextCompat.getDrawable(getContext(), R.drawable.image_placeholder)).into(circleUserImage);
                }
            }
        }

        @Override
        public void onFailure(Call<User> call, Throwable t) {

        }
    };


    Callback<ProfileInfo> baseListModelCallback = new Callback<ProfileInfo>() {
        @Override
        public void onResponse(Call<ProfileInfo> call, Response<ProfileInfo> response) {
            if (response.isSuccessful()) {
                Timber.d("response success");
                if (viewPager != null)
                    viewPager.setVisibility(View.VISIBLE);
                if (progressBarContainer != null)
                    progressBarContainer.setVisibility(View.GONE);
                ProfileInfo profileInfo = response.body();
                if (profileInfo != null) {
                    EventBus.getDefault().postSticky(profileInfo);
                }
            } else {
                Timber.d("response error");
                if (viewPager != null)
                    viewPager.setVisibility(View.GONE);
                if (progressBarContainer != null)
                    progressBarContainer.setVisibility(View.VISIBLE);
                if (errorView != null) {
                    errorView.setVisibility(View.VISIBLE);
                    errorView.setText("No profile information found");
                }
                if (setProfileInfo != null)
                    setProfileInfo.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(Call<ProfileInfo> call, Throwable t) {
            Timber.d("response failed");
            viewPager.setVisibility(View.GONE);
            progressBarContainer.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            errorView.setVisibility(View.VISIBLE);
            errorView.setText("Error getting profile information");
            setProfileInfo.setVisibility(View.GONE);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        ConnectivityReceiver.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        ConnectivityReceiver.unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isAlreadyConnected && isConnected) {
            initialize();
        }
    }

    @OnClick(R.id.frag_profile_set_profile_info)
    public void onEditProfile() {
        EventBus.getDefault().post(new EditProfileEvent(null, false, 0));
    }

    private void initialize() {
        Timber.d("init");
        baseListModelCall.enqueue(baseListModelCallback);
//        emptyView.setVisibility(View.GONE);
    }
}