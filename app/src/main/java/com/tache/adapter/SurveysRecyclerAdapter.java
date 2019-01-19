package com.tache.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.tache.R;
import com.tache.activity.StartMySurveyActivity;
import com.tache.activity.StartSurveyMissionActivity;
import com.tache.fragments.WebViewFragment;
import com.tache.rest.models.response.Surveys;
import com.tache.rest.services.LinksService;
import com.tache.utils.Helper;
import com.tache.utils.TimeFormatHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


/**
 * Created by mayank on 17/9/16.       Panel Adapter Class
 */
public class SurveysRecyclerAdapter extends EasyRecyclerViewAdapter<Surveys> {
    private Context context;
    private ArrayList<Surveys> dataList;

    public SurveysRecyclerAdapter(@NonNull Context context, ArrayList<Surveys> postsList) {
        super(context, postsList);
        this.context = context;
        this.dataList = postsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateItemView(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_survey, parent, false);
        return new SurveysViewHolder(itemView);
    }

    @Override
    public void onBindItemView(RecyclerView.ViewHolder holder, Surveys item, int position) {
        ((SurveysViewHolder) holder).setData(item);
    }

    class SurveysViewHolder extends RecyclerView.ViewHolder {
        private TextView companyName, title, price, people_responded_count, people_total_count, timeSummary;
        private CircularImageView companyLogo;

        public SurveysViewHolder(View itemView) {
            super(itemView);
            companyName = (TextView) itemView.findViewById(R.id.item_survey_brand_name);
            title = (TextView) itemView.findViewById(R.id.title);
            price = (TextView) itemView.findViewById(R.id.price);
            timeSummary = (TextView) itemView.findViewById(R.id.item_survey_time);
            companyLogo = (CircularImageView) itemView.findViewById(R.id.item_survey_profile_image);
            people_responded_count = (TextView) itemView.findViewById(R.id.item_survey_people_responded_count);
            people_total_count = (TextView) itemView.findViewById(R.id.item_survey_people_total_count);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Intent start = new Intent(context, StartMySurveyActivity.class);

                    start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(start);*/


                    Intent start = new Intent(context, StartSurveyMissionActivity.class);
                  //  Intent start = new Intent(Intent.ACTION_VIEW);
                    start.putExtra("url", String.format(LinksService.TASK_URL, dataList.get(getAdapterPosition()).getId(), Helper.getAuthKey(context)));
                    start.putExtra("what", "Survey");
                    start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(start);

                   // System.out.println("Hello Sonu ke titu ki sweeti == "+String.format(LinksService.TASK_URL, dataList.get(getAdapterPosition()).getId(), Helper.getAuthKey(context)));

                    Toast.makeText(context, "Jai ho.....", Toast.LENGTH_SHORT).show();
                    //using Activity for webView for now..
                  //  EventBus.getDefault().post(WebViewFragment.newInstance(String.format(LinksService.TASK_URL, dataList.get(getAdapterPosition()).getId(), Helper.getAuthKey(context))));
                }
            });


        }


        public void setData(Surveys item) {
            Glide.with(context).load(item.getCompany_logo())
                    .placeholder(R.drawable.ic_business)
                    .dontAnimate()
                    .into(companyLogo);
            companyName.setText(item.getCompany_name());
            title.setText(item.getSurvey().getTitle());
            people_responded_count.setText(String.valueOf(item.getResponses_received()));
            people_total_count.setText(String.valueOf(item.getResponses_required()));
            timeSummary.setText(TimeFormatHelper.getDaysHoursMinutes(item.getDate_to()));

            System.out.println("My Survey Title = == "+item.getSurvey().getTitle());
            Toast.makeText(context, item.getSurvey().getTitle() , Toast.LENGTH_SHORT).show();

        }
    }




}
