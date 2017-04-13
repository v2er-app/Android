package me.ghui.v2ex.network.converter;


import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by ghui on 11/04/2017.
 */

public class HtmlResponseBodyConverter<T> implements Converter<ResponseBody, T> {


	@Override
	public T convert(ResponseBody value) throws IOException {

		return null;
	}
}
