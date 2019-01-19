package com.tache.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tache.R;
import com.tache.adapter.UniversalStatePagerAdapter;
import com.tache.fragments.MissionDetailFragment;
import com.tache.fragments.WebViewFragment;
import com.tache.rest.ApiUtils;
import com.tache.rest.models.response.Mission;
import com.tache.rest.services.LinksService;
import com.tache.utils.Constants;
import com.tache.utils.Helper;
import com.tache.utils.TimeFormatHelper;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskDetailActivity extends AppCompatActivity implements WebViewFragment.OnWebViewComplete {
    private View detailContainer;
    private Button start;
    private Mission mission;
    private FrameLayout frameLayout;
    private CoordinatorLayout coordinatorLayout;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        mission = new Gson().fromJson(intent.getStringExtra("data"), Mission.class);
        final String fragmentName = intent.getStringExtra("name");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(mission.getCompany_name());

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        detailContainer = findViewById(R.id.detailContainer);
        ImageView imageView = (ImageView) findViewById(R.id.slider);
        TextView daysAllowed = (TextView) findViewById(R.id.daysAllowed);
        TextView timeAllowed = (TextView) findViewById(R.id.timeAllowed);
        TextView price = (TextView) findViewById(R.id.price);
        TextView date = (TextView) findViewById(R.id.dates);
        TextView address = (TextView) findViewById(R.id.address);
        TextView aboutText = (TextView) findViewById(R.id.aboutText);
        TextView ensureText = (TextView) findViewById(R.id.ensureText);
        TextView pointsText = (TextView) findViewById(R.id.pointsText);
        start = (Button) findViewById(R.id.start);
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        findViewById(R.id.viewInMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Mission> missions = new ArrayList<Mission>();
                missions.add(mission);
                startActivity(MapsActivity.getNewIntent(TaskDetailActivity.this, missions));
            }
        });

        Glide.with(this).load(mission.getCompany_logo()).into(imageView);
        System.out.println("Image == "+mission.getCompany_logo());
        System.out.println("Mission Id = "+mission.getId());
        System.out.println("Mission Survey ====  "+mission.getSurvey()+ " mission Location ==== "+mission.getLocation_lat()+" ttt "+mission.getLocation_long()+" mmm "+mission.getLocation());
        daysAllowed.setText(mission.getDay_allowed());
        date.setText(TimeFormatHelper.getInDM(mission.getDate_from()) + " - " + TimeFormatHelper.getInDMY(mission.getDate_to()));
        price.setText(String.valueOf(mission.getPrice()));
        address.setText(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? Html.fromHtml(mission.getLocation(), Html.FROM_HTML_MODE_COMPACT) : Html.fromHtml(mission.getLocation()).toString());
        aboutText.setText(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? Html.fromHtml(mission.getAbout(), Html.FROM_HTML_MODE_COMPACT) : Html.fromHtml(mission.getAbout()).toString());
        ensureText.setText(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? Html.fromHtml(mission.getEnsure_things(), Html.FROM_HTML_MODE_COMPACT) : Html.fromHtml(mission.getEnsure_things()).toString());
        pointsText.setText(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? Html.fromHtml(mission.getKey_points(), Html.FROM_HTML_MODE_COMPACT) : Html.fromHtml(mission.getKey_points()).toString());

        System.out.println("My Get Location ====  "+mission.getLocation());


        if (TextUtils.isEmpty(fragmentName))
            start.setVisibility(View.GONE);
        else {
            switch (fragmentName.toLowerCase()) {
                case "available":
                    start.setText("Apply now");
                    break;
                case "search":
                    start.setText("Apply now");
                    break;
                case "assigned":
                    start.setText("Start now");
                    break;
            }
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (start.getText().toString().toLowerCase()) {
                        case "apply now":
                            applyMission(mission.getId());
                            break;
                        case "start now":
//                            FragmentManager fm = getSupportFragmentManager();
//                            FragmentTransaction ft = fm.beginTransaction();
//                            Fragment fragment = WebViewFragment.newInstance(String.format(LinksService.TASK_URL, mission.getId(), Helper.getAuthKey(TaskDetailActivity.this)));
//                            ft.replace(frameLayout.getId(), fragment, TAG_MISSION);
//                            ft.commit();
                            //using Activity for now!

                            Intent start = new Intent(TaskDetailActivity.this, StartSurveyMissionActivity.class);
                            start.putExtra("url", String.format(LinksService.TASK_URL, mission.getId(), Helper.getAuthKey(TaskDetailActivity.this)));
                            start.putExtra("what", "Task");
                            startActivity(start);
                            break;
                    }
                }
            });
        }

    }

    private void switchToWebView() {
        detailContainer.setVisibility(View.GONE);
        start.setVisibility(View.GONE);
        frameLayout.setVisibility(View.VISIBLE);
    }

    private void switchToDetailView() {
        detailContainer.setVisibility(View.VISIBLE);
        start.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.GONE);
    }

    @Override
    public void closeWebView(Fragment fragment) {
        switchToDetailView();
        frameLayout.removeAllViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        if (frameLayout.getVisibility() == View.VISIBLE)
//            closeWebView(null);
//        else
        super.onBackPressed();
    }

    private void applyMission(int id) {
        final LinksService linksService = ApiUtils.retrofitInstance().create(LinksService.class);
        linksService.missionApply(Helper.getAuthHeader(this), id).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                System.out.println("My URL ====   "+linksService.toString().length());
                System.out.println("Hello user two");
                if (response.isSuccessful()) {
                    start.setVisibility(View.GONE);

                    snackbar = Snackbar.make(coordinatorLayout, "Applied for audit. You can start audit when approved", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (snackbar != null && snackbar.isShown())
                                snackbar.dismiss();
                        }
                    }).setActionTextColor(Color.YELLOW);
                    snackbar.show();

                    Constants.RELOAD_MISSION = true;
                } else
                    Toast.makeText(TaskDetailActivity.this, "Already applied", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

}
