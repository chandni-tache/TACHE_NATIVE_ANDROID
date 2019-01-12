package com.tache.events;

import android.support.v4.app.Fragment;

/**
 * Created by mayank on 27/10/16.
 */

public class CategoryOpenFragmentEvent {

    private Fragment fragment;
    private Type type;

    public enum Type {
        PRODUCT,
        CART
    }

    public CategoryOpenFragmentEvent(Fragment fragment, Type type){
        this.fragment = fragment;
        this.type = type;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public Type getType() {
        return type;
    }
}
