package com.tache.activity;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tache.R;
import com.tache.fragments.SurveysTabFragment;
import com.tache.fragments.TasksTabFragment;

public class SearchActivity extends AppCompatActivity {
//    private RecyclerView categoryRecycler;
//    private ArrayList<CategorySearch> categories = new ArrayList<>();
//    private SearchRecyclerAdapter categoryAdapter;
//    private AppCompatSeekBar seekBar;
//    private TextView all, week1, week2, week3, week4;
//    private int colorDark, colorLight;
//    private LinksService linksService;
//    private String nextUrl;
//    private boolean isLoaderAdded = false;
//    private Call<BaseListModel<CategorySearch>> getCategories;
//
//    private boolean isFirstCall = true;
//    private boolean hasLoadedOnce = false;
//    private boolean isViewCreated = false;
//    private boolean isLoaded = false;

    private EditText queryEdit, rewardMinEdit, rewardMaxEdit;
    private String what; //audit or panel
    private String query = "", rewardMin = "", rewardMax = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.activity_search);
        //linksService = ApiUtils.retrofitInstance().create(LinksService.class);
        what = getIntent().getStringExtra("what");
        getSupportActionBar().setTitle("Search " + what);
        initUi();
//        setRecyclerView();
//        setSeekBar();
//        hasLoadedOnce = true;
//        isViewCreated = true;
        //fetchData();
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
        super.onBackPressed();
    }

//    private void setRecyclerView() {
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        categoryRecycler.setLayoutManager(linearLayoutManager);
//        categoryAdapter = new SearchRecyclerAdapter(this, categories);
//        categoryAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                super.onItemRangeInserted(positionStart, itemCount);
//                if (positionStart == 0) {
//                    categoryRecycler.smoothScrollToPosition(0);
//                }
//            }
//        });
//        categoryRecycler.setAdapter(categoryAdapter);
//
//        linksService = ApiUtils.retrofitInstance().create(LinksService.class);
//
//        getCategories = linksService.categories(Helper.getAuthHeader(this));
//
//        categoryRecycler.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount) {
//                initialize();
//            }
//        });
//    }
//
//    private void setSeekBar() {
//        colorDark = ContextCompat.getColor(this, R.color.colorPrimary);
//        colorLight = ContextCompat.getColor(this, R.color.textColorSecondary);
//
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                setWeeks(progress);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//    }
//
//    private void setWeeks(int progress) {
//        if (progress <= 100 && progress >= 80) {
//            all.setTextColor(colorDark);
//            week1.setTextColor(colorDark);
//            week2.setTextColor(colorDark);
//            week3.setTextColor(colorDark);
//            week4.setTextColor(colorDark);
//        } else if (progress <= 80 && progress > 60) {
//            all.setTextColor(colorDark);
//            week1.setTextColor(colorDark);
//            week2.setTextColor(colorDark);
//            week3.setTextColor(colorDark);
//            week4.setTextColor(colorLight);
//        } else if (progress <= 60 && progress > 40) {
//            all.setTextColor(colorDark);
//            week1.setTextColor(colorDark);
//            week2.setTextColor(colorDark);
//            week3.setTextColor(colorLight);
//            week4.setTextColor(colorLight);
//        } else if (progress <= 40 && progress > 20) {
//            all.setTextColor(colorDark);
//            week1.setTextColor(colorDark);
//            week2.setTextColor(colorLight);
//            week3.setTextColor(colorLight);
//            week4.setTextColor(colorLight);
//        } else if (progress <= 20 && progress >= 0) {
//            all.setTextColor(colorDark);
//            week1.setTextColor(colorLight);
//            week2.setTextColor(colorLight);
//            week3.setTextColor(colorLight);
//            week4.setTextColor(colorLight);
//        }
//    }

    private void initUi() {
        queryEdit = (EditText) findViewById(R.id.searchBar);
        queryEdit.setHint(getSupportActionBar().getTitle() + "..");
        rewardMinEdit = (EditText) findViewById(R.id.reward_min);
        rewardMaxEdit = (EditText) findViewById(R.id.reward_max);
        TextView apply = (TextView) findViewById(R.id.apply);
//        categoryRecycler = (RecyclerView) findViewById(R.id.categoryRecycler);
//        seekBar = (AppCompatSeekBar) findViewById(R.id.seekBar);
//        all = (TextView) findViewById(R.id.weekAll);
//        week1 = (TextView) findViewById(R.id.weekOne);
//        week2 = (TextView) findViewById(R.id.weekTwo);
//        week3 = (TextView) findViewById(R.id.weekThree);
//        week4 = (TextView) findViewById(R.id.weekFour);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(queryEdit.getText()))
                    Toast.makeText(SearchActivity.this, "Nothing to search", Toast.LENGTH_SHORT).show();
                else {
                    View view = getCurrentFocus();
                    if (view != null)
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);

                    if (!(query.equals(queryEdit.getText().toString()) && rewardMin.equals(rewardMinEdit.getText().toString()) && rewardMax.equals(rewardMaxEdit.getText().toString()))) {

                        query = queryEdit.getText().toString();
                        rewardMin = rewardMinEdit.getText().toString();
                        rewardMax = rewardMaxEdit.getText().toString();

                        if (what.equals("audit"))
                            replaceFragment(TasksTabFragment.newInstance("Search", query, rewardMin, rewardMax));
                        else
                            replaceFragment(SurveysTabFragment.newInstance("Search", query, rewardMin, rewardMax));
                    }
                }
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frameSearch, fragment);
        fragmentTransaction.commit();
    }

//    private void fetchData() {
//        if (hasLoadedOnce && isViewCreated && !isLoaded) {
//            initialize();
//            isLoaded = true;
//        }
//    }

//    private Callback<BaseListModel<CategorySearch>> baseListModelCallback = new Callback<BaseListModel<CategorySearch>>() {
//        @Override
//        public void onResponse(Call<BaseListModel<CategorySearch>> call, Response<BaseListModel<CategorySearch>> response) {
//            if (response.isSuccessful()) {
//                BaseListModel<CategorySearch> baseListModelList = response.body();
//                ArrayList<CategorySearch> newList = baseListModelList.getResults();
//                nextUrl = baseListModelList.getNext();
////                if (nextUrl == null) {
////                    GetPost blankPost = new GetPost();
////                    blankPost.setType(PostType.NO_MORE);
////                    newList.add(blankPost);
////                }
//                categoryAdapter.addItemsAtBottom(newList);
//            }
//            isLoaderAdded = false;
//        }
//
//        @Override
//        public void onFailure(Call<BaseListModel<CategorySearch>> call, Throwable t) {
//            Log.d("newPosts", "size: " + t.getCause());
//            isLoaderAdded = false;
//        }
//    };
//
//    private void initialize() {
//        if (isFirstCall) {
//            getCategories.enqueue(baseListModelCallback);
//            isFirstCall = false;
//            return;
//        }
//
//        if (nextUrl != null && !isLoaderAdded) {
//            isLoaderAdded = true;
//
//            Call<BaseListModel<CategorySearch>> call = linksService.categories(Helper.getAuthHeader(this), nextUrl);
//            call.enqueue(baseListModelCallback);
//        }
//    }
}
