package me.ghui.v2ex.module.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import me.ghui.v2ex.R;
import me.ghui.v2ex.network.bean.NewsInfo;
import me.ghui.v2ex.network.bean.NewsItem;
import me.ghui.v2ex.util.Utils;

/**
 * Created by ghui on 03/04/2017.
 */

public class NewsAdapter extends RecyclerView.Adapter<CommonItemViewHolder> {

	private Context mContext;
	private NewsInfo mNewsInfo;
	private LayoutInflater mLayoutInflater;

	public NewsAdapter(Context context) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
	}

	public void setData(NewsInfo newsInfo) {
		mNewsInfo = newsInfo;
		notifyDataSetChanged();
	}

	@Override
	public CommonItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new CommonItemViewHolder(mLayoutInflater.inflate(R.layout.common_list_item, parent, false));
	}

	@Override
	public void onBindViewHolder(CommonItemViewHolder holder, int position) {
		NewsItem item = mNewsInfo.getItems().get(position);
		Glide.with(mContext)
				.load("https:" + item.getAvatar())
				.into(holder.avtarImg);
		holder.userNameTv.setText(item.getUser());
		holder.tagView.setText(item.getTagName());
		holder.titleTv.setText(item.getTitle());
		holder.commentNumTv.setText("评论" + item.getReplies());
	}

	@Override
	public int getItemCount() {
		return mNewsInfo != null ? Utils.sizeOfList(mNewsInfo.getItems()) : 0;
	}
}
