package me.ghui.v2er.general;

import android.app.Activity;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;

/**
 * Created by ghui on 18/09/2017.
 */

public class SlideBackManager {

    public static SlidrInterface attach(Activity activity) {
        SlidrConfig config = new SlidrConfig.Builder()
                .build();
        return Slidr.attach(activity, config);
    }

}
