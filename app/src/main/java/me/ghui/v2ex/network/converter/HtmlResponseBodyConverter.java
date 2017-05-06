package me.ghui.v2ex.network.converter;


import java.io.IOException;
import java.lang.reflect.Type;

import me.ghui.fruit.Fruit;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by ghui on 11/04/2017.
 */

public class HtmlResponseBodyConverter<T> implements Converter<ResponseBody, T> {

	private Fruit mPicker;
	private Type mType;

	public HtmlResponseBodyConverter(Fruit fruit, Type type) {
		mPicker = fruit;
		mType = type;
	}

	@Override
	public T convert(ResponseBody value) throws IOException {
		return mPicker.fromHtml(value.string(), mType);
	}
}
