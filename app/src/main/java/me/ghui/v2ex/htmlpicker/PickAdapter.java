package me.ghui.v2ex.htmlpicker;

import org.jsoup.nodes.Element;

import javax.annotation.Nullable;

import me.ghui.v2ex.htmlpicker.annotations.Pick;

/**
 * Created by ghui on 13/04/2017.
 */

public abstract class PickAdapter<T> {

	public abstract T read(Element element, @Nullable Pick pick);

//	public abstract void write(T value);

}
