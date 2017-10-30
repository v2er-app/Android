package me.ghui.v2er.network.bean;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;
import me.ghui.toolbox.android.Check;

/**
 * Created by ghui on 22/06/2017.
 */

public class ThxResponseInfo extends BaseInfo {
    @Pick(value = "a[href=/balance]", attr = Attrs.HREF)
    private String link;

    @Override
    public boolean isValid() {
        return Check.notEmpty(link);
    }
}
