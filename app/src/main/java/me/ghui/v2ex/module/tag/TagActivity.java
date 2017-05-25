package me.ghui.v2ex.module.tag;

import android.content.Intent;
import android.widget.TextView;

import me.ghui.v2ex.R;
import me.ghui.v2ex.module.base.BaseActivity;

/**
 * Created by ghui on 25/05/2017.
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
    protected void parseExtras(Intent intent) {
        mTagLink = intent.getStringExtra(TAG_LINK_KEY);
        mTagId = mTagLink.substring(mTagLink.lastIndexOf("/") + 1);
    }

    @Override
    protected void init() {
        TextView tagView = $(R.id.textView);
        tagView.append(mTagId);
    }
}
