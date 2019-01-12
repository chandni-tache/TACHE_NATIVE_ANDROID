package com.tache.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.tache.R;
import com.tache.activity.TaskDetailActivity;
import com.tache.rest.models.response.Mission;
import com.tache.rest.models.response.MissionHistory;
import com.tache.utils.Helper;
import com.tache.utils.TimeFormatHelper;

import java.util.ArrayList;

/**
 * Created by a_man on 4/11/2017.
 */

public class MissionsHistoryRecyclerAdapter extends EasyRecyclerViewAdapter<MissionHistory> {
    private Context context;
    private ArrayList<MissionHistory> dataList;
    private Gson gson;

    public MissionsHistoryRecyclerAdapter(Context context, ArrayList<MissionHistory> missionHistoryList) {
        super(context, missionHistoryList);
        this.context = context;
        this.dataList = missionHistoryList;
        this.gson = new Gson();
    }

    @Override
    public MissionsHistoryViewHolder onCreateItemView(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_mission, parent, false);
        return new MissionsHistoryViewHolder(itemView);
    }

    @Override
    public void onBindItemView(RecyclerView.ViewHolder holder, MissionHistory item, int position) {
        ((MissionsHistoryViewHolder) holder).setData(item);
    }

    private class MissionsHistoryViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private CircularImageView imageView;
        private TextView title, brandName, location, dateRange, price;

        public MissionsHistoryViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.itemCard);
            imageView = (CircularImageView) itemView.findViewById(R.id.item_mission_profile_image);
            title = (TextView) itemView.findViewById(R.id.item_mission_title);
            brandName = (TextView) itemView.findViewById(R.id.item_mission_brand_name);
            location = (TextView) itemView.findViewById(R.id.item_mission_location);
            dateRange = (TextView) itemView.findViewById(R.id.item_mission_date_range);
            price = (TextView) itemView.findViewById(R.id.item_mission_price);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, TaskDetailActivity.class);
                    intent.putExtra("data", gson.toJson(dataList.get(getAdapterPosition()).getMission(), Mission.class));
                    context.startActivity(intent);
                }
            });
        }

        public void setData(MissionHistory item) {
            Glide.with(context).load(item.getMission().getCompany_logo())
                    .placeholder(R.drawable.ic_business)
                    .dontAnimate()
                    .into(imageView);
            title.setText(item.getMission().getSurvey().getTitle());
            brandName.setText(item.getMission().getCompany_name());
            location.setText(item.getMission().getLocation());
            dateRange.setText(TimeFormatHelper.getInDMY(item.getMission().getDate_from()) + " - " + TimeFormatHelper.getInDMY(item.getMission().getDate_to()));
            price.setText(String.valueOf(item.getMission().getPrice()));

            switch (item.getApproval_status()) {
                case 1:
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green));
                    break;
                case 3:
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.yellow));
                    break;
                case 5:
                    break;
                case 7:
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.redd));
                    break;
            }
        }
    }

}
