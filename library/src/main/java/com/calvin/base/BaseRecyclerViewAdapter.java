package com.calvin.base;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by calvin on 2016/1/20 10:26.
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewHolder> {
    protected static String TAG = null;
    private Context context;
    protected RecyclerView recyclerView;
    protected List<T> data;
    protected int layoutResId = -1;
    protected OnItemClickListener<T> onItemClickListener;

    protected boolean multiTypeItemSupport;
    protected SparseArrayCompat<View> multiTypeItems;

    public BaseRecyclerViewAdapter(){
        //do not use;
        this(null,-1);
    }

    public BaseRecyclerViewAdapter(@LayoutRes int layoutResId){
        this(null,layoutResId);
    }

    public BaseRecyclerViewAdapter(List<T> data,@LayoutRes int layoutResId){
        TAG = ((Object) this).getClass().getSimpleName();
        this.layoutResId = layoutResId;
        this.data = data == null ? new ArrayList<T>() : data;
    }

    /**
     * If isSupport is true ,Please override <strong>{@link #getLayoutResId(int)}</strong> and <strong>{@link #getItemViewMultiType(int, Object)}</strong>
     * @param isSupport default is false
     */
    public BaseRecyclerViewAdapter(boolean isSupport){
        this(null,isSupport);
    }

    /**
     * If isSupport is true ,Please override <strong>{@link #getLayoutResId(int)}</strong> and <strong>{@link #getItemViewMultiType(int, Object)}</strong>
     * @param isSupport default is false
     */
    public BaseRecyclerViewAdapter(List<T> data,boolean isSupport){
        TAG = ((Object) this).getClass().getSimpleName();
        this.data = data == null ? new ArrayList<T>() : data;
        multiTypeItemSupport = isSupport;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = null;
        if(multiTypeItemSupport){
            int layoutId = getLayoutResId(viewType);
            if (layoutId == -1) {
                throw new RuntimeException("can not find a valid layout Resource file, Plz override getLayoutResId(viewType) method for a mutliType adapter!");
            }
            view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
        }
        else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutResId, viewGroup, false);
        }
        BaseRecyclerViewHolder viewHolder = new BaseRecyclerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final BaseRecyclerViewHolder viewHolder, final int position) {
        viewHolder.itemView.setTag(position);
        final T item = getItem(position);
        if (item != null) {
            if (isEnabled(getItemViewType(position))) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(viewHolder.itemView, item, position);
                        }
                    }
                });
            }
            onBind(viewHolder, position, item);
        }
    }

    /**
     * onBindViewHolder
     */
    public abstract void onBind(BaseRecyclerViewHolder viewHolder, int position, T item);

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        context = recyclerView.getContext();
        this.recyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
    }

    public boolean isEnabled(int viewType){
        return true;
    }

    public void add(T item){
        data.add(item);
        notifyItemInserted(data.size());
    }

    public void add(int position , T item){
        data.add(position, item);
        notifyItemInserted(position);
    }

    public void addAll(List<T> list) {
        if (list != null && !list.isEmpty()){
            int oldSize = data.size();
            data.addAll(list);
            notifyItemRangeInserted(oldSize, list.size());
        }
    }

    public void replaceAll(List<T> list) {
        if (list != null && !list.isEmpty()){
            data.clear();
            data.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void replaceAllNew(List<T> list) {
        if (list != null && !list.isEmpty()) {
            int previousSize = data.size();
            data.clear();
            notifyItemRangeRemoved(0, previousSize);
            data.addAll(list);
            notifyItemRangeInserted(0, list.size());
        }
    }

    public void remove(T item){
        int pos = data.indexOf(item);
        if(pos >= 0){
            remove(pos);
        }
    }

    public void remove(int position){
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void clearAll(){
        data.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        if (multiTypeItemSupport) {
            return getItemViewMultiType(position, getItem(position));
        }
        return super.getItemViewType(position);
    }

    public T getItem(int position){
        if(position > data.size() || position < 0 || data.isEmpty()){
            return null;
        }
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return  data.size();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<T> getData() {
        return data;
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

    /**
     * when using a multiType adapter, override this method to <strong>get the layout xml file for the specific viewType</strong>
     */
    public @LayoutRes int getLayoutResId(int viewType){
        return -1;
    }

    /**
     * when using a multiType adapter, override this method to <strong>{@link #getItemViewType(int position)}</strong>
     */
    public int getItemViewMultiType(int position, T item){
        return 0;
    }

}
