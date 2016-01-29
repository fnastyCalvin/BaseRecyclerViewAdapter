package com.calvin.base.divider;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * set margin space onto every item in RecyclerView
 * Created by jiangtao on 2016/1/29 10:13.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int mItemSpace;

    public SpaceItemDecoration(int itemSpace) {
        mItemSpace = mItemSpace;
    }

    public SpaceItemDecoration(@NonNull Context context, @DimenRes int itemSpaceId) {
        this(context.getResources().getDimensionPixelSize(itemSpaceId));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mItemSpace, mItemSpace, mItemSpace, mItemSpace);
    }
}
