package me.ghui.v2ex.module.write;

import android.support.v7.widget.Toolbar;

import me.ghui.v2ex.R;
import me.ghui.v2ex.module.base.BaseActivity;

/**
 * Created by ghui on 04/06/2017.
 */

public class NewTopicActivity extends BaseActivity<NewTopicContract.IPresenter> implements NewTopicContract.IView {

    @Override
    protected int attachLayoutRes() {
        return R.layout.act_new_topic;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void configToolBar(Toolbar toolBar) {
        super.configToolBar(toolBar);
        toolBar.inflateMenu(R.menu.new_topic_menu);//设置右上角的填充菜单
    }
}
