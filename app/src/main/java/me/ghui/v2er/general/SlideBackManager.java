package me.ghui.v2er.general;

import android.app.Activity;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;

/**
 * Created by ghui on 18/09/2017.
 */

public class SlideBackManager {

    public static void attach(Activity activity) {
        SlidrConfig config = new SlidrConfig.Builder()
//                .sensitivity(1f)
//                .scrimColor(Color.BLACK)
//                .scrimStartAlpha(0.8f)
//                .scrimEndAlpha(0f)
                .edge(true)
                .build();
        Slidr.attach(activity, config);
    }

}
