package com.tache.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.tache.R;
import com.tache.user.LoginActivity;
import com.tache.utils.Helper;
import com.tache.utils.SharedPrefsUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Full screen splash with animation.
 * Ref: https://guides.codepath.com/android/Animations
 */
public class SplashActivity extends AppCompatActivity {

//    private static final String TAG = SplashActivity.class.getSimpleName();
//
//    private View logoView, textView;
//    private static final int ANIMATION_DURATION = 1400;
//    private static final int ANIMATION_DELAY = 200;
//    private static final int POST_ANIMATION_DELAY = 800;
//    String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent;
                if (new SharedPrefsUtils(SplashActivity.this).getBooleanPreference(SharedPrefsUtils.SHOW_WALKTHROUGH, true)) {
                    intent = new Intent(SplashActivity.this, WalkthroughActivity.class);
                } else if (Helper.isLoggedIn(SplashActivity.this)) {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                    //intent.putExtra("postid", postId);
                } else {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
