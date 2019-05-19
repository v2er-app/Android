package me.ghui.v2er.module.settings;

import butterknife.BindView;
import me.ghui.toolbox.android.Assets;
import me.ghui.v2er.R;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.topic.HtmlView;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.BaseToolBar;

public class UserManualActivity extends BaseActivity {
    @BindView(R.id.htmlview)
    HtmlView mHtmlView;

    @Override
    protected int attachLayoutRes() {
        return R.layout.user_manual;
    }

    @Override
    protected void configToolBar(BaseToolBar toolBar) {
        super.configToolBar(toolBar);
        Utils.setPaddingForStatusBar(toolBar);
        Utils.setPaddingForNavbar(mRootView);
    }

    @Override
    protected void init() {
        super.init();
        String localContent = Assets.getString("html/user_manual.html", this);
        mHtmlView.loadContentView(localContent);
    }
}
