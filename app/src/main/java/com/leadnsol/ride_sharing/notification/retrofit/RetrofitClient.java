package com.leadnsol.ride_sharing.notification.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leadnsol.ride_sharing.app_common.AppConstant;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {
    private static Retrofit mRetrofit = null;

    public static Retrofit getClient() {
        if (mRetrofit == null) {

            Gson gson = new GsonBuilder().setLenient().create();
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(AppConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return mRetrofit;
    }
}
