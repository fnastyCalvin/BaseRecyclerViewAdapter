package com.calvin.baserecyclerviewadapter.adapter;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.calvin.base.BaseRecyclerViewAdapter;
import com.calvin.base.BaseRecyclerViewHolder;
import com.calvin.baserecyclerviewadapter.model.NormalModel;
import com.calvin.baserecyclerviewadapter.R;

/**
 * Created by jiangtao on 2016/6/1 15:49.
 */
public class NormalAdapter extends BaseRecyclerViewAdapter<NormalModel> {

    public NormalAdapter(@LayoutRes int layoutResId){
        super(null,layoutResId);
    }


    @Override
    public void onBind(BaseRecyclerViewHolder viewHolder, final int position, NormalModel item) {
        viewHolder.getTextView(R.id.tv_item_normal_title).setText(item.title);
        viewHolder.getTextView(R.id.tv_item_normal_detail).setText(item.detail);
        SwitchCompat btnSwitch = viewHolder.getView(R.id.cb_item_normal_status);
        btnSwitch.setChecked(item.selected);
        Glide.with(getContext()).load(item.avatorPath).placeholder(R.mipmap.holder).error(R.mipmap.holder).into(viewHolder.getImageView(R.id.iv_item_normal_avator));
        viewHolder.getTextView(R.id.tv_item_normal_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"remove item on position--->"+position,Toast.LENGTH_SHORT).show();
                remove(position);
            }
        });
    }


}
