package me.ghui.v2ex.htmlpicker;

import org.jsoup.Jsoup;
import org.jsoup.helper.DataUtil;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.ghui.v2ex.htmlpicker.bind.PickerAdapters;
import me.ghui.v2ex.htmlpicker.reflect.TypeToken;

/**
 * Created by ghui on 13/04/2017.
 */

public class HtmlPicker {


	private static final TypeToken<?> NULL_KEY_SURROGATE = new TypeToken<Object>() {
	};
	private final Map<TypeToken<?>, PickerAdapter<?>> mTypeTokenCache = new ConcurrentHashMap<>();

	private final List<PickerAdapterFactory> mFactories;


	public HtmlPicker() {
		List<PickerAdapterFactory> factories = new ArrayList<>();
		factories.add(PickerAdapters.INTEGER_FACTORY);
		factories.add(PickerAdapters.STRING_FACTORY);
		factories.add(PickerAdapters.COLLECTION_FACTORY);
		factories.add(PickerAdapters.REFLECTIVE_ADAPTER);
		mFactories = Collections.unmodifiableList(factories);
	}

	public <T> T fromHtml(String html, Class<T> classOfT) {
		return fromHtml(html, (Type) classOfT);
	}

	public <T> T fromHtml(String html, Type typeOfT) {
		return fromHtml(Jsoup.parse(html), typeOfT);
	}

	public <T> T fromHtml(File file, String charsetName, String baseUri, Class<T> classOfT) throws IOException {
		Element element = DataUtil.load(file, charsetName, baseUri);
		return fromHtml(element, classOfT);
	}

	@SuppressWarnings("unchecked")
	public <T> T fromHtml(Element element, Type typeOfT) {
		TypeToken<T> typeToken = (TypeToken<T>) TypeToken.get(typeOfT);
		PickerAdapter<T> pickerAdapter = getAdapter(typeToken);
		return pickerAdapter.read(element, null);
	}

	public <T> PickerAdapter<T> getAdapter(Class<T> type) {
		return getAdapter(TypeToken.get(type));
	}

	@SuppressWarnings("unchecked")
	public <T> PickerAdapter<T> getAdapter(TypeToken<T> type) {
		PickerAdapter<?> cached = mTypeTokenCache.get(type == null ? NULL_KEY_SURROGATE : type);
		if (cached != null) {
			return (PickerAdapter<T>) cached;
		}

		for (PickerAdapterFactory factory : mFactories) {
			PickerAdapter<T> pickerAdapter = factory.create(this, type);
			if (pickerAdapter != null) {
				mTypeTokenCache.put(type, pickerAdapter);
				return pickerAdapter;
			}
		}
		throw new IllegalArgumentException("HtmlPicker cannot handle " + type);
	}
}
