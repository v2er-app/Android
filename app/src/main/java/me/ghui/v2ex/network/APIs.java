package me.ghui.v2ex.network;

import java.util.Map;

import io.reactivex.Observable;
import me.ghui.v2ex.network.bean.DailyHotInfo;
import me.ghui.v2ex.network.bean.LoginParam;
import me.ghui.v2ex.network.bean.LoginResultInfo;
import me.ghui.v2ex.network.bean.NewsInfo;
import me.ghui.v2ex.network.bean.TopicInfo;
import me.ghui.v2ex.network.converter.annotations.Html;
import me.ghui.v2ex.network.converter.annotations.Json;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by ghui on 05/05/2017.
 */

public interface APIs {
    @Json
    @GET("/api/topics/hot.json")
    Observable<DailyHotInfo> dailyHot();

    @Html
    @GET("/")
    Observable<NewsInfo> homeNews(@Query("tab") String tab);

    @Html
    @GET("/recent")
    Observable<NewsInfo> recentNews(@Query("p") int page);

    @Html
    @GET("/signin")
    Observable<LoginParam> loginParam();

    @Html
    @FormUrlEncoded
    @Headers("Referer: " + Constants.BASE_URL + "/signin")
    @POST("/signin")
    Observable<LoginResultInfo> login(@FieldMap Map<String, String> loginParams);

    @Html
    @GET("/t/{id}")
    Observable<TopicInfo> topicDetails(@Path("id") String topicId, @Query("p") int page);
}
