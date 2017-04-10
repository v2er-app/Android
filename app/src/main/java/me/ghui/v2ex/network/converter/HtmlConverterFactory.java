package me.ghui.v2ex.network.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by ghui on 07/04/2017.
 */

public class HtmlConverterFactory extends Converter.Factory {
	// TODO: 07/04/2017


	public static HtmlConverterFactory create() {
		return new HtmlConverterFactory();
	}

	private HtmlConverterFactory() {

	}


	@Override
	public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
		return super.responseBodyConverter(type, annotations, retrofit);
	}

	@Override
	public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
	                                                      Annotation[] methodAnnotations, Retrofit retrofit) {
		return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
	}
}
