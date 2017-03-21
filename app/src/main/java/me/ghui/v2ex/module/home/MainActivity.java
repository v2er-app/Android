package me.ghui.v2ex.module.home;

import android.os.Bundle;

import me.ghui.v2ex.R;
import me.ghui.v2ex.module.base.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
    }

    @Override
    protected int attachLayoutRes() {
        return 0;
    }
}
