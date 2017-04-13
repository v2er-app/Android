package me.ghui.v2ex.htmlpicker;

import me.ghui.v2ex.htmlpicker.reflect.TypeToken;

/**
 * Created by ghui on 13/04/2017.
 */

public interface PickerAdapterFactory {

	<T> PickerAdapter<T> create(HtmlPicker picker, TypeToken<T> typeToken);
}
