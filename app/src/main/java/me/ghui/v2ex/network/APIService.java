package me.ghui.v2ex.network;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import me.ghui.v2ex.network.bean.DailyHotInfo;
import me.ghui.v2ex.network.converter.GlobalConverterFactory;
import me.ghui.v2ex.network.converter.Html;
import me.ghui.v2ex.network.converter.HtmlConverterFactory;
import me.ghui.v2ex.network.converter.Json;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by ghui on 25/03/2017.
 */

public class APIService {

	private static final long TIMEOUT_LENGTH = 30;
	private static IApis mAPI_SERVICE;

	public static void init() {
		if (mAPI_SERVICE == null) {
			OkHttpClient httpClient = new OkHttpClient.Builder()
					.connectTimeout(TIMEOUT_LENGTH, TimeUnit.SECONDS)
					.retryOnConnectionFailure(true)
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

	public static IApis get() {
		return mAPI_SERVICE;
	}

	//************************ below is apis ************************

	public interface IApis {

		@Json
		@GET("api/topics/hot.json")
		Observable<DailyHotInfo> dailyHot();

	}


}
