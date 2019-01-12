package com.tache.adapter;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tache.R;
import com.tache.rest.models.response.CategorySearch;

import java.util.ArrayList;

/**
 * Created by a_man on 2/24/2017.
 */
public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.SearchViewHolder> {
    private Context context;
    private ArrayList<CategorySearch> dataList;
    private ColorDrawable white, blue;

    public SearchRecyclerAdapter(Context context, ArrayList<CategorySearch> itemsList) {
        this.context = context;
        this.dataList = itemsList;
        white = new ColorDrawable(Color.WHITE);
        blue = new ColorDrawable(ContextCompat.getColor(context, R.color.colorAccent));
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_search_item, parent, false));
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        holder.setData(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void addItemsAtBottom(ArrayList<CategorySearch> newList) {
        if (newList.size() == 0) return;
        int size = newList.size();
        //Handles a bug where nothing appears on first position in staggered grid layout manager
        if (size == 0) notifyDataSetChanged();
        this.dataList.addAll(newList);
        notifyItemRangeInserted(size, newList.size());
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView categorySearch;

        public SearchViewHolder(View itemView) {
            super(itemView);
            categorySearch = (TextView) itemView.findViewById(R.id.categorySearch);
            categorySearch.setOnClickListener(this);
        }

        public void setData(CategorySearch item) {
            categorySearch.setText(item.getTitle());
            if (item.isSelected()) {
                categorySearch.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                categorySearch.setTextColor(Color.WHITE);
            } else {
                categorySearch.setBackgroundColor(Color.WHITE);
                categorySearch.setTextColor(ContextCompat.getColor(context, R.color.mainbackcolor));
            }
        }

        @Override
        public void onClick(View view) {
            ColorDrawable[] color;
            ObjectAnimator colorTextAnim;
            CategorySearch dataItem = dataList.get(getAdapterPosition());
            if (dataItem.isSelected()) {
                color = new ColorDrawable[]{blue, white};
                colorTextAnim = ObjectAnimator.ofInt(categorySearch, "textColor", Color.WHITE, ContextCompat.getColor(context, R.color.mainbackcolor));
                dataItem.setSelected(false);
            } else {
                color = new ColorDrawable[]{white, blue};
                colorTextAnim = ObjectAnimator.ofInt(categorySearch, "textColor", ContextCompat.getColor(context, R.color.mainbackcolor), Color.WHITE);
                dataItem.setSelected(true);
            }
            TransitionDrawable transMain = new TransitionDrawable(color);
            colorTextAnim.setEvaluator(new ArgbEvaluator());
            categorySearch.setBackground(transMain);
            transMain.startTransition(250);
            colorTextAnim.setDuration(250);
            colorTextAnim.start();
        }
    }
}
