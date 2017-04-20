package me.ghui.v2ex.htmlpicker.bind;

import org.jsoup.nodes.Element;

import javax.annotation.Nullable;

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
            return parseElement(element, select, String.class);
        }
    };

    private static final PickerAdapter<Number> INTEGER = new PickerAdapter<Number>() {
        @Override
        public Number read(Element element, @Nullable Select select) {
            return parseElement(element, select, int.class);
        }
    };

    private static final PickerAdapter<Number> LONG = new PickerAdapter<Number>() {
        @Override
        public Number read(Element element, @Nullable Select select) {
            return parseElement(element, select, Long.class);
        }
    };

    public static final PickerAdapterFactory STRING_FACTORY = newFactory(String.class, STRING);
    public static final PickerAdapterFactory INTEGER_FACTORY
            = newFactory(int.class, Integer.class, INTEGER);
    public static final PickerAdapterFactory LONG_FACTORY
            = newFactory(long.class, Long.class, LONG);

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

    public static <TT> PickerAdapterFactory newFactory(
            final Class<TT> unboxed, final Class<TT> boxed, final PickerAdapter<? super TT> adapter) {
        return new PickerAdapterFactory() {
            @SuppressWarnings("unchecked")
            @Override
            public <T> PickerAdapter<T> create(HtmlPicker picker, TypeToken<T> typeToken) {
                return (unboxed == typeToken.getRawType() || boxed == typeToken.getRawType())
                        ? (PickerAdapter<T>) adapter : null;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static <T> T parseElement(Element element, Select select, Class<T> type) {
        String value = null;
        if (select != null) {
            element = element.select(select.value()).first();
            if (element == null) return (T) value;
            String attr = select.attr();
            if ("text".equals(attr)) {
                value = element.text();
            } else if ("ownText".equals(attr)) {
                value = element.ownText();
            } else {
                value = element.attr(attr);
            }
        }
        if (type == int.class || type == Integer.class) {
            return (T) Integer.valueOf(value);
        } else if (type == long.class || type == Long.class) {
            return (T) Long.valueOf(value);
        } else if (type == float.class || type == Float.class) {
            return (T) Float.valueOf(value);
        } else if (type == double.class || type == Double.class) {
            return (T) Double.valueOf(value);
        } else if (type == boolean.class || type == Boolean.class) {
            return (T) Boolean.valueOf(value);
        } else {
            return (T) value;
        }
    }

}
