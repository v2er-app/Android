package me.ghui.v2er.network;

import java.util.Map;

import io.reactivex.Observable;
import me.ghui.v2er.network.bean.BingSearchResultInfo;
import me.ghui.v2er.network.bean.CareInfo;
import me.ghui.v2er.network.bean.CreateTopicPageInfo;
import me.ghui.v2er.network.bean.DailyHotInfo;
import me.ghui.v2er.network.bean.LoginParam;
import me.ghui.v2er.network.bean.LoginResultInfo;
import me.ghui.v2er.network.bean.NewsInfo;
import me.ghui.v2er.network.bean.NodeInfo;
import me.ghui.v2er.network.bean.NodeStarInfo;
import me.ghui.v2er.network.bean.NodesInfo;
import me.ghui.v2er.network.bean.NodesNavInfo;
import me.ghui.v2er.network.bean.NotificationInfo;
import me.ghui.v2er.network.bean.SimpleInfo;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.network.bean.TopicStarInfo;
import me.ghui.v2er.network.bean.UserPageInfo;
import me.ghui.v2er.network.converter.annotations.Html;
import me.ghui.v2er.network.converter.annotations.Json;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

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
    Observable<NodesInfo> nodesInfo(@Path("node") String node, @Query("p") int page);

    @Html
    @GET("/member/{user}")
    Observable<UserPageInfo> userPageInfo(@Path("user") String username);

    @Html
    @GET
    Observable<BingSearchResultInfo> bingSearch(@Url String url);

    @Html
    @GET("/new")
    Observable<CreateTopicPageInfo> topicCreatePageInfo();

    @Html
    @FormUrlEncoded
    @POST("/new")
    Observable<CreateTopicPageInfo> postTopic(@FieldMap Map<String, String> postParams);

    @Html
    @POST("/thank/reply/{id}")
    Observable<SimpleInfo> thxReplier(@Path("id") String replyId, @Query("t") String t);

    @Html
    @POST("/ajax/money")
    Observable<SimpleInfo> thxMoney();

    @Html
    @POST("/favorite/topic/{id}")
    Observable<SimpleInfo> starTopic(@Path("id") String id, @Query("t") String string);

    @Html
    @POST("/up/topic/{id}")
    Observable<SimpleInfo> upTopic(@Path("id") String id, @Query("t") String string);

    @Html
    @POST("/down/topic/{id}")
    Observable<SimpleInfo> downTopic(@Path("id") String id, @Query("t") String string);

    @Html
    @FormUrlEncoded
    @POST("/t/{id}")
    Observable<SimpleInfo> replyTopic(@Path("id") String id, @FieldMap Map<String, String> replyMap);

}

}
