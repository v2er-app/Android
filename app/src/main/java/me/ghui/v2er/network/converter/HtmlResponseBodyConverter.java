package me.ghui.v2er.network.converter;


import java.io.IOException;
import java.lang.reflect.Type;

import me.ghui.fruit.Fruit;
import me.ghui.v2er.network.bean.BaseInfo;
import me.ghui.v2er.network.bean.IBase;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by ghui on 11/04/2017.
 */

public class HtmlResponseBodyConverter<T extends IBase> implements Converter<ResponseBody, T> {

    private Fruit mPicker;
    private Type mType;

    public HtmlResponseBodyConverter(Fruit fruit, Type type) {
        mPicker = fruit;
        mType = type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        T data = mPicker.fromHtml(response, mType);
        if (data != null) {
            data.setResponse(response);
        }
        return data;
    }
}
