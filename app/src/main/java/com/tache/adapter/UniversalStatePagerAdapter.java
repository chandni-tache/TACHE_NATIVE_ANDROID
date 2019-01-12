package com.tache.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.tache.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayank on 10/7/16.
 */
public class UniversalStatePagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = UniversalStatePagerAdapter.class.getSimpleName();

    private final List<Fragment> fragmentsList = new ArrayList<>();
    private final List<String> fragmentsTitleList = new ArrayList<>();
    private int nonPresentTitleNum = 0;
    private boolean showTitle = true;
    private FragmentManager fragmentManager;

    public UniversalStatePagerAdapter(FragmentManager manager) {
        super(manager);
        this.fragmentManager = manager;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentsList.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        int index = fragmentsList.indexOf(object);
        if (index != -1) return index;
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

    public void removeFrag(int pos) {
        fragmentsList.remove(pos);
        fragmentsTitleList.remove(pos);
        notifyDataSetChanged();
    }

    public void replaceFrag(Fragment oldFrag, Fragment newFrag) {
        Log.d(TAG, "replace fragment");
        int oldFragIndex = fragmentsList.indexOf(oldFrag);
        if (oldFragIndex != -1) {
            Log.d(TAG, "replace fragment index");
            fragmentsList.set(oldFragIndex, newFrag);
            notifyDataSetChanged();
        } else {
            Log.d(TAG, "replace fragment not found");

        }
    }

    public void showTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return showTitle ? fragmentsTitleList.get(position) : null;
    }
}
