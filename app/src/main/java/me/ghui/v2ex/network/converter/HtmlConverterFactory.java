package me.ghui.v2ex.network.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by ghui on 07/04/2017.
 */

public class HtmlConverterFactory extends Converter.Factory {

	public static HtmlConverterFactory create() {
		return new HtmlConverterFactory();
	}

	private HtmlConverterFactory() {

	}


	@Override
	public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
		// TODO: 11/04/2017
		//HtmlPicker.getAdapter(type);
		//Adapter 负责将 Element解析成具体的类
		return new HtmlResponseBodyConverter<>();
	}

}
