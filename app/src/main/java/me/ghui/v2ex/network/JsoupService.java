package me.ghui.v2ex.network;

import com.github.florent37.retrojsoup.RetroJsoup;

import okhttp3.OkHttpClient;

/**
 * Created by ghui on 04/04/2017.
 */

public class JsoupService {

	private static IJsoupService sJSOUP_SERVICE;

	public static void init() {
		if (sJSOUP_SERVICE == null) {
			sJSOUP_SERVICE = new RetroJsoup.Builder()
					.url(Constants.BASE_URL)
					.client(new OkHttpClient())
					.build()
					.create(IJsoupService.class);
		}
	}

	public static IJsoupService get() {
		return sJSOUP_SERVICE;
	}

}
