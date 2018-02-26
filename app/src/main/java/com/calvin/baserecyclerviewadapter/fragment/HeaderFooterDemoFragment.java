package com.calvin.baserecyclerviewadapter.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.calvin.base.BaseRecyclerViewAdapter;
import com.calvin.base.HeaderFooterAdapter;
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
 * Created by jiangtao on 2016/6/1 15:59.
 */
public class HeaderFooterDemoFragment extends Fragment {
    private static final String TAG = "HeaderFooterDemoFragmen";
    View root;
    RecyclerView recyclerView;
    Button btn_add;
    NormalAdapter normalAdapter;
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
//                normalAdapter.replaceAll(response.body());
                List<NormalModel> data = response.body();
                Log.d(TAG, "onResponse: data size -->"+data.size()+"  page==>"+page);
                normalAdapter.addAll(data);
                loadMoreAdapter.endLoadMore();
                page++;
                if (page >= 5){
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
        normalAdapter = new NormalAdapter(R.layout.item_normal);
        final HeaderFooterAdapter headerAdapter = new HeaderFooterAdapter(normalAdapter);
        recyclerView = root.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        StaggeredGridLayoutManager staggeredGridLayoutManager2 = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.HORIZONTAL);


        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        loadMoreAdapter = new LoadMoreAdapter(headerAdapter);
        loadMoreAdapter.setNoMoreLoadViewId(R.layout.no_more);
        loadMoreAdapter.setOnLoadMoreListener(new LoadMoreAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                initData();
            }
        });

        recyclerView.setAdapter(loadMoreAdapter);

        final View headView = getHeadView("http://ww1.sinaimg.cn/large/43823ba4gw1f06wqlup6fj20sg0g0adi.jpg");
        final View footView = getHeadView("http://ww1.sinaimg.cn/large/43823ba4gw1f06wql1ancj20sg0g0tcg.jpg");

        final View headView2 = getHeadView("http://ww1.sinaimg.cn/large/43823ba4gw1f06wqlup6fj20sg0g0adi.jpg");
        final View footView2 = getHeadView("http://ww1.sinaimg.cn/large/43823ba4gw1f06wql1ancj20sg0g0tcg.jpg");

        headerAdapter.addHeaderView(headView);
        headerAdapter.addFooterView(footView);
        headerAdapter.addHeaderView(headView2);
        headerAdapter.addFooterView(footView2);


        normalAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<NormalModel>() {
            @Override
            public void onItemClick(View view, NormalModel item, int position) {
                Toast.makeText(getActivity(),"click item on position--->"+position,Toast.LENGTH_SHORT).show();
            }
        });
        btn_add = (Button) root.findViewById(R.id.btn_add);
        Button btn_remove = (Button) root.findViewById(R.id.btn_remove);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headerAdapter.addHeaderView(headView);
                headerAdapter.addFooterView(footView);
            }
        });
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headerAdapter.removeHeaderView(headView);
                headerAdapter.removeFooterView(footView);
            }
        });
    }

    @NonNull
    private View getHeadView(String url) {
        View head =  View.inflate(getContext(), R.layout.head1, null);
        ImageView img1 = (ImageView) head.findViewById(R.id.img1);
        Glide.with(getContext()).load(url).into(img1);
        return head;
    }

    @NonNull
    private View getHeadViewHorizontal(String url) {
        View head =  View.inflate(getContext(), R.layout.head_horizontal, null);
        ImageView img1 = (ImageView) head.findViewById(R.id.img1);
        Glide.with(getContext()).load(url).into(img1);
        return head;
    }

}
