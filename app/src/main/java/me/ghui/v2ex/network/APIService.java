package me.ghui.v2ex.network;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
					.addInterceptor(sLOG_INTERCEPTOR)
					.build();
			Retrofit retrofit = new Retrofit.Builder()
					.client(httpClient)
					.addConverterFactory(GsonConverterFactory.create())
					.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
					.baseUrl(Constants.BASE_URL)
					.build();
			mAPI_SERVICE = retrofit.create(IServiceAPI.class);
		}
	}

	public static IServiceAPI get() {
		return mAPI_SERVICE;
	}

	private static final Interceptor sLOG_INTERCEPTOR = new Interceptor() {
		@Override
		public Response intercept(Chain chain) throws IOException {
			final Request request = chain.request();
			String logStr = "Request{method="
					+ request.method()
					+ ", url="
					+ request.url()
					+ ", tag="
					+ (request.tag() != request ? request.tag() : null)
					+ ", body="
					+ request.body()
					+ '}';
			Logger.i(logStr);
			return chain.proceed(request);
		}
	};


}
