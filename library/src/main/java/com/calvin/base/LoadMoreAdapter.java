package com.calvin.base;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by calvin on 2016/7/12 11:23.
 */
public class LoadMoreAdapter extends RecyclerView.Adapter<BaseRecyclerViewHolder>{

    private static final String TAG = LoadMoreAdapter.class.getSimpleName();

    protected RecyclerView recyclerView;

    private static final int ITEM_TYPE_LOAD_MORE = Integer.MAX_VALUE - 1;
    private static final int ITEM_TYPE_NO_MORE_LOAD = Integer.MAX_VALUE - 2;

    private BaseRecyclerViewAdapter wrappedAdapter;

    private OnLoadMoreListener onLoadMoreListener;

    private int loadMoreLayoutId,noMoreLoadViewId;
    private View loadMoreView,noMoreLoadView;

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
        if (viewType == ITEM_TYPE_LOAD_MORE)
        {
            Log.d(TAG, "onCreateViewHolder: ITEM_TYPE_LOAD_MORE ");
            if (loadMoreLayoutId > 0) {
                return BaseRecyclerViewHolder.createViewHolder(parent, loadMoreLayoutId);
            }
            return BaseRecyclerViewHolder.createViewHolder(loadMoreView);
        }
        else if (viewType == ITEM_TYPE_NO_MORE_LOAD){
            Log.d(TAG, "onCreateViewHolder: ITEM_TYPE_NO_MORE_LOAD ");
            if (noMoreLoadViewId > 0) {
                return BaseRecyclerViewHolder.createViewHolder(parent, noMoreLoadViewId);
            }
            return BaseRecyclerViewHolder.createViewHolder(noMoreLoadView);
        }
        return wrappedAdapter.onCreateViewHolder(parent, viewType);
    }


    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: "+" loadmore adpater count ==="+getItemCount());
        if (position < wrappedAdapter.getItemCount()){
            wrappedAdapter.onBindViewHolder(holder, position);
        }
        else {
            if (getItemViewType(position) == ITEM_TYPE_LOAD_MORE) {
                if (needShowLoadMoreView(position) && needDisplayLoadMore) {
                    //show load more view
                    Log.d(TAG, "-------show load more view "+position+" loadmore adpater count ==="+getItemCount());
                    if (onLoadMoreListener != null) {
                        recyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onLoadMoreListener.onLoadMore();
                            }
                        }, 500);

                    }
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            needDisplayLoadMore = false;
                        }
                    }, 100);
                }
            }
            else if (getItemViewType(position) == ITEM_TYPE_NO_MORE_LOAD) {
                if (needShowLoadMoreView(position) && isNoMore2Load) {
                    //show no anymore data to load view
                    Log.d(TAG, "-------show no anymore load view "+position);
                    isNoMore2Load = false;
                }
            }
            else {
                Log.w(TAG, "onBindViewHolder: ---- unknown item type");
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
        wrappedAdapter.onAttachedToRecyclerView(recyclerView);
        makeFullSpan();
    }

    private void makeFullSpan() {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) layoutManager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    boolean isNormalItem = getItemViewType(position) != ITEM_TYPE_LOAD_MORE && getItemViewType(position) != ITEM_TYPE_NO_MORE_LOAD;
                    return  isNormalItem ? 1 : gridManager.getSpanCount();
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
        makeFullSpan();
    }

    private boolean hasLoadMoreView()
    {
        return loadMoreLayoutId > 0 || loadMoreView != null;
    }

    private boolean hasNoMoreLoadView()
    {
        return noMoreLoadViewId > 0 || noMoreLoadView != null;
    }

    private boolean needShowLoadMoreView(int position)
    {
        if (wrappedAdapter.getItemCount() < 1) return false;
        return hasLoadMoreView() && !isNoMore2Load && (position >= wrappedAdapter.getItemCount() );
    }

    private boolean needShowNoMoreLoadView(int position)
    {
        if (wrappedAdapter.getItemCount() < 1) return false;
        return hasNoMoreLoadView() && isNoMore2Load && (position >= wrappedAdapter.getItemCount() );
    }

    @Override
    public int getItemViewType(int position)
    {
        if (needShowLoadMoreView(position))
        {
            Log.d(TAG, "getItemViewType: ITEM_TYPE_LOAD_MORE");
            return ITEM_TYPE_LOAD_MORE;
        }
        else if (needShowNoMoreLoadView(position)){
            Log.d(TAG, "getItemViewType: ITEM_TYPE_NO_MORE_LOAD");
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

    public void setOnLoadMoreListener(OnLoadMoreListener listener)
    {
        if (listener != null)
        {
            this.onLoadMoreListener = listener;
        }
    }

    public interface OnLoadMoreListener
    {
        void onLoadMore();
    }

    public void setLoadMoreLayoutId(@LayoutRes int layoutId) {
        this.loadMoreLayoutId = layoutId;
    }

    public void setLoadMoreView(View loadMoreView) {
        this.loadMoreView = loadMoreView;
    }

    public void setNoMoreLoadView(View noMoreLoadView) {
        this.noMoreLoadView = noMoreLoadView;
    }

    /**
     * notifyDataSetChanged
     */
    public void endLoadMore() {
        needDisplayLoadMore = true;
        notifyDataSetChanged();
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
     */
    public void setNoMore() {
        this.isNoMore2Load = true;
        needDisplayLoadMore = false;
        notifyDataSetChanged();
        if (!hasNoMoreLoadView()) {
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                        notifyItemRemoved(getItemCount()-1);
                }
            });
        }
    }
}
