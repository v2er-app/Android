package me.ghui.v2ex.module.home;

import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import me.ghui.v2ex.R;
import me.ghui.v2ex.module.base.BaseActivity;

public class MainActivity extends BaseActivity {

	@BindView(R.id.main_toolbar)
	Toolbar mToolbar;

	@Override
	protected int attachLayoutRes() {
		return R.layout.act_main;
	}

	@Override
	protected void initViews() {
		mToolbar.setNavigationIcon(R.drawable.main_navigation_icon);
		mToolbar.inflateMenu(R.menu.main_toolbar_menu);//设置右上角的填充菜单
	}


}
