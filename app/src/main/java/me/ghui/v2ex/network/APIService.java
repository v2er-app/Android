package me.ghui.v2ex.network;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import me.ghui.v2ex.general.App;
import me.ghui.v2ex.network.bean.DailyHotInfo;
import me.ghui.v2ex.network.bean.LoginParam;
import me.ghui.v2ex.network.bean.NewsInfo;
import me.ghui.v2ex.network.bean.SimpleInfo;
import me.ghui.v2ex.network.converter.GlobalConverterFactory;
import me.ghui.v2ex.network.converter.HtmlConverterFactory;
import me.ghui.v2ex.network.converter.annotations.Html;
import me.ghui.v2ex.network.converter.annotations.Json;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;


/**
 * Created by ghui on 25/03/2017.
 */

public class APIService {

    private static final long TIMEOUT_LENGTH = 30;
    private static IApis mAPI_SERVICE;
    private static String USER_AGENT = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Mobile Safari/537.36";

    public static void init() {
        if (mAPI_SERVICE == null) {
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT_LENGTH, TimeUnit.SECONDS)
                    .cookieJar(new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(App.get())))
                    .retryOnConnectionFailure(true)
                    .addInterceptor(new ConfigInterceptor())
                    .addInterceptor(new HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .client(httpClient)
                    .addConverterFactory(GlobalConverterFactory
                            .create()
                            .add(GsonConverterFactory.create(), Json.class)
                            .add(HtmlConverterFactory.create(), Html.class))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(Constants.BASE_URL)
                    .build();
            mAPI_SERVICE = retrofit.create(IApis.class);
        }
    }

    private static class ConfigInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("user-agent", USER_AGENT)
                    .build();
            return chain.proceed(request);
        }
    }

    public static IApis get() {
        return mAPI_SERVICE;
    }

    //************************ BELOW IS APIS ************************************************

    public interface IApis {

        @Json
        @GET("api/topics/hot.json")
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
        Observable<SimpleInfo> login(@FieldMap Map<String, String> loginParams);

    }


}
