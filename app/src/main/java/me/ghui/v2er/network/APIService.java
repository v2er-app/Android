package me.ghui.v2er.network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import me.ghui.fruit.Fruit;
import me.ghui.v2er.network.converter.GlobalConverterFactory;
import me.ghui.v2er.network.converter.HtmlConverterFactory;
import me.ghui.v2er.network.converter.annotations.Html;
import me.ghui.v2er.network.converter.annotations.Json;
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

    public static String USER_AGENT = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N)" +
            " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Mobile Safari/537.36";
    private static final long TIMEOUT_LENGTH = 2 * 60;
    private static APIs mAPI_SERVICE;
    private static Gson sGson;
    private static Fruit sFruit;
    private static WebkitCookieManagerProxy sCookieJar;


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
                            .add(HtmlConverterFactory.create(fruit()), Html.class))
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

    public static Fruit fruit() {
        if (sFruit == null) {
            sFruit = new Fruit();
        }
        return sFruit;
    }

    public static WebkitCookieManagerProxy cookieJar() {
        if (sCookieJar == null) {
//            sCookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(App.get()));
            sCookieJar = new WebkitCookieManagerProxy();
        }
        return sCookieJar;
    }

}
