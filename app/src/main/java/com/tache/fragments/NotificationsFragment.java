package com.tache.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tache.R;
import com.tache.adapter.NotificationRecyclerAdapter;
import com.tache.events.NotificationEvent;
import com.tache.receivers.ConnectivityReceiver;
import com.tache.rest.ApiUtils;
import com.tache.rest.models.BaseListModel;
import com.tache.rest.models.response.NotificationResponse;
import com.tache.rest.services.LinksService;
import com.tache.utils.EndlessRecyclerViewScrollListener;
import com.tache.utils.Helper;

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

/**
 * Created by mayank on 23/2/17.
 */

public class NotificationsFragment extends Fragment {
    private Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;
    private LinksService linksService;
    private NotificationRecyclerAdapter notificationAdapter;
    private Call<BaseListModel<NotificationResponse>> getNotifications;

    private boolean isFirstCall = true;
    private boolean isViewCreated = false;
    private boolean isLoaded = false;
    private String nextUrl;
    private boolean isLoaderAdded = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refresh();
        loadData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            refresh();
            if (isVisibleToUser) {
                loadData();
            }
        }
    }

    private void loadData() {
        if (isViewCreated && !isLoaded) {
            if (ConnectivityReceiver.isConnected()) {
                initialize();
            }
            isLoaded = true;
        }
    }

    private void initializeRecycler() {
        emptyView.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        linksService = ApiUtils.retrofitInstance().create(LinksService.class);

        ArrayList<NotificationResponse> NotificationResponseList = new ArrayList<>();
        notificationAdapter = new NotificationRecyclerAdapter(getContext(), NotificationResponseList);
        notificationAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (positionStart == 0) {
                    recyclerView.smoothScrollToPosition(0);
                }
            }
        });
        recyclerView.setAdapter(new AlphaInAnimationAdapter(new ScaleInAnimationAdapter(notificationAdapter)));
        notificationAdapter.showLoading();

        getNotifications = linksService.getNotification(Helper.getAuthHeader(getContext()));

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                initialize();
            }
        });
    }

    public void refresh() {
        initializeRecycler();
        isFirstCall = true;
        isViewCreated = false;
        isLoaded = true;
    }

    private void initialize() {
        if (isFirstCall) {
            getNotifications.enqueue(notificationResponsesCallback);
            isFirstCall = false;
            return;
        }

        if (nextUrl != null && !isLoaderAdded) {
            notificationAdapter.addItemAtBottom(null);
            isLoaderAdded = true;

            Call<BaseListModel<NotificationResponse>> call = linksService.getNotificationNext(Helper.getAuthHeader(getContext()), nextUrl);
            call.enqueue(notificationResponsesCallback);
        }
    }

    private Callback<BaseListModel<NotificationResponse>> notificationResponsesCallback = new Callback<BaseListModel<NotificationResponse>>() {
        @Override
        public void onResponse(Call<BaseListModel<NotificationResponse>> call, Response<BaseListModel<NotificationResponse>> response) {
            if (response.isSuccessful()) {
                BaseListModel<NotificationResponse> baseListModelList = response.body();
                ArrayList<NotificationResponse> newList = baseListModelList.getResults();
                nextUrl = baseListModelList.getNext();
//                if (nextUrl == null) {
//                    NotificationResponse blankPost = new NotificationResponse();
//                    blankPost.setType(PostType.NO_MORE);
//                    newList.add(blankPost);
//                }
                notificationAdapter.hideLoading();
                notificationAdapter.addItemsAtBottom(newList);
                if (notificationAdapter.getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    //recyclerView.setVisibility(View.GONE);
                    emptyView.setText("No notifications to show");
                }
            } else {
                notificationAdapter.hideLoading();
            }
            isLoaderAdded = false;
        }

        @Override
        public void onFailure(Call<BaseListModel<NotificationResponse>> call, Throwable t) {
            Log.d("newPosts", "size: " + t.getCause());
            notificationAdapter.hideLoading();
            isLoaderAdded = false;
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
