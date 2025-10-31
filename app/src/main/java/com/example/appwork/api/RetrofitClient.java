package com.example.appwork.api;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

//Retrofit客户端
public class RetrofitClient {
    private static final String NEWS_BASE_URL = "http://v.juhe.cn/toutiao/";
    private static final String LOGIN_BASE_URL = "http://192.168.137.1:8080/";
    private static final String API_KEY = "6b45f91b76e22d567c43ccb884723fe8";

    private static RetrofitClient instance;
    private final ApiService newsApiService;
    private final ApiService loginApiService;

    private RetrofitClient() {
        // 创建日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // 新闻API的OkHttpClient
        OkHttpClient newsClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    HttpUrl originalHttpUrl = original.url();
                    HttpUrl url = originalHttpUrl.newBuilder()
                            .addQueryParameter("key", API_KEY)
                            .build();

                    Request request = original.newBuilder()
                            .url(url)
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                })
                .addInterceptor(loggingInterceptor)
                .build();

        // 登录API的OkHttpClient
        OkHttpClient loginClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        // 创建新闻API的Retrofit实例
        Retrofit newsRetrofit = new Retrofit.Builder()
                .baseUrl(NEWS_BASE_URL)
                .client(newsClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        // 创建登录API的Retrofit实例
        Retrofit loginRetrofit = new Retrofit.Builder()
                .baseUrl(LOGIN_BASE_URL)
                .client(loginClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        newsApiService = newsRetrofit.create(ApiService.class);
        loginApiService = loginRetrofit.create(ApiService.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    // 获取新闻API服务
    public ApiService getNewsApiService() {
        return newsApiService;
    }

    // 获取登录API服务
    public ApiService getLoginApiService() {
        return loginApiService;
    }
}
