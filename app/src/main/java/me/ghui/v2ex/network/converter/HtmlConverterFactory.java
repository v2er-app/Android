package me.ghui.v2ex.network.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import me.ghui.fruit.Fruit;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by ghui on 07/04/2017.
 */

public class HtmlConverterFactory extends Converter.Factory {

	private Fruit mPicker;

	public static HtmlConverterFactory create(Fruit fruit) {
		return new HtmlConverterFactory(fruit);
	}

	public static HtmlConverterFactory create() {
		return new HtmlConverterFactory(new Fruit());
	}

	private HtmlConverterFactory(Fruit fruit) {
		mPicker = fruit;
	}

	@Override
	public Converter<ResponseBody, ?> responseBodyConverter(
			Type type, Annotation[] annotations, Retrofit retrofit) {
		return new HtmlResponseBodyConverter<>(mPicker, type);
	}

}
