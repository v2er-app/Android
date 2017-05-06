package me.ghui.v2ex.htmlpicker;

import java.lang.annotation.Annotation;

import me.ghui.v2ex.htmlpicker.annotations.Pick;

/**
 * Created by ghui on 16/04/2017.
 */

public class PickFactory {

	public static Pick create(final String query, final String attr) {
		return new Pick() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return Pick.class;
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
