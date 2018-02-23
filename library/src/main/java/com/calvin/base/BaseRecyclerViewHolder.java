package com.calvin.base;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by calvin on 2016/1/20 10:29.
 */
public class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> views;

    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
        this.views = new SparseArray<View>();
    }

    public TextView getTextView(@IdRes int viewId) {
        return findView(viewId);
    }

    public Button getButton(@IdRes int viewId) {
        return findView(viewId);
    }

    public ImageView getImageView(@IdRes int viewId) {
        return findView(viewId);
    }

    public <T extends View> T getView(@IdRes int viewId) {
        return findView(viewId);
    }

    /**
     * get ViewHolder's itemView
     */
    public View getConvertView() {
        return itemView;
    }


    protected <T extends View> T findView(@IdRes int viewId) {
        T view = (T) views.get(viewId);
        if (view == null) {
            view = (T) itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return view;
    }

    public static BaseRecyclerViewHolder createViewHolder(@NonNull View itemView)
    {
        BaseRecyclerViewHolder holder = new BaseRecyclerViewHolder(itemView);
        return holder;
    }

    public static BaseRecyclerViewHolder createViewHolder(@NonNull ViewGroup parent,@LayoutRes int layoutId)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent,false);
        BaseRecyclerViewHolder holder = new BaseRecyclerViewHolder(itemView);
        return holder;
    }
}
