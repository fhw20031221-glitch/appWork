package com.example.appwork.api;



import com.example.appwork.model.BaseResponse;
import com.example.appwork.model.NewsItem;
import com.example.appwork.model.NewsResponse;
import com.example.appwork.model.UserInfo;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

//接口定义类
public interface ApiService {
    @FormUrlEncoded
    @POST("login")
    Observable<BaseResponse<UserInfo>> login(
            @Field("username") String username,
            @Field("password") String password
    );

    @GET("index")
    Observable<NewsResponse> getNewsList(
            @Query("type") String type,     // 新闻类型
            @Query("page") int page,        // 当前页数
            @Query("page_size") int pageSize // 每页数量
    );
}