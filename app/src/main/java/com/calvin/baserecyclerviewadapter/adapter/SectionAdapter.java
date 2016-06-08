package com.calvin.baserecyclerviewadapter.adapter;

import android.support.annotation.LayoutRes;

import com.calvin.base.BaseRecyclerViewHolder;
import com.calvin.base.section.BaseSectionAdapter;
import com.calvin.baserecyclerviewadapter.R;
import com.calvin.baserecyclerviewadapter.model.SectionModel;

import java.util.List;

/**
 * Created by calvin on 2016/6/7 15:25.
 */
public class SectionAdapter extends BaseSectionAdapter<SectionModel>{
    public SectionAdapter(@LayoutRes int layoutResId) {
        super(layoutResId);
    }

    public SectionAdapter(List<SectionModel> data, @LayoutRes int layoutResId) {
        super(data, layoutResId);
    }

    @Override
    public int getSectionLayoutId() {
        return R.layout.section_item;
    }

    @Override
    public void onBindItemView(BaseRecyclerViewHolder viewHolder, int position, SectionModel item) {
        viewHolder.getTextView(R.id.text_name).setText(item.name);
        viewHolder.getTextView(R.id.text_date).setText(item.date);
    }
}
