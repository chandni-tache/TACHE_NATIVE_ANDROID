package com.tache.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.tache.R;
import com.tache.activity.ContactUs;
import com.tache.activity.SendReferralActivity;
import com.tache.utils.Helper;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by mayank on 23/2/17.
 */

public class MoreFragment extends Fragment implements View.OnClickListener {

    private TextView[] textViews;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        textViews = new TextView[5];
        textViews[0] = (TextView) view.findViewById(R.id.setting);
        textViews[1] = (TextView) view.findViewById(R.id.contactUs);
        textViews[2] = (TextView) view.findViewById(R.id.rateUs);
        textViews[3] = (TextView) view.findViewById(R.id.refer);
        textViews[4] = (TextView) view.findViewById(R.id.logout);

        for (TextView tv : textViews)
            tv.setOnClickListener(this);

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            if (isVisibleToUser) {
                for (int i = 0; i < textViews.length; i++) {
                    Animation inAnimation = AnimationUtils.makeInAnimation(getContext(), false);
                    inAnimation.setDuration(500);
                    inAnimation.setStartOffset(i * 100);
                    textViews[i].startAnimation(inAnimation);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting:
                break;
            case R.id.contactUs:
                startActivity(new Intent(getContext(), ContactUs.class));
                break;
            case R.id.rateUs:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getContext().getPackageName())));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
                }
                break;
            case R.id.refer:
                startActivity(new Intent(getContext(), SendReferralActivity.class));
                break;
            case R.id.logout:
                dialogLogout();
                break;
        }
    }

    private void dialogLogout() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Logout");
        alertDialogBuilder.setMessage("Are you sure you want to logout?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Helper.onLogout(getContext(), true);
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

}
