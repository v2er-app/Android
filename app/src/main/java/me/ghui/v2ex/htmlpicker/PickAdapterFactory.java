package me.ghui.v2ex.htmlpicker;

import me.ghui.v2ex.htmlpicker.reflect.TypeToken;

/**
 * Created by ghui on 13/04/2017.
 */

public interface PickAdapterFactory {

	<T> PickAdapter<T> create(Fruit picker, TypeToken<T> typeToken);
}
