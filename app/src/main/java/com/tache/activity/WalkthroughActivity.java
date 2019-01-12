package com.tache.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tache.R;
import com.tache.adapter.UniversalStatePagerAdapter;
import com.tache.fragments.WalkthroughFragment;
import com.tache.user.LoginActivity;
import com.tache.utils.Helper;
import com.tache.utils.SharedPrefsUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mayank on 12/10/16.
 */

public class WalkthroughActivity extends AppCompatActivity {

    @BindView(R.id.activity_walkthrough_view_pager)
    ViewPager viewPager;

    @BindView(R.id.pagerIndicator1)
    ImageView indicator1;
    @BindView(R.id.pagerIndicator2)
    ImageView indicator2;
    @BindView(R.id.pagerIndicator3)
    ImageView indicator3;

    @BindView(R.id.prev)
    TextView prev;
    @BindView(R.id.next)
    TextView next;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_walkthrough);
        ButterKnife.bind(this);

        final UniversalStatePagerAdapter universalStatePagerAdapter = new UniversalStatePagerAdapter(getSupportFragmentManager());
        universalStatePagerAdapter.addFrag(WalkthroughFragment.newInstance(1), "");
        universalStatePagerAdapter.addFrag(WalkthroughFragment.newInstance(2), "");
        universalStatePagerAdapter.addFrag(WalkthroughFragment.newInstance(3), "");

        viewPager.setAdapter(universalStatePagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                indicator1.setBackground(ContextCompat.getDrawable(WalkthroughActivity.this, R.drawable.dot_unselected));
                indicator2.setBackground(ContextCompat.getDrawable(WalkthroughActivity.this, R.drawable.dot_unselected));
                indicator3.setBackground(ContextCompat.getDrawable(WalkthroughActivity.this, R.drawable.dot_unselected));
                switch (position) {
                    case 0:
                        next.setText("Next");
                        indicator1.setBackground(ContextCompat.getDrawable(WalkthroughActivity.this, R.drawable.dot_selected));
                        break;
                    case 1:
                        next.setText("Next");
                        indicator2.setBackground(ContextCompat.getDrawable(WalkthroughActivity.this, R.drawable.dot_selected));
                        break;
                    case 2:
                        next.setText("Get Started");
                        indicator3.setBackground(ContextCompat.getDrawable(WalkthroughActivity.this, R.drawable.dot_selected));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick(R.id.prev)
    public void onClickPrev() {
        new SharedPrefsUtils(this).setBooleanPreference(SharedPrefsUtils.SHOW_WALKTHROUGH, false);
        Class nextActivity = LoginActivity.class;
        if (Helper.isLoggedIn(this)) {
            nextActivity = MainActivity.class;
        }
        startActivity(new Intent(this, nextActivity));
        finish();
    }

    @OnClick(R.id.next)
    public void onClickNext() {
        if (viewPager.getCurrentItem() == 2)
            onClickPrev();
        else
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }
}
