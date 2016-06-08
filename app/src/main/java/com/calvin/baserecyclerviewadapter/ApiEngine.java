package com.calvin.baserecyclerviewadapter;

import com.calvin.baserecyclerviewadapter.model.NormalModel;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;

public interface ApiEngine {
    @GET("normalModels.json")
    Call<List<NormalModel>> getNormalModels();
}