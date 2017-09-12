package me.ghui.v2er.module.settings;

import butterknife.OnClick;
import me.ghui.v2er.R;
import me.ghui.v2er.general.Constants;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.util.Utils;

/**
 * Created by ghui on 11/09/2017.
 */

public class ProInfoActivity extends BaseActivity {
    @Override
    protected int attachLayoutRes() {
        return R.layout.pro_page;
    }


    @OnClick(R.id.go_get_pro_btn)
    void onGetProClicked() {
        Utils.openStorePage(Constants.PKG_PRO);
    }
}
