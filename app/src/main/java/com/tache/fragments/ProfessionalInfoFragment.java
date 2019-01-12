package com.tache.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tache.R;
import com.tache.events.EditProfileEvent;
import com.tache.rest.models.profile.ProfileInfo;

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

public class ProfessionalInfoFragment extends Fragment {

    @BindView(R.id.frag_professional_qualification)
    TextView qualification;
    @BindView(R.id.frag_professional_employment)
    TextView employment;
    @BindView(R.id.frag_professional_designation)
    TextView designation;
    @BindView(R.id.frag_professional_work_experience)
    TextView workExperience;
    @BindView(R.id.frag_professional_annual_income)
    TextView annualIncome;
    @BindView(R.id.frag_professional_industry)
    TextView industry;
    private Unbinder unbinder;

    private ProfileInfo profileInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_professional_info, container, false);
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
        qualification.setText(profileInfo.getQualification());
        employment.setText(profileInfo.getEmployment());
        designation.setText(profileInfo.getDesignation().equalsIgnoreCase("Other") ? profileInfo.getDesignation_other() : profileInfo.getDesignation());
        workExperience.setText(profileInfo.getWorkExperience());
        annualIncome.setText(profileInfo.getAnnualIncome());
        industry.setText(profileInfo.getIndustry());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.frag_professional_info_edit_info)
    public void onEditProfile() {
        EventBus.getDefault().post(new EditProfileEvent(profileInfo, true, 1));
    }
}
