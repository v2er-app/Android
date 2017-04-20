package me.ghui.v2ex.module.home;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import javax.inject.Inject;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import me.ghui.v2ex.R;
import me.ghui.v2ex.injector.component.DaggerNewsComponent;
import me.ghui.v2ex.injector.module.NewsModule;
import me.ghui.v2ex.module.base.BaseFragment;
import me.ghui.v2ex.network.bean.NewsInfo;

/**
 * Created by ghui on 22/03/2017.
 */

public class NewsFragment extends BaseFragment<NewsContract.IPresenter> implements NewsContract.IView {

	@BindView(R.id.news_recyclerview)
	RecyclerView mRecyclerView;

	@Inject
	NewsAdapter mNewsAdapter;

	public static NewsFragment newInstance() {
		Bundle args = new Bundle();
		NewsFragment fragment = new NewsFragment();
		fragment.setArguments(args);
		return fragment;
	}


	@Override
	protected int attachLayoutRes() {
		return R.layout.frag_news;
	}

	@Override
	protected void startInject() {
		DaggerNewsComponent.builder()
				.appComponent(getAppComponent())
				.newsModule(new NewsModule(this))
				.build()
				.inject(this);
	}

	@Override
	protected void init() {
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
		itemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recyclerview_divider));
		mRecyclerView.addItemDecoration(itemDecoration);
		mRecyclerView.setAdapter(mNewsAdapter);
	}

	@Override
	protected PtrHandler attachPtrHandler() {
		return new PtrDefaultHandler2() {
			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				mPresenter.start();
			}

			@Override
			public void onLoadMoreBegin(PtrFrameLayout frame) {

			}
		};
	}

	@Override
	public void fillView(NewsInfo newsInfos) {
		mNewsAdapter.setData(newsInfos);
	}
}
