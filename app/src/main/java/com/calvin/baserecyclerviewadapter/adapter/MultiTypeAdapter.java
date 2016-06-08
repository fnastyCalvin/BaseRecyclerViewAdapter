package com.calvin.baserecyclerviewadapter.adapter;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.calvin.base.BaseRecyclerViewAdapter;
import com.calvin.base.BaseRecyclerViewHolder;
import com.calvin.baserecyclerviewadapter.model.MultiTypeModel;
import com.calvin.baserecyclerviewadapter.R;

/**
 * Created by jiangtao on 2016/6/2 15:18.
 */
public class MultiTypeAdapter extends BaseRecyclerViewAdapter<MultiTypeModel>
{

    private static final int VIEW_TYPE_1 = 782;
    private static final int VIEW_TYPE_2 = 533;
    
    public MultiTypeAdapter(boolean multiTypeSupport){
        super(multiTypeSupport);
    }

    @Override
    public void onBind(BaseRecyclerViewHolder viewHolder,final int position, MultiTypeModel item) {
        viewHolder.getTextView(R.id.tv_item_normal_title).setText(item.title);
        viewHolder.getTextView(R.id.tv_item_normal_detail).setText(item.detail);
        Glide.with(getContext()).load(item.avatorPath).placeholder(R.mipmap.holder).error(R.mipmap.holder).into(viewHolder.getImageView(R.id.iv_item_normal_avator));
        viewHolder.getTextView(R.id.tv_item_normal_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"remove item on position--->"+position,Toast.LENGTH_SHORT).show();
                remove(position);
            }
        });
        int viewType = getItemViewMultiType(position,item);
        if (viewType == VIEW_TYPE_1){

        }
        else if (viewType == VIEW_TYPE_2){
            viewHolder.getTextView(R.id.tv_item_type).setText("annother type --"+item.type);
        }
        else {
            Log.e(TAG, "onBind: error item type");
        }
    }

    @Override
    public int getLayoutResId(int viewType) {
        if (viewType == VIEW_TYPE_1){
            return R.layout.item_normal;
        }
        else if (viewType == VIEW_TYPE_2){
            return R.layout.item_type2;
        }
        else {
            Log.e(TAG, "getLayoutResId: error item type");
            return -1;
        }
    }


    @Override
    public int getItemViewMultiType(int position, MultiTypeModel item) {
        if (item.type == 0){
            return VIEW_TYPE_1;
        }
        else if (item.type == 1){
            return VIEW_TYPE_2;
        }
        else {
            return -1;
        }
    }
}
