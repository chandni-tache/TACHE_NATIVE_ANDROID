package com.tache.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tache.R;
import com.tache.activity.SearchActivity;
import com.tache.adapter.UniversalStatePagerAdapter;
import com.tache.events.NavigateToEvent;
import com.tache.utils.Constants;
import com.tache.utils.SharedPrefsUtils;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by mayank on 20/2/17.
 */

public class SurveyFragment extends Fragment {

    @BindView(R.id.frag_survey_tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.frag_survey_view_pager)
    ViewPager viewPager;

    private Unbinder unbinder;
    private UniversalStatePagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_survey, container, false);
        unbinder = ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        pagerAdapter = new UniversalStatePagerAdapter(getChildFragmentManager());
        pagerAdapter.addFrag(SurveysTabFragment.newInstance("Available"), "Available");
        pagerAdapter.addFrag(SurveysTabFragment.newInstance("History"), "History");

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = LayoutInflater.from(getContext()).inflate(R.layout.tab_custom_layout, null);
            TextView tabText = ((TextView) tab.findViewById(R.id.tabText));
            tabText.setText(pagerAdapter.getPageTitle(i));
            if (i == 0)
                tabText.setTextColor(Color.WHITE);
            tabLayout.getTabAt(i).setCustomView(tab);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view1 = tab.getCustomView();
                if (view1 != null)
                    ((TextView) view1.findViewById(R.id.tabText)).setTextColor(Color.WHITE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view1 = tab.getCustomView();
                if (view1 != null)
                    ((TextView) view1.findViewById(R.id.tabText)).setTextColor(ContextCompat.getColor(getActivity(), R.color.textTabColor));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_survey, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_map:
//                EventBus.getDefault().post(new OnMapOpenEvent(pagerAdapter.getPageTitle(viewPager.getCurrentItem()).toString()));
//                return true;
            case R.id.action_search:
                Intent intent = new Intent(getContext(),SearchActivity.class);
                intent.putExtra("what","panel");
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Constants.RELOAD_SURVEY) {
            Constants.RELOAD_SURVEY = false;
            for (int i = 0; i < pagerAdapter.getCount(); i++)
                ((SurveysTabFragment) pagerAdapter.getItem(i)).refresh();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void lastTab() {
        viewPager.setCurrentItem(pagerAdapter.getCount() - 1, false);
    }
}
