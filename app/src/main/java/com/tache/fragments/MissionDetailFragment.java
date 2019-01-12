package com.tache.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tache.R;

public class MissionDetailFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    //private SliderLayout mDemoSlider;
    private int[] images;

    // TODO: Rename and change types of parameters
    private String mParam1;

    public MissionDetailFragment() {
        // Required empty public constructor
    }

    public static MissionDetailFragment newInstance(String param1) {
        MissionDetailFragment fragment = new MissionDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mission_detail, container, false);
        //mDemoSlider = (SliderLayout) view.findViewById(R.id.slider);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
