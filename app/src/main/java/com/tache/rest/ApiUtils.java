package com.tache.rest;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ujjwal on 8/13/16.
 */

public class ApiUtils {
    //public static final String BASEURL = "http://ec2-52-66-116-5.ap-south-1.compute.amazonaws.com:81/";
//     public static final String BASEURL = "http://192.168.1.14:8000/";
 //   public static final String BASEURL = "http://35.154.18.134:94/";
    //public static final String BASEURL = "http://ec2-13-233-195-121.ap-south-1.compute.amazonaws.com/";
   //  public static final String BASEURL = "http://52.66.195.101/";
    public static final String BASEURL = "http://13.233.186.205/";
    private static Retrofit retrofitInstance;

    public static Retrofit retrofitInstance() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .build();

        if (retrofitInstance == null) {
            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(BASEURL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitInstance;
    }
}
