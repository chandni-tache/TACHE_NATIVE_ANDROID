package com.tache.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.tache.R;
import com.tache.adapter.BuyCoinsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by mayank on 9/12/16.
 */

public class BuyCoinsFragment extends Fragment {

    @BindView(R.id.frag_buy_coins_recycler_view)
    RecyclerView recyclerView;
    private Unbinder unbinder;
    public static boolean isOpen;

    @Override
    public void onStart() {
        super.onStart();
        isOpen = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        isOpen = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_buy_coins, container, false);
        unbinder = ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        BuyCoinsAdapter buyCoinsAdapter = new BuyCoinsAdapter();
        recyclerView.setAdapter(buyCoinsAdapter);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
