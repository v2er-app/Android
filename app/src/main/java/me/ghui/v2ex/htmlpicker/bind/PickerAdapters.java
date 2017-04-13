package me.ghui.v2ex.htmlpicker.bind;

import org.jsoup.nodes.Element;

import me.ghui.v2ex.htmlpicker.HtmlPicker;
import me.ghui.v2ex.htmlpicker.PickerAdapter;
import me.ghui.v2ex.htmlpicker.PickerAdapterFactory;
import me.ghui.v2ex.htmlpicker.reflect.TypeToken;

/**
 * Created by ghui on 13/04/2017.
 */

public class PickerAdapters {


	private static final PickerAdapter<String> STRING = new PickerAdapter<String>() {
		@Override
		public String read(Element element, Picker picker) {
			if (picker != null) {
				return element.select(picker.getPath()).first().attr(picker.getAttr());
			}
			return null;
		}
	};

	public static final PickerAdapterFactory STRING_FACTORY = newFactory(String.class, STRING);



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
