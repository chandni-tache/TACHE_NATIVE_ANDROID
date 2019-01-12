package com.tache.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import com.tache.utils.TimeFormatHelper;

import java.util.ArrayList;


/**
 * Created by mayank on 17/9/16.
 */
public class MissionsRecyclerAdapter extends EasyRecyclerViewAdapter<Mission> {
    private Context context;
    private ArrayList<Mission> dataList;
    private Gson gson;
    private String fragmentName;

    public MissionsRecyclerAdapter(@NonNull Context context, ArrayList<Mission> postsList, String fragmentName) {
        super(context, postsList);
        this.context = context;
        this.dataList = postsList;
        this.gson = new Gson();
        this.fragmentName = fragmentName;
    }

    @Override
    public MissionsViewHolder onCreateItemView(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_mission, parent, false);
        return new MissionsViewHolder(itemView);
    }

    @Override
    public void onBindItemView(RecyclerView.ViewHolder holder, Mission item, int position) {
        ((MissionsViewHolder) holder).setData(item);
    }

    private class MissionsViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private CircularImageView imageView;
        private TextView title, brandName, location, dateRange, price;

        public MissionsViewHolder(View itemView) {
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
                    intent.putExtra("data", gson.toJson(dataList.get(getAdapterPosition()), Mission.class));
                    intent.putExtra("name", fragmentName);
                    context.startActivity(intent);
                }
            });
        }

        public void setData(Mission item) {
            Glide.with(context).load(item.getCompany_logo())
                    .placeholder(R.drawable.ic_business)
                    .dontAnimate()
                    .into(imageView);
            title.setText(item.getSurvey().getTitle());
            brandName.setText(item.getCompany_name());
            location.setText(item.getLocation());
            dateRange.setText(TimeFormatHelper.getInDMY(item.getDate_from()) + " - " + TimeFormatHelper.getInDMY(item.getDate_to()));
            price.setText(String.valueOf(item.getPrice()));

//                switch (item.getApproval_status()) {
//                    case 1:
//                        cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green));
//                        break;
//                    case 3:
//                        cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.yellow));
//                        break;
//                    case 5:
//                        break;
//                    case 7:
//                        cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.redd));
//                        break;
//                }
        }
    }


}
