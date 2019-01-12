package com.tache.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.tache.R;
import com.tache.receivers.ConnectivityReceiver;

import java.util.ArrayList;

public abstract class EasyRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = EasyRecyclerViewAdapter.class.getSimpleName();
    private ArrayList<T> itemsList;
    private Context context;
    private static final int LOADING_VIEW = 1;
    private boolean isLoaderShowing = false;

    public EasyRecyclerViewAdapter(@NonNull Context context, @Nullable ArrayList<T> itemsList) {
        this.context = context;
        this.itemsList = itemsList == null ? new ArrayList<T>() : itemsList;
        if (this.itemsList.isEmpty() && ConnectivityReceiver.isConnected()) {
            showLoading();
        }
    }

    public void addItemOnTop(T item) {
        ArrayList<T> items = new ArrayList<>();
        items.add(item);
        addItemsOnTop(items);
    }

    public void addItemsOnTop(ArrayList<T> items) {
        addItemsOnTop(items, null);
    }

    public void addItemsOnTop(ArrayList<T> items, @Nullable EmptyViewListener emptyViewListener) {
        if (checkListEmpty(items, emptyViewListener)) return;
        this.itemsList.addAll(0, items);
        notifyItemRangeInserted(0, items.size());
        hideLoading();
    }

    public void addItemAtBottom(T item) {
        ArrayList<T> items = new ArrayList<>();
        items.add(item);
        addItemsAtBottom(items);
    }

    public void addItemsAtBottom(ArrayList<T> items) {
        addItemsAtBottom(items, null);
    }

    public void addItemsAtBottom(ArrayList<T> items, @Nullable EmptyViewListener emptyViewListener) {
        if (checkListEmpty(items, emptyViewListener)) return;
        int size = itemsList.size();
        //Handles a bug where nothing appears on first position in staggered grid layout manager
        if (size == 0) notifyDataSetChanged();
        this.itemsList.addAll(items);
        notifyItemRangeInserted(size, items.size());
        hideLoading();
    }

    private boolean checkListEmpty(ArrayList<T> items, @Nullable EmptyViewListener emptyViewListener) {
        if (items.size() == 0) {
            if (emptyViewListener != null) emptyViewListener.showEmptyView();
            hideLoading();
            return true;
        } else {
            if (emptyViewListener != null) emptyViewListener.hideEmptyView();
            return false;
        }
    }

    public void removeItemAtBottom() {
        int size = itemsList.size();
        if (itemsList.get(size - 1) == null)
            this.itemsList.remove(size - 1);
        notifyItemRemoved(size - 1);
    }

    public void removeItemAt(int pos) {
        itemsList.remove(pos);
        notifyItemRemoved(pos);
    }

    public void removeItemsTill(int current) {
        if (current >= itemsList.size()) return;
        for (int i = current - 1; i >= 0; i--) {
            itemsList.remove(i);
        }
        notifyItemRangeRemoved(0, current);
    }

    public void clear() {
        int size = itemsList.size();
        itemsList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void findAndRemoveItem(T item) {
        for (int i = 0; i < getItemsListSize(); i++) {
            if (getItem(i).equals(item)) {
                removeItemAt(i);
                return;
            }
        }
    }

    public void hideLoading() {
        if (isLoaderShowing) {
            notifyItemRemoved(getItemCount() - 1);
            isLoaderShowing = false;
        }
    }

    public void showLoading() {
        if (!isLoaderShowing) {
            isLoaderShowing = true;
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public boolean isLoaderShowing() {
        return isLoaderShowing;
    }

    protected T getItem(int position) {
        return itemsList.get(position);
    }

    public ArrayList<T> getItemsList() {
        return itemsList;
    }

    protected int getItemsListSize() {
        return itemsList.size();
    }

    public abstract RecyclerView.ViewHolder onCreateItemView(ViewGroup parent, int viewType);

    public abstract void onBindItemView(RecyclerView.ViewHolder holder, T item, int position);

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (viewType == LOADING_VIEW) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.footer_view, parent, false);
            return new LoadingViewHolder(itemView);
        } else {
            return onCreateItemView(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadingViewHolder) {
            ((LoadingViewHolder) holder).progressBar.getIndeterminateDrawable().setColorFilter(
                    ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        } else {
            if (!itemsList.isEmpty()) {
                onBindItemView(holder, itemsList.get(position), position);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderShowing && position == getItemCount() - 1) {
            return LOADING_VIEW;
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public int getItemCount() {
        int count = itemsList.size();
        if (isLoaderShowing) {
            count++;
        }
        return count;
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loading_progress_bar);
            if (getAdapterPosition() % 2 == 0 && itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) itemView.getLayoutParams();
                layoutParams.setFullSpan(true);
            }
        }
    }

    public interface EmptyViewListener {
        void showEmptyView();

        void hideEmptyView();
    }
}
