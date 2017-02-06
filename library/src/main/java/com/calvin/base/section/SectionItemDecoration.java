package com.calvin.base.section;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;

import java.util.List;

/**
 * 一种section的实现方式，adapter直接使用BaseRecyclerViewAdapter
 * Created by calvin on 2016/8/31 15:27.
 */
public class SectionItemDecoration extends RecyclerView.ItemDecoration{

    private List<? extends Section> mDatas;
    private Paint mPaint;
    private Rect mBounds;//用于存放测量文字Rect

    private int mSectionHeight;//section字体大小
    private static int COLOR_SECTION_BG = Color.parseColor("#FFc1de23");
    private static int COLOR_SECTION_FONT = Color.parseColor("#FF000000");
    private static int mSectionFontSize;//section字体大小

    public SectionItemDecoration(Context context, List<? extends Section> datas) {
        super();
        mDatas = datas;
        mPaint = new Paint();
        mBounds = new Rect();
        mSectionHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics());
        mSectionFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, context.getResources().getDisplayMetrics());
        mPaint.setTextSize(mSectionFontSize);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int position = params.getViewLayoutPosition();
            if (position > RecyclerView.NO_POSITION) {
                if (position == 0) {
                    drawSectionArea(c, left, right, child, params, position);

                } else {
                    if (!TextUtils.isEmpty(mDatas.get(position).sectionName) && !mDatas.get(position).sectionName.equals(mDatas.get(position - 1).sectionName)) {
                        drawSectionArea(c, left, right, child, params, position);
                    } else {
                        //none
                    }
                }
            }
        }
    }


    private void drawSectionArea(Canvas c, int left, int right, View child, RecyclerView.LayoutParams params, int position) {//最先调用，绘制在最下层
        mPaint.setColor(COLOR_SECTION_BG);
        c.drawRect(left, child.getTop() - params.topMargin - mSectionHeight, right, child.getTop() - params.topMargin, mPaint);
        mPaint.setColor(COLOR_SECTION_FONT);

        mPaint.getTextBounds(mDatas.get(position).sectionName, 0, mDatas.get(position).sectionName.length(), mBounds);
        c.drawText(mDatas.get(position).sectionName, left + child.getPaddingLeft(), child.getTop() - params.topMargin - (mSectionHeight / 2 - mBounds.height() / 2), mPaint);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {//最后调用 绘制在最上层
        int pos = ((LinearLayoutManager)(parent.getLayoutManager())).findFirstVisibleItemPosition();

        String tag = mDatas.get(pos).sectionName;
        View child = parent.getChildAt(pos);
        mPaint.setColor(COLOR_SECTION_BG);
        c.drawRect(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getRight() - parent.getPaddingRight(), parent.getPaddingTop() + mSectionHeight, mPaint);
        mPaint.setColor(COLOR_SECTION_FONT);
        mPaint.getTextBounds(tag, 0, tag.length(), mBounds);
        c.drawText(tag, parent.getPaddingLeft() + child.getPaddingLeft() ,parent.getPaddingTop() + mSectionHeight - (mSectionHeight / 2 - mBounds.height() / 2),
                mPaint);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        if (position > RecyclerView.NO_POSITION) {
            if (position == 0) {
                outRect.set(0, mSectionHeight, 0, 0);
            } else
            {
                if (!TextUtils.isEmpty(mDatas.get(position).sectionName) && !mDatas.get(position).sectionName.equals(mDatas.get(position - 1).sectionName)) {
                    outRect.set(0, mSectionHeight, 0, 0);
                } else {
                    outRect.set(0, 0, 0, 0);
                }
            }
        }
    }
}
