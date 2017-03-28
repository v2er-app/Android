package me.ghui.v2ex.module.drawer.dailyhot;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ghui on 28/03/2017.
 */

// TODO: 28/03/2017
public class DailyHotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private Context mContext;

	public DailyHotAdapter(Context context) {
		this.mContext = context;
	}

	public void setData() {
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return null;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

	}

	@Override
	public int getItemCount() {
		return 0;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		public ViewHolder(View itemView) {
			super(itemView);
		}
	}

}
