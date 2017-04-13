package me.ghui.v2ex.htmlpicker.bind;

import org.jsoup.nodes.Element;

import javax.annotation.Nullable;

import me.ghui.v2ex.htmlpicker.HtmlPicker;
import me.ghui.v2ex.htmlpicker.PickerAdapter;
import me.ghui.v2ex.htmlpicker.PickerAdapterFactory;
import me.ghui.v2ex.htmlpicker.reflect.TypeToken;

/**
 * Created by ghui on 13/04/2017.
 */

public final class ReflectivePickerAdapterFactory implements PickerAdapterFactory {

	@Override
	public <T> PickerAdapter<T> create(HtmlPicker picker, TypeToken<T> typeToken) {
		return null;
	}

	public static final class Adapter<T> extends PickerAdapter<T> {

		@Override
		public T read(Element element, @Nullable Picker picker) {
			return null;
		}
	}

}
