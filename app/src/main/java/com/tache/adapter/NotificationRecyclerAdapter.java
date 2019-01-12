package com.tache.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tache.R;
import com.tache.rest.models.response.NotificationResponse;
import com.tache.utils.TimeFormatHelper;

import java.util.ArrayList;

/**
 * Created by a_man on 4/15/2017.
 */

public class NotificationRecyclerAdapter extends EasyRecyclerViewAdapter<NotificationResponse> {
    private Context context;
    private ArrayList<NotificationResponse> dataList;

    public NotificationRecyclerAdapter(@NonNull Context context, @Nullable ArrayList<NotificationResponse> itemsList) {
        super(context, itemsList);
        this.context = context;
        this.dataList = itemsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateItemView(ViewGroup parent, int viewType) {
        return new NotificationViewHolder(LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false));
    }

    @Override
    public void onBindItemView(RecyclerView.ViewHolder holder, NotificationResponse item, int position) {
        ((NotificationViewHolder) holder).setData(item);
    }

    private class NotificationViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout linearLayout;
        private TextView title, time, message;

        public NotificationViewHolder(View view) {
            super(view);
            linearLayout = (LinearLayout) view.findViewById(R.id.content);
            title = (TextView) view.findViewById(R.id.title);
            time = (TextView) view.findViewById(R.id.time);
            message = (TextView) view.findViewById(R.id.message);
        }

        public void setData(NotificationResponse item) {
            title.setText(item.getTitle());
            message.setText(item.getDetail());
            time.setText(TimeFormatHelper.getRelativeTime(item.getCreated_on()));
        }
    }
}
