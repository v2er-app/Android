package me.ghui.v2ex.htmlpicker.bind;

import org.jsoup.nodes.Element;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import me.ghui.v2ex.htmlpicker.HtmlPicker;
import me.ghui.v2ex.htmlpicker.PickerAdapter;
import me.ghui.v2ex.htmlpicker.PickerAdapterFactory;
import me.ghui.v2ex.htmlpicker.SelectFactory;
import me.ghui.v2ex.htmlpicker.annotations.Select;
import me.ghui.v2ex.htmlpicker.internal.Types;
import me.ghui.v2ex.htmlpicker.reflect.TypeToken;

/**
 * Created by ghui on 13/04/2017.
 */

public final class ReflectivePickerAdapterFactory implements PickerAdapterFactory {

    @Override
    public <T> PickerAdapter<T> create(HtmlPicker picker, TypeToken<T> type) {
        Class<? super T> raw = type.getRawType();
        if (!Object.class.isAssignableFrom(raw)) {
            return null; // it's a primitive!
        }
        return new Adapter<>(type, getBoundFields(picker, type, raw));
    }

    private List<BoundField> getBoundFields(HtmlPicker htmlPicker, TypeToken<?> type, Class<?> raw) {
        List<BoundField> boundFields = new ArrayList<>();
        if (raw.isInterface()) return boundFields;
        //only support current class annotation(exclude the super class annotion)
        Select classSelect = raw.getAnnotation(Select.class);
        while (raw != Object.class) {
            for (Field field : raw.getDeclaredFields()) {
                String name = field.getName();
                if (name.contains("$change") || name.equals("serialVersionUID") || field.isSynthetic()) {
                    continue;
                }
                field.setAccessible(true);
                Type fieldType = Types.resolve(type.getType(), raw, field.getGenericType());
                BoundField boundField = createBoundField(htmlPicker, field, classSelect, TypeToken.get(fieldType));
                boundFields.add(boundField);
            }
            type = TypeToken.get(Types.resolve(type.getType(), raw, raw.getGenericSuperclass()));
            raw = type.getRawType();
        }
        return boundFields;
    }

    private BoundField createBoundField(HtmlPicker htmlPicker, Field field, final Select parentSelect, final TypeToken<?> fieldType) {
        final PickerAdapter<?> pickerAdapter = htmlPicker.getAdapter(fieldType);
        return new BoundField(field) {
            @Override
            public void read(Element element, Object instance) throws IllegalAccessException {
                Select select = field.getAnnotation(Select.class);
                if (parentSelect != null) {
                    if (select == null) {
                        throw new IllegalArgumentException("ignore Field: " + field.getName() + " without a Select anotation");
                    }
                    String query = parentSelect.value() + " " + select.value();//ancestor child
                    select = SelectFactory.create(query, select.attr());
                }
                Object fieldValue = pickerAdapter.read(element, select);
                if (fieldValue != null) {
                    field.set(instance, fieldValue);
                }
            }
        };
    }

    private static abstract class BoundField {
        Field field;

        BoundField(Field field) {
            this.field = field;
        }

        public abstract void read(Element element, Object instance) throws IllegalAccessException;
    }

    private static final class Adapter<T> extends PickerAdapter<T> {

        private TypeToken<T> type;
        private List<BoundField> boundFields;

        Adapter(TypeToken<T> type, List<BoundField> boundFields) {
            this.type = type;
            this.boundFields = boundFields;
        }

        @Override
        public T read(Element element, @Nullable Select select) {
            T instance = null;
            try {
                final Constructor<? super T> constructor
                        = type.getRawType().getDeclaredConstructor();
                if (!constructor.isAccessible()) {
                    constructor.setAccessible(true);
                }
                instance = (T) constructor.newInstance();
                for (BoundField boundField : boundFields) {
                    boundField.read(element, instance);
                }
            } catch (NoSuchMethodException
                    | IllegalAccessException
                    | InstantiationException
                    | InvocationTargetException e) {
                e.printStackTrace();
            }
            return instance;
        }
    }

}
