package me.ghui.v2ex.htmlpicker;

import java.lang.annotation.Annotation;

import me.ghui.v2ex.htmlpicker.annotations.Select;

/**
 * Created by ghui on 16/04/2017.
 */

public class SelectFactory {

	public static Select create(final String query, final String attr) {
		return new Select() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return Select.class;
			}

			@Override
			public String value() {
				return query;
			}

			@Override
			public String attr() {
				return attr;
			}
		};
	}
}
