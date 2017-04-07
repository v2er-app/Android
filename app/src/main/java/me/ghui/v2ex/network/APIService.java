package me.ghui.v2ex.network;

import java.util.concurrent.TimeUnit;

import me.ghui.v2ex.network.converter.Html;
import me.ghui.v2ex.network.converter.HtmlConverterFactory;
import me.ghui.v2ex.network.converter.Json;
import me.ghui.v2ex.network.converter.GlobalConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ghui on 25/03/2017.
 */

public class APIService {

	private static final long TIMEOUT_LENGTH = 30;
	private static IServiceAPI mAPI_SERVICE;

	public static void init() {
		if (mAPI_SERVICE == null) {
			OkHttpClient httpClient = new OkHttpClient.Builder()
					.connectTimeout(TIMEOUT_LENGTH, TimeUnit.SECONDS)
					.retryOnConnectionFailure(true)
					.addInterceptor(new HttpLoggingInterceptor()
							.setLevel(HttpLoggingInterceptor.Level.BODY))
					.build();

			GlobalConverterFactory factory = new GlobalConverterFactory();
			factory.addConverterFactory(GsonConverterFactory.create(), Json.class);
			factory.addConverterFactory(HtmlConverterFactory.create(), Html.class);

			Retrofit retrofit = new Retrofit.Builder()
					.client(httpClient)
					.addConverterFactory(factory)
					.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
					.baseUrl(Constants.BASE_URL)
					.build();
			mAPI_SERVICE = retrofit.create(IServiceAPI.class);
		}
	}

	public static IServiceAPI get() {
		return mAPI_SERVICE;
	}


}
