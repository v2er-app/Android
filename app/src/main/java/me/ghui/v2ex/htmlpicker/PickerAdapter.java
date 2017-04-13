package me.ghui.v2ex.htmlpicker;

import org.jsoup.nodes.Element;

/**
 * Created by ghui on 13/04/2017.
 */

public abstract class PickerAdapter<T> {

	public abstract T read(Element element);

//	public abstract void write(T value);

}
