package com.calvin.base.divider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.calvin.base.BaseRecyclerViewAdapter;

/**
 * <p>divider for LinearLayoutManager RecyclerView</p>
 * <p>only for item, when RecyclerView has headers or footers</p>
 * Created by calvin on 2016/1/22 11:16.
 */
public class ListDividerItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    private BaseRecyclerViewAdapter adapter;

    private Drawable mDivider;

    private int mOrientation;

    public ListDividerItemDecoration(@NonNull Context context, int orientation) {
        this(context,orientation,-1);
    }

    public ListDividerItemDecoration(@NonNull Context context, int orientation, @IdRes int drawableId) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        if(drawableId > 0){
            mDivider = a.getDrawable(drawableId);
        }
        else {
            mDivider = a.getDrawable(0);
        }
        a.recycle();
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if(adapter == null) {
            adapter = (BaseRecyclerViewAdapter) parent.getAdapter();
        }
        else
        {
            if(adapter.getData().isEmpty()){
                return;
            }
        }
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int visibleChildCount = parent.getChildCount();
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        for (int i = 0; i < visibleChildCount; i++) {
            final View child = parent.getChildAt(i);
            if(child != null) {
                int position = parent.getChildAdapterPosition(child);
                if(position >= adapter.getHeaderViews().size() && position < adapter.getHeaderViews().size() + adapter.getData().size() - 1){
                    final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                    final int top = child.getBottom() + params.bottomMargin;
                    final int bottom = top + mDivider.getIntrinsicHeight();
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(c);
                }
            }
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int visibleChildCount = parent.getChildCount();
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();
        for (int i = 0; i < visibleChildCount; i++) {
            final View child = parent.getChildAt(i);
            if(child != null) {
                int position = parent.getChildAdapterPosition(child);
                if(position >= adapter.getHeaderViews().size() && position < adapter.getHeaderViews().size() + adapter.getData().size() -1) {
                    final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                    final int left = child.getRight() + params.rightMargin;
                    final int right = left + mDivider.getIntrinsicHeight();
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(c);
                }
            }
        }
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if(adapter == null) return;
        int position = parent.getChildAdapterPosition(view);
        if (mOrientation == VERTICAL_LIST) {
            if(position >= adapter.getHeaderViews().size() && position < adapter.getHeaderViews().size() + adapter.getData().size()){
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            }
            else {
                outRect.set(0, 0, 0, 0);
            }

        } else
        {
            if(position >= adapter.getHeaderViews().size() && position < adapter.getHeaderViews().size() + adapter.getData().size()){
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            }
            else {
                outRect.set(0, 0, 0, 0);
            }

        }
    }

}
