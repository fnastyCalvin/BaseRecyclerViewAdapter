package com.calvin.base;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by calvin on 2016/6/3 10:47.
 */
public abstract class BaseSelectableAdapter extends BaseRecyclerViewAdapter<BaseRecyclerViewHolder>{

    private SparseBooleanArray selectedItems;
    private RecyclerView recyclerView;

    public BaseSelectableAdapter() {
        selectedItems = new SparseBooleanArray();
    }

    @Override
    public void onBind(BaseRecyclerViewHolder viewHolder, int position, BaseRecyclerViewHolder item) {
        if (getSelectedItemCount() > 0){

        }
        else {

        }
        onBindView(viewHolder,position,item);
    }

    protected abstract void onBindView(BaseRecyclerViewHolder viewHolder, int position, BaseRecyclerViewHolder item);

    public boolean isSelected(int position) {
        return getSelectedItems().contains(position);
    }

    public void toggleSelection(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    public void clearAllSelection() {
        List<Integer> selection = getSelectedItems();
        selectedItems.clear();
        for (Integer i : selection) {
            notifyItemChanged(i);
        }
    }

    public void setAllSelectable(boolean isSelectable) {
        for (int i = 0; i < recyclerView.getAdapter().getItemCount(); i++) {
            /*RecyclerView.ViewHolder holder = recyclerView.findViewHolderForPosition(i);
            if (holder != null) {
                ((SwappingHolder)holder).setSelectable(isSelectable);
            }*/
        }
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); ++i) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }
}
