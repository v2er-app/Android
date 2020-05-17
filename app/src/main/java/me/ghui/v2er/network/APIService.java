package me.ghui.v2er.network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import me.ghui.fruit.Fruit;
import me.ghui.fruit.converter.retrofit.FruitConverterFactory;
import me.ghui.retrofit.converter.GlobalConverterFactory;
import me.ghui.retrofit.converter.annotations.Html;
import me.ghui.retrofit.converter.annotations.Json;
import me.ghui.v2er.BuildConfig;
import me.ghui.v2er.util.Check;
import me.ghui.v2er.util.L;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by ghui on 25/03/2017.
 */

public class APIService {
    public static final String WAP_USER_AGENT = "Mozilla/5.0 (Linux; Android 9.0; V2er Build/OPD3.170816.012) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Mobile Safari/537.36";
    public static final String WEB_USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4; V2er) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36";
    public static final String UA_KEY = "user-agent";

    public static final long TIMEOUT_LENGTH = 10;
    private static APIs mAPI_SERVICE;
    private static Gson sGson;
    private static Fruit sFruit;
    private static WebkitCookieManagerProxy sCookieJar;
    private static OkHttpClient sHttpClient;


    public static void init() {
        if (mAPI_SERVICE == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(httpClient())
                    .addConverterFactory(GlobalConverterFactory
                            .create()
                            .add(FruitConverterFactory.create(fruit()), Html.class)
                            .add(GsonConverterFactory.create(gson()), Json.class))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(Constants.BASE_URL)
                    .build();
            mAPI_SERVICE = retrofit.create(APIs.class);
        }
    }

    public static APIs get() {
        return mAPI_SERVICE;
    }

    public static Gson gson() {
        if (sGson == null) {
            sGson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();
        }
        return sGson;
    }

    public static Fruit fruit() {
        if (sFruit == null) {
            sFruit = new Fruit();
        }
        return sFruit;
    }

    public static OkHttpClient httpClient() {
        if (sHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT_LENGTH, TimeUnit.SECONDS)
                    .cookieJar(cookieJar())
                    .retryOnConnectionFailure(true)
                    .addInterceptor(new ConfigInterceptor());
            if (BuildConfig.DEBUG) {
                builder.addInterceptor(new HttpLoggingInterceptor(L::v)
                        .setLevel(HttpLoggingInterceptor.Level.BODY));
            }
            sHttpClient = builder.build();
        }
        return sHttpClient;
    }

    public static WebkitCookieManagerProxy cookieJar() {
        if (sCookieJar == null) {
//            sCookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(App.get()));
            sCookieJar = new WebkitCookieManagerProxy();
        }
        return sCookieJar;
    }

    private static class ConfigInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String ua = request.header(UA_KEY);
            if (Check.isEmpty(ua)) {
                request = request.newBuilder()
                        .addHeader("user-agent", WAP_USER_AGENT)
                        .build();
            }
            try {
                return chain.proceed(request);
            } catch (Exception e) {
                e.printStackTrace();
                return new Response.Builder()
                        .protocol(Protocol.HTTP_1_1)
                        .code(404)
                        .message("Exeception when execute chain.proceed request")
                        .body(new ResponseBody() {
                            @Nullable
                            @Override
                            public MediaType contentType() {
                                return null;
                            }

                            @Override
                            public long contentLength() {
                                return 0;
                            }

                            @Override
                            public BufferedSource source() {
                                return null;
                            }
                        })
                        .request(request).build();
            }
        }
    }

}
