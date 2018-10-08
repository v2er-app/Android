package me.ghui.v2er.module.topic;

import android.content.Context;
import android.support.annotation.Nullable;

import me.ghui.v2er.adapter.base.ItemViewDelegate;
import me.ghui.v2er.network.bean.TopicInfo;

public class TopicAppendContentDelegate extends ItemViewDelegate<TopicInfo.Item> {
    public TopicAppendContentDelegate(Context context) {
        super(context);
    }

    @Override
    public int getItemViewLayoutId() {
        return 0;
    }

    @Override
    public boolean isForViewType(@Nullable TopicInfo.Item item, int position) {
        return false;
    }
}
