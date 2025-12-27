package me.ghui.v2er.module.topic;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.ItemViewDelegate;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.Theme;

/**
 * Delegate for reply sort header item
 */
public class TopicReplySortHeaderDelegate extends ItemViewDelegate<TopicInfo.Item> {

    private OnSortTypeChangeListener mSortTypeChangeListener;

    public TopicReplySortHeaderDelegate(Context context) {
        super(context);
    }

    public void setSortTypeChangeListener(OnSortTypeChangeListener listener) {
        this.mSortTypeChangeListener = listener;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.reply_sort_header;
    }

    @Override
    public boolean isForViewType(@Nullable TopicInfo.Item item, int position) {
        return item instanceof TopicInfo.ReplySortHeader;
    }

    @Override
    public void convert(ViewHolder holder, TopicInfo.Item item, int position) {
        TopicInfo.ReplySortHeader sortHeader = (TopicInfo.ReplySortHeader) item;
        TopicInfo.ReplySortType currentSortType = sortHeader.getCurrentSortType();

        TextView timeBtn = holder.getView(R.id.sort_by_time_btn);
        TextView popularityBtn = holder.getView(R.id.sort_by_popularity_btn);

        updateSortButtonState(timeBtn, currentSortType == TopicInfo.ReplySortType.BY_TIME);
        updateSortButtonState(popularityBtn, currentSortType == TopicInfo.ReplySortType.BY_POPULARITY);

        timeBtn.setOnClickListener(v -> {
            if (currentSortType != TopicInfo.ReplySortType.BY_TIME && mSortTypeChangeListener != null) {
                mSortTypeChangeListener.onSortTypeChanged(TopicInfo.ReplySortType.BY_TIME);
            }
        });

        popularityBtn.setOnClickListener(v -> {
            if (currentSortType != TopicInfo.ReplySortType.BY_POPULARITY && mSortTypeChangeListener != null) {
                mSortTypeChangeListener.onSortTypeChanged(TopicInfo.ReplySortType.BY_POPULARITY);
            }
        });
    }

    private void updateSortButtonState(TextView button, boolean isSelected) {
        if (isSelected) {
            button.setBackgroundResource(R.drawable.sort_toggle_item_bg_selected);
            button.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
            // Tint the drawable to white
            tintCompoundDrawables(button, android.R.color.white);
        } else {
            button.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            int hintColor = Theme.getColor(R.attr.hintTextColor, mContext);
            button.setTextColor(hintColor);
            // Tint the drawable to hint color
            tintCompoundDrawablesWithColor(button, hintColor);
        }
    }

    private void tintCompoundDrawables(TextView textView, int colorResId) {
        Drawable[] drawables = textView.getCompoundDrawablesRelative();
        for (int i = 0; i < drawables.length; i++) {
            if (drawables[i] != null) {
                Drawable wrappedDrawable = DrawableCompat.wrap(drawables[i].mutate());
                DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(mContext, colorResId));
                drawables[i] = wrappedDrawable;
            }
        }
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

    private void tintCompoundDrawablesWithColor(TextView textView, int color) {
        Drawable[] drawables = textView.getCompoundDrawablesRelative();
        for (int i = 0; i < drawables.length; i++) {
            if (drawables[i] != null) {
                Drawable wrappedDrawable = DrawableCompat.wrap(drawables[i].mutate());
                DrawableCompat.setTint(wrappedDrawable, color);
                drawables[i] = wrappedDrawable;
            }
        }
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

    public interface OnSortTypeChangeListener {
        void onSortTypeChanged(TopicInfo.ReplySortType sortType);
    }
}
