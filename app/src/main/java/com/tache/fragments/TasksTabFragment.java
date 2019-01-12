package com.tache.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tache.R;
import com.tache.activity.MapsActivity;
import com.tache.adapter.MissionsHistoryRecyclerAdapter;
import com.tache.adapter.MissionsRecyclerAdapter;
import com.tache.events.OnMapOpenEvent;
import com.tache.receivers.ConnectivityReceiver;
import com.tache.rest.ApiUtils;
import com.tache.rest.models.BaseListModel;
import com.tache.rest.models.response.Mission;
import com.tache.rest.models.response.MissionHistory;
import com.tache.rest.services.LinksService;
import com.tache.utils.EndlessRecyclerViewScrollListener;
import com.tache.utils.Helper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by mayank on 15/2/17.
 */
public class TasksTabFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;
    @BindView(R.id.colorLegend)
    View colorLegend;

    private MissionsRecyclerAdapter adapterMissions;
    private MissionsHistoryRecyclerAdapter adapterMissionsHistory;
    private boolean isAlreadyConnected = false;
    private Unbinder unbinder;

    private Call<BaseListModel<Mission>> getMissions;
    private Call<BaseListModel<MissionHistory>> getMissionsHistory;

    private boolean isFirstCall = true;
    private boolean hasLoadedOnce = false;
    private boolean isViewCreated = false;
    private boolean isLoaded = false;
    private String nextUrl;
    private boolean isLoaderAdded = false;

    private static final String ARG_PARAM1 = "param1";

    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    private String fragmentName, searchQuery, searchRewardMin, searchRewardMax;
    private LinksService linksService;

    public static TasksTabFragment newInstance(String name) {
        TasksTabFragment fragment = new TasksTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, name);
        fragment.setArguments(args);
        return fragment;
    }

    public static TasksTabFragment newInstance(String name, String query, String rewardMin, String rewardMax) {
        TasksTabFragment fragment = new TasksTabFragment();
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
        initializeRecyclerViewPager();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("homefrag", "viewcreated");
        isViewCreated = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            if (isVisibleToUser && !hasLoadedOnce) {
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
        linksService = ApiUtils.retrofitInstance().create(LinksService.class);

        switch (fragmentName) {
            case "Available":
                initializeRecycler();
                getMissions = linksService.missions(Helper.getAuthHeader(getContext()));
                System.out.println("Mera Token one=  "+Helper.getAuthHeader(getContext()));
                break;
            case "Assigned":
                initializeRecycler();
                getMissions = linksService.missionsApproved(Helper.getAuthHeader(getContext()));
                System.out.println("Inside Assigned     "+getMissions.toString().length()+"=======");
                System.out.println("Mera Token two=  "+Helper.getAuthHeader(getContext()));
                System.out.print("\n=================1");

                break;
            case "Completed":
                initializeRecyclerHistory();
                colorLegend.setVisibility(View.VISIBLE);
                System.out.print("\n=================2");
                getMissionsHistory = linksService.missionsHistory(Helper.getAuthHeader(getContext()));
                System.out.println("Mera Token three=  "+Helper.getAuthHeader(getContext()));
                break;
            case "Search":
                initializeRecycler();
                getMissions = linksService.searchTask(Helper.getAuthHeader(getContext()), searchQuery, searchRewardMin, searchRewardMax);
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
        System.out.println(" abcd123 ");
        ArrayList<Mission> missionList = new ArrayList<>();
        System.out.println("list"+missionList.toString());
        adapterMissions = new MissionsRecyclerAdapter(getContext(), missionList, fragmentName);
        adapterMissions.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (positionStart == 0) {
                    recyclerView.smoothScrollToPosition(0);
                }
            }
        });
        recyclerView.setAdapter(new AlphaInAnimationAdapter(new ScaleInAnimationAdapter(adapterMissions)));
        adapterMissions.showLoading();
        System.out.println("fjrehskguehurg");
    }

    private void initializeRecyclerHistory() {
        ArrayList<MissionHistory> missionHistoryList = new ArrayList<>();
        adapterMissionsHistory = new MissionsHistoryRecyclerAdapter(getContext(), missionHistoryList);
        adapterMissionsHistory.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (positionStart == 0) {
                    recyclerView.smoothScrollToPosition(0);
                }
            }
        });
        recyclerView.setAdapter(new AlphaInAnimationAdapter(new ScaleInAnimationAdapter(adapterMissionsHistory)));
        adapterMissionsHistory.showLoading();
    }

    private Callback<BaseListModel<Mission>> missionsCallback = new Callback<BaseListModel<Mission>>() {


        @Override
        public void onResponse(Call<BaseListModel<Mission>> call, Response<BaseListModel<Mission>> response) {
            System.out.println("vnfesjrhfr"+missionsCallback.toString().length());
//            System.out.println("Response of TasksTabFragment one "+response.body().getResults().toString());
            if (response.isSuccessful()) {
                BaseListModel<Mission> baseListModelList = response.body();
                ArrayList<Mission> newList = baseListModelList.getResults();
                nextUrl = baseListModelList.getNext();
                System.out.print("Next Url one = "+nextUrl);
//                if (nextUrl == null) {
//                    Mission blankPost = new Mission();
//                    blankPost.setType(PostType.NO_MORE);
//                    newList.add(blankPost);
//                }
                adapterMissions.hideLoading();
                adapterMissions.addItemsAtBottom(newList);
                if (adapterMissions.getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    //recyclerView.setVisibility(View.GONE);
                    emptyView.setText(fragmentName.equals("Search") ? "No results to show" : "No posts to show");
                }
            } else {
                adapterMissions.hideLoading();
            }
            isLoaderAdded = false;
        }

        @Override
        public void onFailure(Call<BaseListModel<Mission>> call, Throwable t) {
            Log.d("newPosts", "size: " + t.getCause());
            adapterMissions.hideLoading();
            isLoaderAdded = false;
        }
    };

    private Callback<BaseListModel<MissionHistory>> missionsHistoryCallback = new Callback<BaseListModel<MissionHistory>>() {
        @Override
        public void onResponse(Call<BaseListModel<MissionHistory>> call, Response<BaseListModel<MissionHistory>> response) {
            System.out.println("Response of TasksTabFragment two "+response.toString());
            if (response.isSuccessful()) {
                BaseListModel<MissionHistory> baseListModelList = response.body();
                ArrayList<MissionHistory> newList = baseListModelList.getResults();
                nextUrl = baseListModelList.getNext();
                System.out.print("Next Url two = "+nextUrl);
//                if (nextUrl == null) {
//                    GetPost blankPost = new GetPost();
//                    blankPost.setType(PostType.NO_MORE);
//                    newList.add(blankPost);
//                }
                adapterMissionsHistory.hideLoading();
                adapterMissionsHistory.addItemsAtBottom(newList);
                if (adapterMissionsHistory.getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setText("No posts to show");
                    //recyclerView.setVisibility(View.GONE);
                }
            } else {
                adapterMissionsHistory.hideLoading();
            }
            isLoaderAdded = false;
        }

        @Override
        public void onFailure(Call<BaseListModel<MissionHistory>> call, Throwable t) {
            Log.d("newPosts", "size: " + t.getCause());
            adapterMissionsHistory.hideLoading();
            isLoaderAdded = false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        ConnectivityReceiver.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        ConnectivityReceiver.unregister(this);
    }

//    @Subscribe
//    public void onNewPostEvent(NewPostEvent newPostEvent) {
//        adapterMissions.addItemOnTop(newPostEvent.getPost());
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
            if (fragmentName.equals("Completed"))
                getMissionsHistory.enqueue(missionsHistoryCallback);
            else
                getMissions.enqueue(missionsCallback);
            isFirstCall = false;
            return;
        }

        if (nextUrl != null && !isLoaderAdded) {
            adapterMissions.addItemAtBottom(null);
            isLoaderAdded = true;

            if (fragmentName.equals("Completed")) {
                Call<BaseListModel<MissionHistory>> call = linksService.missionsHistoryNext(Helper.getAuthHeader(getContext()), nextUrl);
                call.enqueue(missionsHistoryCallback);
            } else {
                Call<BaseListModel<Mission>> call = linksService.missionsNext(Helper.getAuthHeader(getContext()), nextUrl);
                call.enqueue(missionsCallback);
            }
        }
    }

    @Subscribe
    public void openMaps(OnMapOpenEvent onMapOpenEvent) {
        if (onMapOpenEvent != null && onMapOpenEvent.getFragName().equals(fragmentName)) {
            if (adapterMissions.getItemCount() <= 0)
                Toast.makeText(getContext(), "No Audit available here", Toast.LENGTH_SHORT).show();
            else
                startActivity(MapsActivity.getNewIntent(getContext(), adapterMissions.getItemsList()));
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