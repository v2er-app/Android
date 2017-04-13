package me.ghui.v2ex.network.converter;


import java.io.IOException;
import java.lang.reflect.Type;

import me.ghui.v2ex.htmlpicker.HtmlPicker;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by ghui on 11/04/2017.
 */

public class HtmlResponseBodyConverter<T> implements Converter<ResponseBody, T> {

	private HtmlPicker mPicker;
	private Type mType;

	public HtmlResponseBodyConverter(HtmlPicker htmlPicker, Type type) {
		mPicker = htmlPicker;
		mType = type;
	}

	@Override
	public T convert(ResponseBody value) throws IOException {
		return mPicker.fromHtml(value.string(), mType);
	}
}
