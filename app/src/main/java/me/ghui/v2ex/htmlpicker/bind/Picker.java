package me.ghui.v2ex.htmlpicker.bind;

import me.ghui.v2ex.htmlpicker.annotations.Pick;

/**
 * Created by ghui on 13/04/2017.
 */

public class Picker {
	private String path;
	private String attr;

	public static Picker create(Pick pick) {
		return create(pick.value(), pick.attr());
	}

	public static Picker create(String path, String attr) {
		return new Picker(path, attr);
	}

	private Picker(String path, String attr) {
		this.path = path;
		this.attr = attr;
	}

	public String getPath() {
		return path;
	}

	public String getAttr() {
		return attr;
	}
}
