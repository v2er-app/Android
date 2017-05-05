package me.ghui.v2ex.network;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import me.ghui.v2ex.general.App;
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


/**
 * Created by ghui on 25/03/2017.
 */

public class APIService {

    private static String USER_AGENT = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N)" +
            " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Mobile Safari/537.36";
    private static final long TIMEOUT_LENGTH = 30;
    private static APIs mAPI_SERVICE;
    private static Gson sGson;
    private static PersistentCookieJar sCookieJar;


    public static void init() {
        if (mAPI_SERVICE == null) {
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT_LENGTH, TimeUnit.SECONDS)
                    .cookieJar(cookieJar())
                    .retryOnConnectionFailure(true)
                    .addInterceptor(new ConfigInterceptor())
                    .addInterceptor(new HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .client(httpClient)
                    .addConverterFactory(GlobalConverterFactory
                            .create()
                            .add(GsonConverterFactory.create(gson()), Json.class)
                            .add(HtmlConverterFactory.create(), Html.class))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(Constants.BASE_URL)
                    .build();
            mAPI_SERVICE = retrofit.create(APIs.class);
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

    public static PersistentCookieJar cookieJar() {
        if (sCookieJar == null) {
            sCookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(App.get()));
        }
        return sCookieJar;
    }

}
