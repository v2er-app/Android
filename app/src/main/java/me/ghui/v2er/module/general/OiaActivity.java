package me.ghui.v2er.module.general;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.orhanobut.logger.Logger;

import me.ghui.v2er.network.UrlInterceptor;

/**
 * Created by ghui on 30/06/2017.
 */

public class OiaActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        go();
    }

    private void go() {
        // 1.topic 2.member 3.node
        Intent oiaIntent = getIntent();
        String path = oiaIntent.getData().getPath();
        Logger.d("path: " + path);
        UrlInterceptor.openWapPage(path, this);
        finish();
    }
}
