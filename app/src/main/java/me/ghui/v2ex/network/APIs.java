package me.ghui.v2ex.network;

import java.util.Map;

import io.reactivex.Observable;
import me.ghui.v2ex.network.bean.CareInfo;
import me.ghui.v2ex.network.bean.DailyHotInfo;
import me.ghui.v2ex.network.bean.LoginParam;
import me.ghui.v2ex.network.bean.LoginResultInfo;
import me.ghui.v2ex.network.bean.NewsInfo;
import me.ghui.v2ex.network.bean.NodeInfo;
import me.ghui.v2ex.network.bean.NodeStarInfo;
import me.ghui.v2ex.network.bean.NodesInfo;
import me.ghui.v2ex.network.bean.NodesNavInfo;
import me.ghui.v2ex.network.bean.NotificationInfo;
import me.ghui.v2ex.network.bean.TopicInfo;
import me.ghui.v2ex.network.bean.TopicStarInfo;
import me.ghui.v2ex.network.bean.UserPageInfo;
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

    @Html
    @GET("/notifications")
    Observable<NotificationInfo> notifications(@Query("p") int page);

    @Html
    @GET("/my/following")
    Observable<CareInfo> specialCareInfo(@Query("p") int page);

    @Html
    @GET("/my/topics")
    Observable<TopicStarInfo> topicStarInfo(@Query("p") int page);

    @Html
    @GET("/my/nodes")
    Observable<NodeStarInfo> nodeStarInfo();

    @Html
    @GET("/")
    Observable<NodesNavInfo> nodesNavInfo();

    @Json
    @GET("/api/nodes/show.json")
    Observable<NodeInfo> nodeInfo(@Query("name") String name);

    @Html
    @GET("/go/{node}")
    Observable<NodesInfo> nodesInfo(@Path("node") String node);

    @Html
    @GET("/member/{user}")
    Observable<UserPageInfo> userPageInfo(@Path("user") String username);

}
