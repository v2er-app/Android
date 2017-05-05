package me.ghui.v2ex.module.topic;

import me.ghui.v2ex.R;
import me.ghui.v2ex.module.base.BaseActivity;

import static me.ghui.v2ex.util.Utils.KEY;

/**
 * Created by ghui on 04/05/2017.
 */

public class TopicActivity extends BaseActivity<TopicContract.IPresenter> implements TopicContract.IView {
    public static final String TOPIC_LINK_KEY = KEY("topic_link_key");

    @Override
    protected int attachLayoutRes() {
        return R.layout.act_topic;
    }

}
