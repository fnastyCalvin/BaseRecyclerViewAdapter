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
import com.calvin.baserecyclerviewadapter.ApiEngine;
import com.calvin.baserecyclerviewadapter.App;
import com.calvin.baserecyclerviewadapter.model.MultiTypeModel;
import com.calvin.baserecyclerviewadapter.model.NormalModel;
import com.calvin.baserecyclerviewadapter.R;
import com.calvin.baserecyclerviewadapter.adapter.MultiTypeAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by jiangtao on 2016/6/1 15:32.
 */
public class MultiTypeDemoFragment extends Fragment {
    View root;
    RecyclerView recyclerView;
    MultiTypeAdapter adapter;
    List<MultiTypeModel> data = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_list_view, null);
        initView();
        initData();
        return root;
    }

    private void initData() {
        App.getInstance().getRetrofit().create(ApiEngine.class).getNormalModels().enqueue(new Callback<List<NormalModel>>() {
            @Override
            public void onResponse(Response<List<NormalModel>> response, Retrofit retrofit) {
                for (int i = 0; i < response.body().size(); i++) {
                    NormalModel n = response.body().get(i);
                    int type = -1;
                    if ( i % 2 == 0 )
                    {
                        type = 0;
                    }
                    else {
                        type = 1;
                    }
                    MultiTypeModel m = new MultiTypeModel(n.title,n.detail,n.avatorPath,type);
                    data.add(m);
                }
                adapter.replaceAll(data);
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getActivity(),"数据加载失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        adapter = new MultiTypeAdapter(true);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<MultiTypeModel>() {
            @Override
            public void onItemClick(View view, MultiTypeModel item, int position) {
                Toast.makeText(getActivity(),"click item on position--->"+position,Toast.LENGTH_SHORT).show();
            }
        });
    }


}
