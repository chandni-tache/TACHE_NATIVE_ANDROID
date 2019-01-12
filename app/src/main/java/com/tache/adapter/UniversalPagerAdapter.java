package com.tache.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by mayank on 17/2/17.
 */

/*https://stackoverflow.com/questions/13664155/dynamically-add-and-remove-view-to-viewpager*/

public class UniversalPagerAdapter extends PagerAdapter {

    private ArrayList<View> views = new ArrayList<>();
    private float pageWidth;
    private boolean isCustomWidthProvided;

    @Override
    public int getItemPosition (Object object) {
        int index = views.indexOf(object);
        if (index == -1) return POSITION_NONE;
        else return index;
    }

    @Override
    public Object instantiateItem (ViewGroup container, int position) {
        View v = views.get(position);
        container.addView (v);
        return v;
    }

    @Override
    public void destroyItem (ViewGroup container, int position, Object object) {
        container.removeView (views.get (position));
    }

    @Override
    public int getCount () {
        return views.size();
    }

    @Override
    public boolean isViewFromObject (View view, Object object) {
        return view == object;
    }

    public int addView (View v) {
        return addView (v, views.size());
    }

    public int addView (View v, int position) {
        views.add (position, v);
        return position;
    }

    public int removeView (ViewPager pager, View v) {
        return removeView (pager, views.indexOf (v));
    }

    public int removeView (ViewPager pager, int position)
    {
        // ViewPager doesn't have a delete method; the closest is to set the adapter
        // again.  When doing so, it deletes all its views.  Then we can delete the view
        // from from the adapter and finally set the adapter to the pager again.  Note
        // that we set the adapter to null before removing the view from "views" - that's
        // because while ViewPager deletes all its views, it will call destroyItem which
        // will in turn cause a null pointer ref.
        pager.setAdapter (null);
        views.remove (position);
        pager.setAdapter (this);

        return position;
    }

    public View getView (int position) {
        return views.get (position);
    }

    public void setPageWidth(float pageWidth){
        isCustomWidthProvided = true;
        this.pageWidth = pageWidth;
    }

    @Override
    public float getPageWidth(int position) {
        return isCustomWidthProvided ? pageWidth : super.getPageWidth(position);
    }



    // Other relevant methods:

    // finishUpdate - called by the ViewPager - we don't care about what pages the
    // pager is displaying so we don't use this method.
}
