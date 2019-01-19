package com.tache.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.tache.R;
import com.tache.rest.models.response.SurveysHistory;
import com.tache.utils.TimeFormatHelper;

import java.util.ArrayList;

/**
 * Created by a_man on 4/11/2017.
 */

public class SurveysHistoryRecyclerAdapter extends EasyRecyclerViewAdapter<SurveysHistory> {
    private Context context;
    private ArrayList<SurveysHistory> dataList;

    public SurveysHistoryRecyclerAdapter(Context context, ArrayList<SurveysHistory> dataList) {
        super(context, dataList);
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateItemView(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_survey, parent, false);
        return new SurveysHistoryViewHolder(itemView);
    }

    @Override
    public void onBindItemView(RecyclerView.ViewHolder holder, SurveysHistory item, int position) {
        ((SurveysHistoryViewHolder) holder).setData(item);
    }

    class SurveysHistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView companyName, title, price, people_responded_count, people_total_count, timeSummary;
        private CircularImageView companyLogo;

        public SurveysHistoryViewHolder(View itemView) {
            super(itemView);
            companyName = (TextView) itemView.findViewById(R.id.item_survey_brand_name);
            title = (TextView) itemView.findViewById(R.id.title);
            price = (TextView) itemView.findViewById(R.id.price);
            timeSummary = (TextView) itemView.findViewById(R.id.item_survey_time);
            companyLogo = (CircularImageView) itemView.findViewById(R.id.item_survey_profile_image);
            people_responded_count = (TextView) itemView.findViewById(R.id.item_survey_people_responded_count);
            people_total_count = (TextView) itemView.findViewById(R.id.item_survey_people_total_count);
        }

        public void setData(SurveysHistory item) {
            Glide.with(context).load(item.getSurveys().getCompany_logo())
                    .placeholder(R.drawable.ic_business)
                    .dontAnimate()
                    .into(companyLogo);
            companyName.setText(item.getSurveys().getCompany_name());
            title.setText(item.getSurveys().getSurvey().getTitle());
            people_responded_count.setText(String.valueOf(item.getSurveys().getResponses_received()));
            people_total_count.setText(String.valueOf(item.getSurveys().getResponses_required()));
            timeSummary.setText(TimeFormatHelper.getDaysHoursMinutes(item.getSurveys().getDate_to()));
        }
    }

}
