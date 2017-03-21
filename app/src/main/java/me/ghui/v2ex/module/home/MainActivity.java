package me.ghui.v2ex.module.home;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import me.ghui.v2ex.R;
import me.ghui.v2ex.module.base.BaseActivity;

public class MainActivity extends BaseActivity {

	@BindView(R.id.main_toolbar)
	Toolbar mToolbar;
	@BindView(R.id.left_draw_layout)
	DrawerLayout mDrawerLayout;
	@BindView(R.id.navigation_view)
	NavigationView mNavigationView;

	@Override
	protected int attachLayoutRes() {
		return R.layout.act_main;
	}

	@Override
	protected void initViews() {
		mToolbar.setNavigationIcon(R.drawable.main_navigation_icon);
		mToolbar.inflateMenu(R.menu.main_toolbar_menu);//设置右上角的填充菜单
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
					mDrawerLayout.closeDrawer(Gravity.START);
				} else {
					mDrawerLayout.openDrawer(Gravity.START);
				}
			}
		});
		mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (item.getItemId() == R.id.action_search) {
					Toast.makeText(getContext(), "do Search...", Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		});

		mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				mDrawerLayout.closeDrawers();
				return true;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				mDrawerLayout.openDrawer(GravityCompat.START);  // OPEN DRAWER
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
