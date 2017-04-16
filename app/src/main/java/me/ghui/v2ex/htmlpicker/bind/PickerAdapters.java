package me.ghui.v2ex.htmlpicker.bind;

import org.jsoup.nodes.Element;

import me.ghui.v2ex.htmlpicker.HtmlPicker;
import me.ghui.v2ex.htmlpicker.PickerAdapter;
import me.ghui.v2ex.htmlpicker.PickerAdapterFactory;
import me.ghui.v2ex.htmlpicker.annotations.Select;
import me.ghui.v2ex.htmlpicker.reflect.TypeToken;

/**
 * Created by ghui on 13/04/2017.
 */

public final class PickerAdapters {

	private PickerAdapters() {
		throw new UnsupportedOperationException();
	}

	private static final PickerAdapter<String> STRING = new PickerAdapter<String>() {
		@Override
		public String read(Element element, Select select) {
			String value = null;
			if (select != null) {
				element = element.select(select.value()).first();
				String attr = select.attr();
				if ("text".equals(attr)) {
					value = element.text();
				} else if ("ownText".equals(attr)) {
					value = element.ownText();
				} else {
					value = element.attr(attr);
				}
			}
			return value;
		}
	};

	public static final PickerAdapterFactory STRING_FACTORY = newFactory(String.class, STRING);

	public static final PickerAdapterFactory COLLECTION_FACTORY = new CollectionPickerAdapterFactory();

	public static final ReflectivePickerAdapterFactory REFLECTIVE_ADAPTER = new ReflectivePickerAdapterFactory();

//**************************************************************************************************

	public static <T> PickerAdapterFactory newFactory(final Class<T> type, final PickerAdapter<T> adapter) {
		return new PickerAdapterFactory() {
			@SuppressWarnings("unchecked")
			@Override
			public <TT> PickerAdapter<TT> create(HtmlPicker picker, TypeToken<TT> typeToken) {
				return typeToken.getRawType() == type ? (PickerAdapter<TT>) adapter : null;
			}
		};
	}

}
