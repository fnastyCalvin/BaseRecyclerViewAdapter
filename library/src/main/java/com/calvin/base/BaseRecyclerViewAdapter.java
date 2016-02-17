package com.calvin.base;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangtao on 2016/1/20 10:26.
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewHolder> {
    protected static String TAG = null;
    private Context context;
    protected List<T> data;
    protected int layoutResId;

    protected OnItemClickListener<T> onItemClickListener;

    private boolean multiTypeItemSupport;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    //head & footer
    protected List<View> headerViews;

    protected List<View> footerViews;

    private RecyclerView.LayoutManager mLayoutManager;

    protected static final int TYPE_HEADER = 1111111;
    protected static final int TYPE_FOOTER = 2222222;
    protected static final int TYPE_ITEM = 333333333;
    //head & footer

    public BaseRecyclerViewAdapter(@LayoutRes int layoutResId){
        this(null,layoutResId);
    }

    public BaseRecyclerViewAdapter(List<T> data,@LayoutRes int layoutResId){
        TAG = ((Object) this).getClass().getSimpleName();
        this.layoutResId = layoutResId;
        this.data = data == null ? new ArrayList<T>() : data;
        if(headerViews == null){
            headerViews = new ArrayList<>();
        }
        if(footerViews == null){
            footerViews = new ArrayList<>();
        }
    }

    public BaseRecyclerViewAdapter(List<T> data,boolean multiType){
        this.data = data == null ? new ArrayList<T>() : data;
        if(headerViews == null){
            headerViews = new ArrayList<>();
        }
        if(footerViews == null){
            footerViews = new ArrayList<>();
        }
        multiTypeItemSupport = multiType;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType == TYPE_HEADER || viewType == TYPE_FOOTER){
            FrameLayout frameLayout = new FrameLayout(viewGroup.getContext());
            //make sure it fills the space
            /*if(mLayoutManager instanceof LinearLayoutManager){
                if(mLayoutManager.canScrollVertically()){
                    frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
                else {
                    frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
            }*/
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return new HeaderFooterViewHolder(frameLayout);
        }
        else {
            View view = null;
            if(multiTypeItemSupport){
                int layoutId = getLayoutResId(viewType);
                view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
            }
            else {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutResId, viewGroup, false);
            }
            BaseRecyclerViewHolder viewHolder = new BaseRecyclerViewHolder(view);
            return viewHolder;
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
            View v = footerViews.get(position- data.size()- headerViews.size());
            bindHeadFootViewHolder((HeaderFooterViewHolder) viewHolder, v);
        }
        else {
            //real pos in dataSource
            final int pos = getRealPosition(viewHolder);
            viewHolder.itemView.setTag(position);
            final T item = getItem(pos);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(viewHolder.itemView, item, pos);
                    }
                }
            });
            onBind(viewHolder, pos, item);
        }
    }

    /**
     * head or foot view 最好指定宽高，以免不显示
     * @param viewHolder
     * @param v
     */
    private void bindHeadFootViewHolder(HeaderFooterViewHolder viewHolder, View v) {

        if(viewHolder.convertView.getTag() != null){
//            Log.d(TAG,"show head");
            viewHolder.convertView.setVisibility(View.VISIBLE);
//            if(viewHolder.convertView.getMeasuredWidth() <= 0 || viewHolder.convertView.getMeasuredHeight() <= 0){
//                throw new RuntimeException("head or foot view better set exactly height and width");
//            }
        }
        else {
//            Log.d(TAG,"bind head");
            //if the view already belongs to another layout, remove it
            if(v.getParent() != null){
                ((ViewGroup)v.getParent()).removeView(v);
            }
            viewHolder.convertView.removeAllViews();
            viewHolder.convertView.addView(v);
//            viewHolder.convertView.measure(0,0);
            viewHolder.convertView.setTag(v);
        }

    }

    /**
     * onBindViewHolder
     * @param viewHolder
     * @param position  real position in dataSource, excluding head size
     * @param item
     */
    public abstract void onBind(BaseRecyclerViewHolder viewHolder, int position, T item);

    /**
     * 获取真实的position（如果含有head）
     * @param viewHolder
     * @return
     */
    private int getRealPosition(BaseRecyclerViewHolder viewHolder) {
        if(headerViews != null && !headerViews.isEmpty()){
            return viewHolder.getAdapterPosition() - headerViews.size();
        }
        else {
            return viewHolder.getAdapterPosition();
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        context = recyclerView.getContext();
        super.onAttachedToRecyclerView(recyclerView);
        mLayoutManager = recyclerView.getLayoutManager();
        if(mLayoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) mLayoutManager);
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
            if(holder.getLayoutPosition() < headerViews.size() || holder.getLayoutPosition() >= headerViews.size() + data.size())
            {
                p.setFullSpan(true);
            }
            else {
                p.setFullSpan(false);
            }
        }
    }

    public void add(T item){
        data.add(item);
        notifyItemInserted(data.size() + getHeadersCount());
    }

    public void add(int position , T item){
        data.add(position, item);
        notifyItemInserted(position + getHeadersCount());
    }

    public void addAll(List<T> source){
        data.addAll(source);
        notifyItemRangeInserted(data.size() + getHeadersCount(), source.size());
    }


    public void remove(T item){
        int pos = data.indexOf(item);
        if(pos >= 0){
            remove(pos);
        }
    }

    public void remove(int position){
        data.remove(position);
        notifyItemRemoved(position + getHeadersCount());
    }

    // TODO: 2016/1/20  加入head和foot的判断
    public void move(int from , int to){
        data.add(to, data.remove(from));
        notifyItemMoved(from, to);
    }

    public void clearAll(){
        data.clear();
        notifyDataSetChanged();
    }

    //add a header to the adapter
    public void addHeader(View header){
        if(!headerViews.contains(header)){
            headerViews.add(header);
            notifyItemInserted(headerViews.size() - 1);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            }, 200);
        }
    }

    //remove a header from the adapter
    public void removeHeader(View header){
        if(headerViews.contains(header)){
            notifyItemRemoved(headerViews.indexOf(header));
            headerViews.remove(header);
            notifyDataSetChanged();
        }
    }

    //add a footer to the adapter
    public void addFooter(View footer){
        if(!footerViews.contains(footer)){
            footerViews.add(footer);
            notifyItemInserted(headerViews.size() + data.size() + footerViews.size() - 1);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            }, 200);
        }
    }

    //remove a footer from the adapter
    public void removeFooter(View footer){
        if(footerViews.contains(footer)) {
            notifyItemRemoved(headerViews.size() + data.size() + footerViews.indexOf(footer));
            footerViews.remove(footer);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position < headerViews.size()){
            return TYPE_HEADER;
        }else if(position >= headerViews.size() + data.size()){
            return TYPE_FOOTER;
        }
        else {
            if (multiTypeItemSupport) {
                return getItemViewMultiType(position, getItem(position));
            }
            return TYPE_ITEM;
        }
    }

    public T getItem(int position){
        if(position > data.size() || position < 0){
            return null;
        }
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * @return the count of all items including head and foot
     */
    @Override
    public int getItemCount() {
        return getHeadersCount() + data.size() + getFooterCount();
    }

    /**
     * @return only the count of dataSource
     */
    public int getRealItemCount() {
        return  data.size() ;
    }

    public Context getContext() {
        return context;
    }

    public List<T> getData() {
        return data;
    }

    public List<View> getHeaderViews() {
        return headerViews;
    }

    public int getHeadersCount() {
        return headerViews.size();
    }

    public List<View> getFooterViews() {
        return footerViews;
    }

    public int getFooterCount() {
        return footerViews.size();
    }

    public static class HeaderFooterViewHolder extends BaseRecyclerViewHolder {
        FrameLayout convertView;

        public HeaderFooterViewHolder(View itemView) {
            super(itemView);
            this.convertView = (FrameLayout) itemView;
        }
    }

    public interface OnItemClickListener<T> {
        void onItemClick(View view, T item, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    /**
     * If isSupport is true ,Please override <strong>{@link #getLayoutResId(int)}</strong> and <strong>{@link #getItemViewMultiType(int, Object)}</strong>
     * @param isSupport default is false
     */
    public void setMultiTypeItemSupport(boolean isSupport) {
        this.multiTypeItemSupport = isSupport;
    }

    public @LayoutRes int getLayoutResId( int viewType){
        return -1;
    }

    public int getItemViewMultiType(int position, T item){
        return 0;
    }

}
