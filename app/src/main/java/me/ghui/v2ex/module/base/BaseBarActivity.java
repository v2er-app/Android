package me.ghui.v2ex.module.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import butterknife.BindView;
import me.ghui.v2ex.R;

/**
 * Created by ghui on 26/03/2017.
 */

public abstract class BaseBarActivity extends BaseActivity {

	@BindView(R.id.basebar)
	Toolbar mToolbar;
	@BindView(R.id.content_container)
	FrameLayout mContentLayout;

	@Override
	protected int attachLayoutRes() {
		return R.layout.act_basebar;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (attachContentLayoutRes() != 0) {
			LayoutInflater.from(this).inflate(attachContentLayoutRes(), mContentLayout);
		}
	}

	@LayoutRes
	protected abstract int attachContentLayoutRes();

}
