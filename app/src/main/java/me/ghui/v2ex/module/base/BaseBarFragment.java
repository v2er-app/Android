package me.ghui.v2ex.module.base;

import android.widget.Toolbar;

import butterknife.BindView;
import me.ghui.v2ex.R;

/**
 * Created by ghui on 21/03/2017.
 * Fragment with a toobar
 */

public abstract class BaseBarFragment extends BaseFragment {

	@BindView(R.id.basebar)
	Toolbar mToolbar;

	@Override
	protected int attachLayoutId() {
		return R.layout.frag_base_bar_fragment;
	}

}
