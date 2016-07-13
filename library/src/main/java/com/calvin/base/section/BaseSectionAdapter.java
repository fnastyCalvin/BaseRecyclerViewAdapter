package com.calvin.base.section;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.calvin.base.BaseRecyclerViewAdapter;
import com.calvin.base.BaseRecyclerViewHolder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by calvin on 2016/6/3 10:45.
 */
public abstract class BaseSectionAdapter<T extends Section> extends BaseRecyclerViewAdapter<T> {
    private static final int TYPE_SECTION = 0;
    //key : sectionName,Value : the 1st display position of section
    private LinkedHashMap<String, Integer> sectionMap = new LinkedHashMap<>();

    public BaseSectionAdapter(@LayoutRes int layoutResId){
        this(null,layoutResId);
    }

    public BaseSectionAdapter(List<T> data, @LayoutRes int layoutResId){
        super(data,true);
        this.layoutResId = layoutResId;
        findSections();
        registerAdapterDataObserver(observer);
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_SECTION) {
            if (getSectionLayoutId() < 0)
                throw new RuntimeException("must specify a section layout xml");
            return new SectionViewHolder(createSectionView(viewGroup,getSectionLayoutId()));
        }
        else {
            return super.onCreateViewHolder(viewGroup, viewType + 1);
        }
    }

    @Override
    public void onBind(BaseRecyclerViewHolder viewHolder, int position, T item) {
        int positionInDataSource = getRealPosition(position);
        int viewType = getItemViewMultiType(position,item);
        if (viewType == TYPE_SECTION){
            if (viewHolder instanceof SectionViewHolder) {
                viewHolder.getTextView(android.R.id.title).setText(item.sectionName);
            }
        }
        else {
            onBindItemView(viewHolder,positionInDataSource,item);
        }
    }

    @Override
    public @LayoutRes int getLayoutResId(int viewType) {
        if (viewType == TYPE_SECTION){
            return getSectionLayoutId();
        }
        else {
            // the layout xml id of normal item
            return layoutResId;
        }
    }

    @Override
    public int getItemViewMultiType(int position, T item) {
        return sectionMap.values().contains(position) ? TYPE_SECTION : 1;
    }

    @Override
    public T getItem(int position) {
        return super.getItem(getRealPosition(position));
    }

    public void setData(@NonNull List<T> source){
        data = source;
//        registerAdapterDataObserver(observer);
//        findSections();
        notifyDataSetChanged();
    }

    public int getRealPosition(int position) {
        int nSections = 0;
        Set<Map.Entry<String, Integer>> entrySet = sectionMap.entrySet();
        for (Map.Entry<String, Integer> entry : entrySet)
        {
            if (entry.getValue() < position)
            {
                nSections++;
            }
        }
        return position - nSections;

    }

    @Override
    public boolean isEnabled(int viewType) {
        if (viewType == TYPE_SECTION){
            return false;
        }
        return super.isEnabled(viewType);
    }

    final RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver()
    {
        @Override
        public void onChanged()
        {
            super.onChanged();
            findSections();
        }
    };

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        unregisterAdapterDataObserver(observer);
    }

    public void findSections()
    {
        int size = data.size();
        int nSections = 0;
        sectionMap.clear();

        for (int i = 0; i < size; i++)
        {
            String sectionName = data.get(i).sectionName;
            if (!sectionMap.containsKey(sectionName))
            {
                sectionMap.put(sectionName, i + nSections);
                nSections++;
            }
        }

    }

    @Override
    public int getItemCount()
    {
        return data.size() + sectionMap.size();
    }

    public abstract @LayoutRes int getSectionLayoutId();

    public abstract void onBindItemView(BaseRecyclerViewHolder viewHolder, int position, T item);

    public static class SectionViewHolder extends BaseRecyclerViewHolder {
        private static final String  SECTION_TITLE = "SECTION_TITLE";
        private final TextView titleView;
        public SectionViewHolder(View itemView) {
            super(itemView);
            this.titleView = (TextView) itemView.findViewWithTag(SECTION_TITLE);
        }
    }

    private View createSectionView(ViewGroup parent, @LayoutRes int layoutResId) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        TextView titleView = (TextView) view.findViewById(android.R.id.title);
        if (titleView == null) {
            throw new RuntimeException("The section layout xml must contain a TextView with the id called \"android.R.id.title\".");
        }
        titleView.setTag(SectionViewHolder.SECTION_TITLE);
        return view;
    }
}
