package me.ghui.v2ex.module.home;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.ghui.v2ex.R;
import me.ghui.v2ex.widget.TagView;

/**
 * Created by ghui on 17/04/2017.
 */

public class CommonItemViewHolder extends RecyclerView.ViewHolder {

	@BindView(R.id.avtar_img)
	public ImageView avtarImg;
	@BindView(R.id.leftdrawer_username_tv)
	public TextView userNameTv;
	@BindView(R.id.time_tv)
	public TextView TimeTv;
	@BindView(R.id.tagview)
	public TagView tagView;
	@BindView(R.id.title_tv)
	public TextView titleTv;
	@BindView(R.id.comment_num_tv)
	public TextView commentNumTv;

	public CommonItemViewHolder(View itemView) {
		super(itemView);
		ButterKnife.bind(this, itemView);
	}

}
