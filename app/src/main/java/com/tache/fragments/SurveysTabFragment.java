package com.tache.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tache.R;
import com.tache.adapter.SurveysHistoryRecyclerAdapter;
import com.tache.adapter.SurveysRecyclerAdapter;
import com.tache.receivers.ConnectivityReceiver;
import com.tache.rest.ApiUtils;
import com.tache.rest.models.BaseListModel;
import com.tache.rest.models.response.Surveys;
import com.tache.rest.models.response.SurveysHistory;
import com.tache.rest.services.LinksService;
import com.tache.utils.EndlessRecyclerViewScrollListener;
import com.tache.utils.Helper;
import com.tache.utils.SharedPrefsUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mayank on 20/10/17.
 */
public class SurveysTabFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;

    private ArrayList<Surveys> surveysList;
    private ArrayList<SurveysHistory> surveysHistoryList;
    private SurveysRecyclerAdapter adapterSurveys;
    private SurveysHistoryRecyclerAdapter adapterSurveysHistory;
    private boolean isAlreadyConnected = false;
    private Unbinder unbinder;

    private LinksService linksService;
    private String nextUrl;
    private boolean isLoaderAdded = false;
    private Call<BaseListModel<Surveys>> getSurveys;
    private Call<BaseListModel<SurveysHistory>> getSurveysHistory;

    private boolean isFirstCall = true;
    private boolean hasLoadedOnce = false;
    private boolean isViewCreated = false;
    private boolean isLoaded = false;
  //  SwipeRefreshLayout swipeRefreshLayout;
    private static final String ARG_PARAM1 = "param1";

    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    private String fragmentName, searchQuery, searchRewardMin, searchRewardMax;

    public static SurveysTabFragment newInstance(String value) {
        SurveysTabFragment fragment = new SurveysTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, value);
        fragment.setArguments(args);
        return fragment;
    }

    public static Fragment newInstance(String name, String query, String rewardMin, String rewardMax) {
        SurveysTabFragment fragment = new SurveysTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, name);
        args.putString(ARG_PARAM2, query);
        args.putString(ARG_PARAM3, rewardMin);
        args.putString(ARG_PARAM4, rewardMax);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fragmentName = getArguments().getString(ARG_PARAM1);
            if (fragmentName.equals("Search")) {
                searchQuery = getArguments().getString(ARG_PARAM2);
                searchRewardMin = getArguments().getString(ARG_PARAM3);
                searchRewardMax = getArguments().getString(ARG_PARAM4);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.universal_recycler_and_empty_view, container, false);

        unbinder = ButterKnife.bind(this, view);


        /*swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeOne);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initializeRecyclerHistory();
                getSurveysHistory = linksService.surveysHistory(Helper.getAuthHeader(getContext()));

                swipeRefreshLayout.setRefreshing(false);

            }
        });*/

        initializeRecyclerViewPager();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            // we check that the fragment is becoming visible
            if (isVisibleToUser && !hasLoadedOnce) {
                //run your async task here since the user has just focused on your fragment
                hasLoadedOnce = true;
                loadData();
            }
        }
    }

    private void loadData() {
        if (hasLoadedOnce && isViewCreated && !isLoaded) {
            if (ConnectivityReceiver.isConnected()) {
                initialize();
                isAlreadyConnected = true;
            }
            isLoaded = true;
        }
    }

    private void initializeRecyclerViewPager() {
        emptyView.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        System.out.println("chnadniiiiiiiiii");
        linksService = ApiUtils.retrofitInstance().create(LinksService.class);

        switch (fragmentName) {
            case "Available":
                initializeRecycler();
                getSurveys = linksService.surveys(Helper.getAuthHeader(getContext()));
                System.out.println("=========="+getSurveys);
                break;
            case "History":
                initializeRecyclerHistory();
                getSurveysHistory = linksService.surveysHistory(Helper.getAuthHeader(getContext()));

                break;
            case "Search":
                initializeRecycler();
                getSurveys = linksService.searchSurveys(Helper.getAuthHeader(getContext()), searchQuery, searchRewardMin, searchRewardMax);
                break;
        }

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                initialize();
            }
        });
    }

    private void initializeRecycler() {
        surveysList = new ArrayList<>();
        adapterSurveys = new SurveysRecyclerAdapter(getContext(), surveysList);
        adapterSurveys.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (positionStart == 0) {
                    recyclerView.smoothScrollToPosition(0);
                }
            }
        });
        recyclerView.setAdapter(new AlphaInAnimationAdapter(new ScaleInAnimationAdapter(adapterSurveys)));
        adapterSurveys.showLoading();
    }

    private void initializeRecyclerHistory() {
        surveysHistoryList = new ArrayList<>();
        adapterSurveysHistory = new SurveysHistoryRecyclerAdapter(getContext(), surveysHistoryList);

        adapterSurveysHistory.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
              System.out.println("hnhnhnhn... ==== "+positionStart+" gn "+itemCount);
                if (positionStart == 0) {
                    recyclerView.smoothScrollToPosition(0);
                }

                SharedPrefsUtils.getInstance(getContext()).setIntegerPreference("count_history",itemCount);
            }
        });
        recyclerView.setAdapter(new AlphaInAnimationAdapter(new ScaleInAnimationAdapter(adapterSurveysHistory)));
        adapterSurveysHistory.showLoading();
    }

    private Callback<BaseListModel<Surveys>> surveysCallback = new Callback<BaseListModel<Surveys>>() {
        @Override
        public void onResponse(Call<BaseListModel<Surveys>> call, Response<BaseListModel<Surveys>> response) {

            if (response.isSuccessful()) {
                BaseListModel<Surveys> baseListModelList = response.body();
                ArrayList<Surveys> newList = baseListModelList.getResults();

                for (Surveys abc:
                     newList) {
                    System.out.println("=========");
                    System.out.println("ID"+abc.getSurvey().getTitle());

                }

                try {

                }catch(Exception e){
                    e.printStackTrace();
                }
                nextUrl = baseListModelList.getNext();
//                if (nextUrl == null){
//                    GetPost blankPost = new GetPost();
//                    blankPost.setType(PostType.NO_MORE);
//                    newList.add(blankPost);
//                }
                adapterSurveys.hideLoading();
                adapterSurveys.addItemsAtBottom(newList);
                if (adapterSurveys.getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setText(fragmentName.equals("Search") ? "No results to show" : "No posts to show");
                }
            } else {
                adapterSurveys.hideLoading();
            }
            isLoaderAdded = false;
        }

        @Override
        public void onFailure(Call<BaseListModel<Surveys>> call, Throwable t) {
            Log.d("newPosts", "size: " + t.getCause());
            adapterSurveys.hideLoading();
            isLoaderAdded = false;
        }
    };

    private Callback<BaseListModel<SurveysHistory>> surveysHistoryCallback = new Callback<BaseListModel<SurveysHistory>>() {
        @Override
        public void onResponse(Call<BaseListModel<SurveysHistory>> call, Response<BaseListModel<SurveysHistory>> response) {
            if (response.isSuccessful()) {
                BaseListModel<SurveysHistory> baseListModelList = response.body();
                ArrayList<SurveysHistory> newList = baseListModelList.getResults();
                nextUrl = baseListModelList.getNext();
//                if (nextUrl == null){
//                    GetPost blankPost = new GetPost();
//                    blankPost.setType(PostType.NO_MORE);
//                    newList.add(blankPost);
//                }
                adapterSurveysHistory.hideLoading();
                adapterSurveysHistory.addItemsAtBottom(newList);
                if (adapterSurveysHistory.getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setText("No posts to show");
                }
            } else {
                adapterSurveysHistory.hideLoading();
            }
            isLoaderAdded = false;
        }

        @Override
        public void onFailure(Call<BaseListModel<SurveysHistory>> call, Throwable t) {
            Log.d("newPosts    >", "size: " + t.getCause());
            adapterSurveysHistory.hideLoading();
            isLoaderAdded = false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        //EventBus.getDefault().register(this);
        ConnectivityReceiver.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        //EventBus.getDefault().unregister(this);
        ConnectivityReceiver.unregister(this);
    }

//    @Subscribe
//    public void onNewPostEvent(NewPostEvent newPostEvent){
//        adapterSurveys.addItemOnTop(newPostEvent.getPost());
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isAlreadyConnected && isConnected) {
            initialize();
        }
        if (!isConnected) {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText("Please check your internet connection");
        }
    }

    private void initialize() {
        if (isFirstCall) {
            if (fragmentName.equals("History"))
                getSurveysHistory.enqueue(surveysHistoryCallback);
            else
                getSurveys.enqueue(surveysCallback);
            isFirstCall = false;
            return;
        }

        if (nextUrl != null && !isLoaderAdded) {
            adapterSurveys.addItemAtBottom(null);
            isLoaderAdded = true;

            if (fragmentName.equals("History")) {
                Call<BaseListModel<SurveysHistory>> call = linksService.surveysHistoryNext(Helper.getAuthHeader(getContext()), nextUrl);
                call.enqueue(surveysHistoryCallback);
            } else {
                Call<BaseListModel<Surveys>> call = linksService.surveysNext(Helper.getAuthHeader(getContext()), nextUrl);
                call.enqueue(surveysCallback);
            }
        }
    }

    public void refresh() {
        initializeRecyclerViewPager();
        isFirstCall = true;
        hasLoadedOnce = false;
        isViewCreated = false;
        isLoaded = true;
        loadData();
    }
}