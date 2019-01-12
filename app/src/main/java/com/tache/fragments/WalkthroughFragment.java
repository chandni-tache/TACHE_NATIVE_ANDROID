package com.tache.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tache.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mayank on 12/10/16.
 */

public class WalkthroughFragment extends Fragment {

    @BindView(R.id.frag_walkthrough_img)
    ImageView image;
    @BindView(R.id.frag_walkthrough_txt)
    ImageView text;

    public static WalkthroughFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt("pos", position);
        WalkthroughFragment fragment = new WalkthroughFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_walkthrough, container, false);
        ButterKnife.bind(this, view);
        switch (getArguments().getInt("pos")) {
            case 1:
                Glide.with(getContext()).load(R.drawable.wt_1).into(image);
                Glide.with(getContext()).load(R.drawable.wt_1_1).into(text);
                break;
            case 2:
                Glide.with(getContext()).load(R.drawable.wt_2).into(image);
                Glide.with(getContext()).load(R.drawable.wt_2_2).into(text);
                break;
            case 3:
                Glide.with(getContext()).load(R.drawable.wt_3).into(image);
                Glide.with(getContext()).load(R.drawable.wt_3_3).into(text);
                break;
        }
        return view;
    }

}
