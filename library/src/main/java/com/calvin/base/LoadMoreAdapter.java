package com.calvin.base;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
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
    private static final int ITEM_TYPE_NO_MORE_LOAD = Integer.MAX_VALUE - 2;

    private final BaseRecyclerViewAdapter wrappedAdapter;

    private OnLoadMoreListener onLoadMoreListener;

    private int loadMoreLayoutId,noMoreLoadViewId;
    private boolean needDisplayLoadMore = true;//whether or not display the load more view
    private boolean isNoMore2Load; //whether or not display no more load view

    public LoadMoreAdapter(BaseRecyclerViewAdapter wrappedAdapter) {
        this(wrappedAdapter,R.layout.loadmore_progress);
    }

    public LoadMoreAdapter(BaseRecyclerViewAdapter wrappedAdapter,@LayoutRes int layoutId) {
        this(wrappedAdapter,layoutId,-1);
    }

    public LoadMoreAdapter(BaseRecyclerViewAdapter wrappedAdapter,@LayoutRes int layoutId,@LayoutRes int noMoreLoadViewId) {
        this.wrappedAdapter = wrappedAdapter;
        this.loadMoreLayoutId = layoutId;
        this.noMoreLoadViewId = noMoreLoadViewId;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_LOAD_MORE )
        {
            return createLoadMoreViewHolder(parent,loadMoreLayoutId);
        }
        else if (viewType == ITEM_TYPE_NO_MORE_LOAD ){
            return createLoadMoreViewHolder(parent, noMoreLoadViewId);
        }
        return wrappedAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: cout->"+getItemCount());
        if (getItemViewType(position) == ITEM_TYPE_LOAD_MORE) {
            if (needShowLoadMoreView(position) && needDisplayLoadMore) {
                //show load more view
                needDisplayLoadMore = false;
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                    }
                }, 200);
            }
        }
        else if (getItemViewType(position) == ITEM_TYPE_NO_MORE_LOAD) {
            if (needShowLoadMoreView(position) && isNoMore2Load) {
                //show no anymore data to load view
                isNoMore2Load = false;
            }
        } else {
            if (position > wrappedAdapter.getItemCount() - 1) return;
            wrappedAdapter.onBindViewHolder(holder, position);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        if (loadMoreLayoutId <= 0){
            throw new RuntimeException("need a load more layout xml");
        }
        this.recyclerView = recyclerView;
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
            if(needShowLoadMoreView(holder.getAdapterPosition()) || needShowNoMoreLoadView(holder.getAdapterPosition()))
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
        return loadMoreLayoutId > 0;
    }

    private boolean hasNoMoreLoadView()
    {
        return noMoreLoadViewId > 0;
    }

    private boolean needShowLoadMoreView(int position)
    {
        if (wrappedAdapter.getItemCount() < 1) return false;
        return hasLoadMoreView() && !isNoMore2Load && (position == wrappedAdapter.getItemCount() );
    }

    private boolean needShowNoMoreLoadView(int position)
    {
        if (wrappedAdapter.getItemCount() < 1) return false;
        return hasNoMoreLoadView() && isNoMore2Load && (position == wrappedAdapter.getItemCount() );
    }

    @Override
    public int getItemViewType(int position)
    {
        if (needShowLoadMoreView(position))
        {
            return ITEM_TYPE_LOAD_MORE;
        }
        else if (needShowNoMoreLoadView(position)){
            return ITEM_TYPE_NO_MORE_LOAD;
        }
        return wrappedAdapter.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (wrappedAdapter.getItemCount() < 1 ) return 0;
        if (isNoMore2Load){
            return wrappedAdapter.getItemCount() + (hasNoMoreLoadView() && isNoMore2Load ? 1 : 0);
        }
        else {
            return wrappedAdapter.getItemCount() + (hasLoadMoreView() && needDisplayLoadMore ? 1 : 0);
        }
    }


    private static class LoadMoreViewHolder extends BaseRecyclerViewHolder {
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

    /**
     * notifyDataSetChanged
     */
    public void endLoadMore() {
        needDisplayLoadMore = true;
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    /**
     * set a footer view that shown nothing to load
     * @param layoutId
     */
    public void setNoMoreLoadViewId(@LayoutRes int layoutId){
        noMoreLoadViewId = layoutId;
    }

    /**
     * there is nothing to load anymore.
     * @param isNoMore2Load
     */
    public void setNoMore(boolean isNoMore2Load) {
        if (isNoMore2Load){
            this.isNoMore2Load = true;
            needDisplayLoadMore = false;
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
//                    notifyItemRemoved(getItemCount()-1);
                }
            });

        }
    }
}
