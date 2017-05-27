package me.ghui.v2ex.module.tag;

import android.content.Intent;

import me.ghui.v2ex.R;
import me.ghui.v2ex.module.base.BaseActivity;
import me.ghui.v2ex.widget.BaseToolBar;

/**
 * Created by ghui on 25/05/2017.
 * 节点话题页
 */

// TODO: 25/05/2017
public class TagActivity extends BaseActivity<TagContract.IPresenter> implements TagContract.IView {

    public static final String TAG_LINK_KEY = KEY("tag_link_key");

    private String mTagId;
    private String mTagLink;

    @Override
    protected int attachLayoutRes() {
        return R.layout.act_tag_page;
    }

    @Override
    protected BaseToolBar attachToolbar() {
        return null;
    }

    @Override
    protected void parseExtras(Intent intent) {
        mTagLink = intent.getStringExtra(TAG_LINK_KEY);
        mTagId = mTagLink.substring(mTagLink.lastIndexOf("/") + 1);
    }

    @Override
    protected void init() {
    }
}
