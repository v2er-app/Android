package me.ghui.v2ex.module.drawer.dailyhot;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import me.ghui.v2ex.R;
import me.ghui.v2ex.module.home.CommonItemViewHolder;
import me.ghui.v2ex.network.bean.DailyHotInfo;
import me.ghui.v2ex.util.Utils;

/**
 * Created by ghui on 28/03/2017.
 */

public class DailyHotAdapter extends RecyclerView.Adapter<CommonItemViewHolder> {

	private Context mContext;
	private DailyHotInfo mDailyHotInfo;
	private LayoutInflater mLayoutInflater;

	public DailyHotAdapter(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
	}

	public void setData(DailyHotInfo dailyHotInfo) {
		mDailyHotInfo = dailyHotInfo;
		notifyDataSetChanged();
	}

	@Override
	public CommonItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = mLayoutInflater.inflate(R.layout.common_list_item, parent, false);
		return new CommonItemViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(CommonItemViewHolder holder, int position) {
		DailyHotInfo.Item item = mDailyHotInfo.get(position);
		Glide.with(mContext)
				.load("http:" + item.getMember().getAvatar())
				.into(holder.avtarImg);
		holder.userNameTv.setText(item.getMember().getUserName());
		holder.tagView.setText(item.getNode().getTitle());
		holder.titleTv.setText(item.getTitle());
		holder.commentNumTv.setText("评论" + item.getReplies());
	}

	@Override
	public int getItemCount() {
		return Utils.listSize(mDailyHotInfo);
	}

}
