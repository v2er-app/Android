package me.ghui.v2ex.htmlpicker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.lang.reflect.Type;

import me.ghui.v2ex.htmlpicker.reflect.TypeToken;

/**
 * Created by ghui on 13/04/2017.
 */

public class HtmlPicker {


	public <T> T fromHtml(String html, Class<T> classOfT) {
		return fromHtml(html, (Type) classOfT);
	}

	public <T> T fromHtml(String html, Type typeOfT) {
		return fromHtml(Jsoup.parse(html), typeOfT);
	}

	@SuppressWarnings("unchecked")
	public <T> T fromHtml(Element element, Type typeOfT) {
		TypeToken<T> typeToken = (TypeToken<T>) TypeToken.get(typeOfT);
		PickerAdapter<T> pickerAdapter = getAdapter(typeToken);
		return pickerAdapter.read(element);
	}

	public <T> PickerAdapter<T> getAdapter(Class<T> type) {
		return getAdapter(TypeToken.get(type));
	}

	public <T> PickerAdapter<T> getAdapter(TypeToken<T> typeToken) {
		// TODO: 13/04/2017
		return null;
	}


}
