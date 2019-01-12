package com.tache.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tache.R;
import com.tache.events.EditProfileEvent;
import com.tache.rest.models.profile.ProfileInfo;
import com.tache.utils.TimeFormatHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by mayank on 19/2/17.
 */

public class PersonalInfoFragment extends Fragment {

    @BindView(R.id.frag_personal_info_gender)
    TextView gender;
    @BindView(R.id.frag_personal_info_dob)
    TextView dob;
    @BindView(R.id.frag_personal_info_pin_code)
    TextView pinCode;
    @BindView(R.id.frag_personal_info_area)
    TextView area;
    private Unbinder unbinder;

    private ProfileInfo profileInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_personal_info, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void setData(ProfileInfo profileInfo) {
        this.profileInfo = profileInfo;
        String genderString;
        if (TextUtils.isEmpty(profileInfo.getGender())) genderString = "";
        else genderString = profileInfo.getGender().equals("m") ? "Male" : "Female";
        gender.setText(genderString);
        dob.setText(TimeFormatHelper.getDateInAppropriateFormat(profileInfo.getDob()));
        pinCode.setText(profileInfo.getPincode());
        area.setText(profileInfo.getArea());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.frag_personal_info_edit_info)
    public void onEditProfile() {
        EventBus.getDefault().post(new EditProfileEvent(profileInfo, true, 0));
    }
}
