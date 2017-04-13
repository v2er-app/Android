package me.ghui.v2ex.network.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import me.ghui.v2ex.htmlpicker.HtmlPicker;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by ghui on 07/04/2017.
 */

public class HtmlConverterFactory extends Converter.Factory {

	private HtmlPicker mPicker;

	public static HtmlConverterFactory create(HtmlPicker htmlPicker) {
		return new HtmlConverterFactory(htmlPicker);
	}

	public static HtmlConverterFactory create() {
		return new HtmlConverterFactory(new HtmlPicker());
	}

	private HtmlConverterFactory(HtmlPicker htmlPicker) {
		mPicker = htmlPicker;
	}

	@Override
	public Converter<ResponseBody, ?> responseBodyConverter(
			Type type, Annotation[] annotations, Retrofit retrofit) {
		return new HtmlResponseBodyConverter<>(mPicker, type);
	}

}
