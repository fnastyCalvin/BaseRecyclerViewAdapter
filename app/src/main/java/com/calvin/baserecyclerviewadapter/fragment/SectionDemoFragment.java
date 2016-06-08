package com.calvin.baserecyclerviewadapter.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.calvin.base.BaseRecyclerViewAdapter;
import com.calvin.baserecyclerviewadapter.R;
import com.calvin.baserecyclerviewadapter.adapter.SectionAdapter;
import com.calvin.baserecyclerviewadapter.model.SectionModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jiangtao on 2016/6/1 15:32.
 */
public class SectionDemoFragment extends Fragment {
    View root;
    RecyclerView recyclerView;
    SectionAdapter adapter;
    List<SectionModel> data = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_list_view, null);
        initView();
        initData();
        return root;
    }

    private void initData() {
        data.add(new SectionModel("消费10","04-20"));
        data.add(new SectionModel("消费11","05-11"));
        data.add(new SectionModel("消费12","06-06"));
        data.add(new SectionModel("消费13","01-08"));
        data.add(new SectionModel("消费14","01-23"));
        data.add(new SectionModel("消费1","02-20"));
        data.add(new SectionModel("消费21","05-10"));
        data.add(new SectionModel("消费22","05-09"));
        data.add(new SectionModel("消费33","04-08"));
        data.add(new SectionModel("消费44","03-23"));
        data.add(new SectionModel("消费50","03-20"));
        data.add(new SectionModel("消费61","02-11"));
        data.add(new SectionModel("消费72","02-09"));
        data.add(new SectionModel("消费83","01-08"));
        data.add(new SectionModel("消费94","02-23"));
        Collections.sort(data, new Comparator<SectionModel>() {
            @Override
            public int compare(SectionModel lhs, SectionModel rhs) {
                return (lhs.dateMill == rhs.dateMill) ? 0 : ((lhs.dateMill > rhs.dateMill) ? -1 : 1);
            }
        });
//        adapter.notifyDataSetChanged();
        adapter.setData(data);
    }

    private void initView() {
        adapter = new SectionAdapter(data,R.layout.section_normal_item);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<SectionModel>() {
            @Override
            public void onItemClick(View view, SectionModel item, int position) {
                Toast.makeText(getActivity(),"click item on position--->"+position,Toast.LENGTH_SHORT).show();
            }
        });
    }


}
