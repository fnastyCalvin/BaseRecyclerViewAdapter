package com.calvin.baserecyclerviewadapter.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.calvin.base.BaseRecyclerViewAdapter;
import com.calvin.base.HeaderFooterAdapter;
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
public class MultiTypeWithHeaderDemoFragment extends Fragment {
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
        final HeaderFooterAdapter headerAdapter = new HeaderFooterAdapter(adapter);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        StaggeredGridLayoutManager staggeredGridLayoutManager2 = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.HORIZONTAL);


        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(headerAdapter);

        final View headView = getHeadView("http://ww1.sinaimg.cn/large/43823ba4gw1f06wqlup6fj20sg0g0adi.jpg");
        final View footView = getHeadView("http://ww1.sinaimg.cn/large/43823ba4gw1f06wql1ancj20sg0g0tcg.jpg");
        headerAdapter.addHeaderView(headView);
        headerAdapter.addFooterView(footView);


        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<MultiTypeModel>() {
            @Override
            public void onItemClick(View view, MultiTypeModel item, int position) {
                Toast.makeText(getActivity(),"click item on position--->"+position,Toast.LENGTH_SHORT).show();
            }
        });
        Button btn_add = (Button) root.findViewById(R.id.btn_add);
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
