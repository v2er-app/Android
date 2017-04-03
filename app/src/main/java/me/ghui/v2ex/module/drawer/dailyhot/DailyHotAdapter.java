package me.ghui.v2ex.module.drawer.dailyhot;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.ghui.v2ex.R;
import me.ghui.v2ex.network.bean.DailyHotInfo;
import me.ghui.v2ex.util.Utils;
import me.ghui.v2ex.widget.TagView;

/**
 * Created by ghui on 28/03/2017.
 */

public class DailyHotAdapter extends RecyclerView.Adapter<DailyHotAdapter.ViewHolder> {

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
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = mLayoutInflater.inflate(R.layout.common_list_item, parent, false);
		return new ViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
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
		return Utils.sizeOfList(mDailyHotInfo);
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.avtar_img)
		ImageView avtarImg;
		@BindView(R.id.username_tv)
		TextView userNameTv;
		@BindView(R.id.time_tv)
		TextView TimeTv;
		@BindView(R.id.tagview)
		TagView tagView;
		@BindView(R.id.title_tv)
		TextView titleTv;
		@BindView(R.id.comment_num_tv)
		TextView commentNumTv;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

	}

}
