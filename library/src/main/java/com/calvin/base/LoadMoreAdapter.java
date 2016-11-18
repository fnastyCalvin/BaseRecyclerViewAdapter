package com.calvin.base;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by calvin on 2016/7/12 11:23.
 */
public class LoadMoreAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewHolder>{

    private static final String TAG = LoadMoreAdapter.class.getSimpleName();

    protected RecyclerView recyclerView;

    private static final int ITEM_TYPE_LOAD_MORE = Integer.MAX_VALUE - 1;

    private final BaseRecyclerViewAdapter wrappedAdapter;

    private OnLoadMoreListener onLoadMoreListener;

    private int loadMoreLayoutId;
    private boolean displayLoadMore = true;//whether or not display the load more view

    public LoadMoreAdapter(BaseRecyclerViewAdapter wrappedAdapter) {
        this(wrappedAdapter,R.layout.loadmore_progress);
    }

    public LoadMoreAdapter(BaseRecyclerViewAdapter wrappedAdapter,@LayoutRes int layoutId) {
        this.wrappedAdapter = wrappedAdapter;
        this.loadMoreLayoutId = layoutId;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_LOAD_MORE && displayLoadMore)
        {
            return  createLoadMoreViewHolder(parent,loadMoreLayoutId);
        }
        return wrappedAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        if (getItemViewType(position) == ITEM_TYPE_LOAD_MORE) {
            if (needShowLoadMore(position) && displayLoadMore) {
                displayLoadMore = false;
                if (onLoadMoreListener != null) {
                    onLoadMoreListener.onLoadMore();
                }
            }
        }
        else {
            if (position > wrappedAdapter.getItemCount() - 1) return;
            wrappedAdapter.onBindViewHolder(holder, position);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        if (loadMoreLayoutId <= 0){
            throw new RuntimeException("need a load more layout xml");
        }
        super.onAttachedToRecyclerView(recyclerView);
        wrappedAdapter.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) layoutManager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) != ITEM_TYPE_LOAD_MORE ? gridManager.getSpanCount() : 1;
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
            if(needShowLoadMore(holder.getAdapterPosition()))
            {
                p.setFullSpan(true);
            }
            else {
                p.setFullSpan(false);
            }
        }
    }


    private boolean hasLoadMoreView()
    {
        return loadMoreLayoutId != 0;
    }

    private boolean needShowLoadMore(int position)
    {
        if (wrappedAdapter.getItemCount() < 1) return false;
        return hasLoadMoreView() && (position == wrappedAdapter.getItemCount() );
    }

    @Override
    public int getItemViewType(int position)
    {
        if (needShowLoadMore(position))
        {
            return ITEM_TYPE_LOAD_MORE;
        }
        return wrappedAdapter.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (wrappedAdapter.getItemCount() < 1 ) return 0;
        return wrappedAdapter.getItemCount() + ( hasLoadMoreView() && displayLoadMore ? 1 : 0);
    }

    public void setNoMore(boolean isNoMore2Load) {
        if (isNoMore2Load){
            displayLoadMore = false;
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    notifyItemRemoved(getItemCount()-1);
                }
            });
        }
    }

    public static class LoadMoreViewHolder extends BaseRecyclerViewHolder {
        public LoadMoreViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener)
    {
        if (listener != null)
        {
            this.onLoadMoreListener = listener;
        }
    }

    private LoadMoreViewHolder  createLoadMoreViewHolder(ViewGroup parent, @LayoutRes int layoutResId) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new LoadMoreViewHolder(view);
    }

    public interface OnLoadMoreListener
    {
        void onLoadMore();
    }

    public void setLoadMoreLayoutId(@LayoutRes int layoutId) {
        this.loadMoreLayoutId = layoutId;
    }

    public void endLoadMore() {
        displayLoadMore = true;
        notifyDataSetChanged();
    }
}
