package com.iplds.minimintji.iplds.manager;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iplds.minimintji.iplds.manager.http.ApiServiceGMS;
import com.iplds.minimintji.iplds.manager.http.ApiServiceParka;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpManager {
    private static HttpManager instance;

    private Context mContext;
    private static ApiServiceParka serviceParka = null;
    private static ApiServiceGMS serviceGms = null;
    private String url_parka = "https://applicationserver.parka028.me/";
    private String url_gms = "https://gms.parka028.me/";

    public static HttpManager getInstance(){
        if (instance == null)
            instance = new HttpManager();
        return instance;
    }

    private HttpManager(){
        mContext = Contextor.getInstance().getContext();

        Gson gson = new GsonBuilder()
                .setDateFormat("dd/MM/yyyy HH:MM:SS")
                .create();

        Retrofit retrofitParka = new Retrofit.Builder()
                .baseUrl(url_parka)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Retrofit retrofitGms = new Retrofit.Builder()
                .baseUrl(url_gms)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        serviceParka = retrofitParka.create(ApiServiceParka.class);
        serviceGms = retrofitGms.create(ApiServiceGMS.class);
    }

    public ApiServiceParka getServiceParka(){
        return serviceParka;
    }

    public ApiServiceGMS getServiceGMS() {
        return serviceGms;
    }

}
