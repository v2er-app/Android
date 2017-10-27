package me.ghui.v2er.general;

import android.app.Activity;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;

import me.ghui.v2er.R;

/**
 * Created by ghui on 18/09/2017.
 */

public class SlideBackManager {

    public static SlidrInterface attach(Activity activity) {
        boolean edgeBack = !Pref.readBool(R.string.pref_key_fullscreen_back);
        SlidrConfig config = new SlidrConfig.Builder()
                .sensitivity(0.2f)
                .distanceThreshold(0.15f)
                .edge(edgeBack)
                .build();
        return Slidr.attach(activity, config);
    }
}
