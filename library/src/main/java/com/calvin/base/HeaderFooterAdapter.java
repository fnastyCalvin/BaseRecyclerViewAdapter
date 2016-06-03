package com.calvin.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * add Header & Footer for RecyclerView
 * Created by calvin on 2016/6/1 11:39.
 */
public class HeaderFooterAdapter extends RecyclerView.Adapter<BaseRecyclerViewHolder>{

    private static final String TAG = HeaderFooterAdapter.class.getSimpleName();
    private RecyclerView.Adapter<BaseRecyclerViewHolder> wrappedAdapter;
    private Context context;
    //header & footer
    protected List<View> headerViews = new ArrayList<>();

    protected List<View> footerViews = new ArrayList<>();

    protected static final int TYPE_HEADER = 0x111111;
    protected static final int TYPE_FOOTER = 0x222222;
    protected static final int TYPE_ITEM   = 0x000000;
    private RecyclerView recyclerView;
    //header & footer end

    public HeaderFooterAdapter(RecyclerView.Adapter<BaseRecyclerViewHolder> wrappedAdapter) {
        setAdapter(wrappedAdapter);
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType == TYPE_HEADER || viewType == TYPE_FOOTER){
            FrameLayout frameLayout = new FrameLayout(viewGroup.getContext());
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new HeaderFooterViewHolder(frameLayout);
        }
        else {
            return wrappedAdapter.onCreateViewHolder(viewGroup,viewType);
        }
    }

    @Override
    public void onBindViewHolder(final BaseRecyclerViewHolder viewHolder, final int position) {
        //add head view
        if(getItemViewType(position) == TYPE_HEADER ){
            View v = headerViews.get(position);
            bindHeadFootViewHolder((HeaderFooterViewHolder) viewHolder, v);
        }
        else if(getItemViewType(position) == TYPE_FOOTER)
        {
            View v = footerViews.get(position - wrappedAdapter.getItemCount() - headerViews.size());
            bindHeadFootViewHolder((HeaderFooterViewHolder) viewHolder, v);
        }
        else {
            if (wrappedAdapter instanceof BaseRecyclerViewAdapter){
                BaseRecyclerViewAdapter baseAdapter = (BaseRecyclerViewAdapter) wrappedAdapter;
                if (baseAdapter.getContext() == null){
                    baseAdapter.setContext(this.context);
                }
            }
            int realPos = getRealPosition(viewHolder);
            wrappedAdapter.onBindViewHolder(viewHolder,realPos);
        }
    }

     /**
     * get position for wrapperAdapter while headers exist
     */
    private int getRealPosition(BaseRecyclerViewHolder viewHolder) {
        if(getHeaderViewsCount() > 0){
            return viewHolder.getAdapterPosition() - headerViews.size();
        }
        else {
            return viewHolder.getAdapterPosition();
        }
    }

    /**
     *  better to specify the width and height
     */
    private void bindHeadFootViewHolder(HeaderFooterViewHolder viewHolder, View v) {
        if(viewHolder.convertView.getTag() != null){
            viewHolder.convertView.setVisibility(View.VISIBLE);
            if(viewHolder.convertView.getMeasuredWidth() <= 0 || viewHolder.convertView.getMeasuredHeight() <= 0){
                throw new RuntimeException("head or foot view better set exactly height and width");
            }
        }
        else {
            //if the view already belongs to another layout, remove it
            if(v.getParent() != null){
                ((ViewGroup)v.getParent()).removeView(v);
            }
            viewHolder.convertView.removeAllViews();
            viewHolder.convertView.addView(v);
            viewHolder.convertView.setTag(v);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position < getHeaderViewsCount()){
            return TYPE_HEADER;
        }else if(position >= getHeaderViewsCount() + wrappedAdapter.getItemCount()){
            return TYPE_FOOTER;
        }
        else {
            int realPos = position - getHeaderViewsCount();
            if (realPos < wrappedAdapter.getItemCount()) {
                return wrappedAdapter.getItemViewType(realPos);
            }
            return TYPE_ITEM;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        context = recyclerView.getContext();
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) layoutManager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == TYPE_ITEM ? 1 : gridManager.getSpanCount();
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(BaseRecyclerViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if(lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams)
        {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            if(holder.getLayoutPosition() < headerViews.size() || holder.getLayoutPosition() >= headerViews.size() + wrappedAdapter.getItemCount())
            {
                p.setFullSpan(true);
            }
            else {
                p.setFullSpan(false);
            }
        }
    }

    /**
     * add a header to the adapter
     */
    public void addHeaderView(@NonNull View header){
        if(!headerViews.contains(header)){
            if (!headerViews.isEmpty()) {
                notifyItemInserted(headerViews.size() - 1);
            }
            else {
                notifyItemInserted(0);
//                recyclerView.smoothScrollToPosition(0);
            }
            headerViews.add(header);
        }
    }

    /**
     * remove a header to the adapter
     */
    public void removeHeaderView(@NonNull View header){
        if(headerViews.contains(header)){
            notifyItemRemoved(headerViews.indexOf(header));
            headerViews.remove(header);
        }
    }

    /**
     * add a footer to the adapter
     */
    public void addFooterView(@NonNull View footer){
        if(!footerViews.contains(footer)){
            footerViews.add(footer);
            notifyItemInserted(headerViews.size() + wrappedAdapter.getItemCount() + footerViews.size() - 1);
        }
    }

    /**
     * remove a footer to the adapter
     */
    public void removeFooterView(@NonNull View footer){
        if(footerViews.contains(footer)) {
            notifyItemRemoved(headerViews.size() + wrappedAdapter.getItemCount() + footerViews.indexOf(footer));
            footerViews.remove(footer);
        }
    }

    public void setAdapter(@NonNull RecyclerView.Adapter<BaseRecyclerViewHolder> adapter) {
        if (!(adapter instanceof RecyclerView.Adapter)){
            throw new RuntimeException("adapter must be a RecyclerView.Adapter<BaseRecyclerViewHolder> or BaseRecyclerViewAdapter");
        }
        if (wrappedAdapter != null) {
            notifyItemRangeRemoved(getHeaderViewsCount(), wrappedAdapter.getItemCount());
            wrappedAdapter.unregisterAdapterDataObserver(dataObserver);
        }

        this.wrappedAdapter = adapter;
        wrappedAdapter.registerAdapterDataObserver(dataObserver);
        notifyItemRangeInserted(getHeaderViewsCount(), wrappedAdapter.getItemCount());
    }

    private RecyclerView.AdapterDataObserver dataObserver = new RecyclerView.AdapterDataObserver() {

        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            notifyItemRangeChanged(positionStart + getHeaderViewsCount(), itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            notifyItemRangeInserted(positionStart + getHeaderViewsCount(), itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            notifyItemRangeRemoved(positionStart + getHeaderViewsCount(), itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            int headerViewsCountCount = getHeaderViewsCount();
            notifyItemRangeChanged(fromPosition + headerViewsCountCount, toPosition + headerViewsCountCount + itemCount);
        }
    };

    /**
     * @return the count of all items including head and foot
     */
    @Override
    public int getItemCount() {
        return getHeaderViewsCount() + wrappedAdapter.getItemCount() + getFooterViewsCount();
    }

    public List<View> getHeaderViews() {
        return headerViews;
    }

    public int getHeaderViewsCount() {
        return headerViews.size();
    }

    public List<View> getFooterViews() {
        return footerViews;
    }

    public int getFooterViewsCount() {
        return footerViews.size();
    }

    public static class HeaderFooterViewHolder extends BaseRecyclerViewHolder {
        FrameLayout convertView;

        public HeaderFooterViewHolder(View itemView) {
            super(itemView);
            this.convertView = (FrameLayout) itemView;
        }
    }
}
