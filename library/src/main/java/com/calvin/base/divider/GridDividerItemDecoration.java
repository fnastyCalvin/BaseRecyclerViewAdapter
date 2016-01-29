package com.calvin.base.divider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.calvin.base.BaseRecyclerViewAdapter;

/**
 * <p>divider for GridLayoutManager RecyclerView</p>
 * <p>only for item, when RecyclerView has headers or footers</p>
 * Created by calvin on 2016/1/22 11:16.
 */
public class GridDividerItemDecoration extends RecyclerView.ItemDecoration
{

    private static final int[] ATTRS = new int[] { android.R.attr.listDivider };
    private Drawable mDivider;

    private BaseRecyclerViewAdapter adapter;

    public static final int HORIZONTAL = 2;

    public static final int VERTICAL = 1;

    private int mOrientation = 1;

    public GridDividerItemDecoration(@NonNull Context context,int orientation)
    {
        this(context, orientation, -1);
    }

    public GridDividerItemDecoration(@NonNull Context context,int orientation, @IdRes int drawableId) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        if(drawableId > 0){
            mDivider = a.getDrawable(drawableId);
        }
        else {
            mDivider = a.getDrawable(0);
        }
        mOrientation = orientation;
        a.recycle();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state)
    {
        if(adapter == null) {
            adapter = (BaseRecyclerViewAdapter) parent.getAdapter();
        }
        else
        {
            if(adapter.getData().isEmpty()){
                return;
            }
            drawHorizontal(c, parent);
            drawVertical(c, parent);
        }
    }


    /**
     * 获取列数
     * @param parent
     * @return
     */
    private int getSpanCount(RecyclerView parent)
    {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager)
        {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager)
        {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    public void drawHorizontal(Canvas c, RecyclerView parent)
    {
        int visibleChildCount = parent.getChildCount();
        for (int i = 0; i < visibleChildCount; i++)
        {
            final View child = parent.getChildAt(i);
            if(child != null){
                int position = parent.getChildAdapterPosition(child);
                int rowIndex = getRowIndex(position, parent);
                int columnIndex = getColumnIndex(position, parent);
                int totalDataRow = getTotalDataRow(getSpanCount(parent));
                if(mOrientation == VERTICAL){
                    if (rowIndex >= adapter.getHeaderViews().size() && rowIndex < adapter.getHeaderViews().size() + totalDataRow - 1) {
                        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                        final int left = child.getLeft() - params.leftMargin;
                        final int right = child.getRight() + params.rightMargin + mDivider.getIntrinsicWidth();
                        final int top = child.getBottom() + params.bottomMargin;
                        final int bottom = top + mDivider.getIntrinsicHeight();
                        mDivider.setBounds(left, top, right, bottom);
                        mDivider.draw(c);
                    }
                }
                else {
                    if (columnIndex >= adapter.getHeaderViews().size() && columnIndex < adapter.getHeaderViews().size() + totalDataRow ) {
                        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                        final int left = child.getLeft() - params.leftMargin;
                        final int right = child.getRight() + params.rightMargin + mDivider.getIntrinsicWidth();
                        final int top = child.getBottom() + params.bottomMargin;
                        final int bottom = top + mDivider.getIntrinsicHeight();
                        mDivider.setBounds(left, top, right, bottom);
                        mDivider.draw(c);
                    }
                }
            }
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent)
    {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++)
        {
            final View child = parent.getChildAt(i);
            if(child != null) {
                int position = parent.getChildAdapterPosition(child);
                int columnIndex = getColumnIndex(position, parent);
                int rowIndex = getRowIndex(position, parent);
                int totalDataRow = getTotalDataRow(getSpanCount(parent));
                if(mOrientation == VERTICAL) {
                    if (rowIndex >= adapter.getHeaderViews().size() && rowIndex < adapter.getHeaderViews().size() + totalDataRow) {
                        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                        final int top = child.getTop() - params.topMargin;
                        final int bottom = child.getBottom() + params.bottomMargin;
                        final int left = child.getRight() + params.rightMargin;
                        final int right = left + mDivider.getIntrinsicWidth();
                        mDivider.setBounds(left, top, right, bottom);
                        mDivider.draw(c);
                    }
                }
                else {
                    if (columnIndex >= adapter.getHeaderViews().size() && columnIndex < adapter.getHeaderViews().size() + totalDataRow -1) {
                        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                        final int top = child.getTop() - params.topMargin;
                        final int bottom = child.getBottom() + params.bottomMargin;
                        final int left = child.getRight() + params.rightMargin;
                        final int right = left + mDivider.getIntrinsicWidth();
                        mDivider.setBounds(left, top, right, bottom);
                        mDivider.draw(c);
                    }
                }
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if(adapter == null) return;
        int position = parent.getChildAdapterPosition(view);
        int rowIndex = getRowIndex(position, parent);
        int columbIndex = getColumnIndex(position, parent);
//        Log.d("testr", "getGroupIndex---->" + getRowIndex(position, parent));
//        Log.d("testr", "getColumnIndex---->" + getColumnIndex(position, parent));
        int spanCount = getSpanCount(parent);
        int totalDataRow = getTotalDataRow(spanCount);
        if( parent.getLayoutManager().canScrollVertically()){
            if(rowIndex >= adapter.getHeaderViews().size() && rowIndex < adapter.getHeaderViews().size() + totalDataRow){
                if (rowIndex == totalDataRow + adapter.getHeaderViews().size()-1)// 如果是最后一行，则不需要绘制底部
                {
                    outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
                } else if (columbIndex == spanCount - 1)// 如果是最后一列，则不需要绘制右边
                {
                    outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
                } else
                {
                    outRect.set(0, 0, mDivider.getIntrinsicWidth(),mDivider.getIntrinsicHeight());
                }
            }
        }
        else {
            if(columbIndex >= adapter.getHeaderViews().size() && columbIndex < adapter.getHeaderViews().size() + totalDataRow){
                if (rowIndex == totalDataRow + adapter.getHeaderViews().size()-1)// 最后一行，则不需要绘制底部
                {
                    outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
                } else if (columbIndex == spanCount - 1)// 最后一列，则不需要绘制右边
                {
                    outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
                } else
                {
                    outRect.set(0, 0, mDivider.getIntrinsicWidth(),mDivider.getIntrinsicHeight());
                }
            }
        }

    }

    /**
     * 根据dataSource获取数据源的行数
     * @param spanCount
     * @return
     */
    private int getTotalDataRow(int spanCount) {
        int dataCount = adapter.getData().size();
        int totalDataRow = 0;
        if(dataCount % spanCount == 0){
            totalDataRow = dataCount/spanCount;
        }
        else {
            totalDataRow = dataCount/spanCount + 1;
        }
        return totalDataRow;
    }

    /**
     * 根据getChildAdapterPosition获取item所在的行数
     * @param position
     * @param parent
     * @return
     */
    private int getRowIndex(int position, RecyclerView parent) {
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
            GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
            int spanCount = layoutManager.getSpanCount();
            if(layoutManager.canScrollVertically()){
                return spanSizeLookup.getSpanGroupIndex(position, spanCount);
            }
            else {
                return spanSizeLookup.getSpanIndex(position, spanCount);
            }
        }
        return -1;
    }

    private int getColumnIndex(int position, RecyclerView parent) {
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
            GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
            int spanCount = layoutManager.getSpanCount();
            if(layoutManager.canScrollVertically()){
                return spanSizeLookup.getSpanIndex(position, spanCount);
            }
            else {
                return spanSizeLookup.getSpanGroupIndex(position, spanCount);
            }

        }
//        if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
//            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) parent.getLayoutManager();
//            layoutManager.
//            GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
//            int spanCount = layoutManager.getSpanCount();
//            return spanSizeLookup.getSpanIndex(position, spanCount);
//        }
        return -1;
    }
}
