package com.tache.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tache.R;


/**
 * Created by mayank on 9/12/16.
 */

public class BuyCoinsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_buy_coin, parent, false);
        return new BuyCoinsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return 8;
    }


    private class BuyCoinsViewHolder extends RecyclerView.ViewHolder{

        public BuyCoinsViewHolder(View itemView) {
            super(itemView);
        }
    }
}
