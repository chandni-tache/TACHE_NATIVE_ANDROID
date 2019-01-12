package com.tache.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayank on 19/2/17.
 */

public class UniversalFragmentPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragmentsList = new ArrayList<>();
    private final List<String> fragmentsTitleList = new ArrayList<>();
    private int nonPresentTitleNum = 0;
    private boolean showTitle = true;

    public UniversalFragmentPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentsList.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return fragmentsList.size();
    }

    public void addFrag(@NonNull Fragment fragment) {
        addFrag(fragment, String.valueOf(nonPresentTitleNum++));
    }

    public void addFrag(@NonNull Fragment fragment, @NonNull String title) {
        fragmentsList.add(fragment);
        fragmentsTitleList.add(title);
    }

    public void showTitle(boolean showTitle){
        this.showTitle = showTitle;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return showTitle ? fragmentsTitleList.get(position) : null;
    }
}
