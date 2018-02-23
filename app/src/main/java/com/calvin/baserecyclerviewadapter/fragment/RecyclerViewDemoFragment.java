package com.calvin.baserecyclerviewadapter.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.calvin.base.BaseRecyclerViewAdapter;
import com.calvin.base.LoadMoreAdapter;
import com.calvin.baserecyclerviewadapter.ApiEngine;
import com.calvin.baserecyclerviewadapter.App;
import com.calvin.baserecyclerviewadapter.R;
import com.calvin.baserecyclerviewadapter.adapter.NormalAdapter;
import com.calvin.baserecyclerviewadapter.model.NormalModel;

import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by jiangtao on 2016/6/1 15:32.
 */
public class RecyclerViewDemoFragment extends Fragment {
    private static final String TAG = "RecyclerViewDemo";
    View root;
    RecyclerView recyclerView;
    NormalAdapter adapter;
    LoadMoreAdapter loadMoreAdapter;
    int page = 1;

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
//                adapter.replaceAll(response.body());
                List<NormalModel> data = response.body();
                Log.d(TAG, "onResponse: data size -->"+data.size()+"  page==>"+page);
                adapter.addAll(data);
                loadMoreAdapter.endLoadMore();
                page++;
                    if (page > 3){
                        loadMoreAdapter.setNoMore();
                    }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getActivity(),"数据加载失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        adapter = new NormalAdapter(R.layout.item_normal);
        recyclerView = root.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        loadMoreAdapter = new LoadMoreAdapter(adapter);
        loadMoreAdapter.setNoMoreLoadViewId(R.layout.no_more);
        loadMoreAdapter.setOnLoadMoreListener(new LoadMoreAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.d(TAG, "onLoadMore: ");
                initData();
            }
        });
        recyclerView.setAdapter(loadMoreAdapter);
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<NormalModel>() {
            @Override
            public void onItemClick(View view, NormalModel item, int position) {
                Toast.makeText(getActivity(),"click item on position--->"+position,Toast.LENGTH_SHORT).show();
            }
        });
    }


}
