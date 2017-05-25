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

    public static final String TAG_NAME_KEY = KEY("tag_name_key");

    private String mTag;

    @Override
    protected int attachLayoutRes() {
        return R.layout.act_tag_page;
    }

    @Override
    protected void parseExtras(Intent intent) {
        mTag = intent.getStringExtra(TAG_NAME_KEY);
    }

    @Override
    protected void init() {
        TextView tagView = $(R.id.textView);
        tagView.append(mTag);
    }
}
