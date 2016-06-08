package com.calvin.baserecyclerviewadapter;

import android.app.Application;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class App extends Application {
    private static App instance;
    private Retrofit mRetrofit;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://7xk9dj.com1.z0.glb.clouddn.com/adapter/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static App getInstance() {
        return instance;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

}