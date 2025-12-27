package me.ghui.v2er.injector.module;

import android.content.Context;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2er.util.Check;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.general.Pref;
import me.ghui.v2er.injector.scope.PerActivity;
import me.ghui.v2er.module.node.NodeTopicActivity;
import me.ghui.v2er.module.topic.TopicActivity;
import me.ghui.v2er.module.topic.TopicContentItemDelegate;
import me.ghui.v2er.module.topic.TopicContract;
import me.ghui.v2er.module.topic.TopicHeaderItemDelegate;
import me.ghui.v2er.module.topic.TopicPresenter;
import me.ghui.v2er.module.topic.TopicReplyItemDelegate;
import me.ghui.v2er.module.topic.TopicReplySortHeaderDelegate;
import me.ghui.v2er.module.user.UserHomeActivity;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.ThxResponseInfo;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.Voast;
import me.ghui.v2er.widget.LoadMoreRecyclerView;

/**
 * Created by ghui on 05/05/2017.
 */

@Module
public class TopicModule {

    private TopicActivity mView;

    public TopicModule(TopicActivity view) {
        mView = view;
    }

    @Provides
    public LoadMoreRecyclerView.Adapter<TopicInfo.Item> provideAdapter() {
        LoadMoreRecyclerView.Adapter<TopicInfo.Item> adapter = new LoadMoreRecyclerView.Adapter<TopicInfo.Item>(mView) {
            @Override
            protected void bindListener(ViewHolder holder, int viewType) {
                super.bindListener(holder, viewType);
                holder.setOnClickListener(v -> {
                            String userName;
                            String avatar;
                            View sourceView;
                            if (v.getId() == R.id.avatar_img || v.getId() == R.id.user_name_tv) {
                                userName = getItem(holder.index()).getUserName();
                                avatar = getItem(holder.index()).getAvatar();
                                sourceView = holder.getImgView(R.id.avatar_img);
                            } else {
                                userName = getItem(holder.index()).getUserName();
                                avatar = getItem(holder.index()).getAvatar();
                                sourceView = holder.getImgView(R.id.reply_avatar_img);
                            }
                            UserHomeActivity.open(userName, mContext, sourceView, avatar);
                        },
                        R.id.avatar_img, R.id.user_name_tv, R.id.reply_avatar_img, R.id.reply_user_name_tv);

                holder.setOnClickListener(v ->
                        NodeTopicActivity.open(((TopicInfo.HeaderInfo) getItem(holder.index())).getTagLink(), mContext), R.id.tagview);

                View.OnClickListener onThxClickListener = v -> {
                    if (UserUtils.notLoginAndProcessToLogin(false, mView.getContext())) return;
                    TopicInfo.Reply replyInfo = (TopicInfo.Reply) getItem(holder.index());

                    if (replyInfo.isSelf()) {
                        Voast.show("自己不用感谢啦");
                        return;
                    }

                    if (replyInfo.hadThanked()) {
                        Toast.makeText(mContext, R.string.already_thx_cannot_return, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mView.mPresenter.thxReplier(replyInfo.getReplyId(), mView.getOnce())
                            .subscribe(new GeneralConsumer<ThxResponseInfo>(mView) {
                                @Override
                                public void onConsume(ThxResponseInfo thxReplyInfo) {
                                    if (thxReplyInfo.isValid()) {
                                        replyInfo.updateThanks(true);
                                        notifyItemChanged(holder.index());
                                    } else {
                                        Voast.show(mContext.getString(R.string.send_thx_occured_error));
                                    }
                                }
                            });
                };

                if (Pref.readBool(R.string.pref_key_long_press_thx)) {
                    holder.setOnLongClickListener(v -> {
                        onThxClickListener.onClick(v);
                        return true;
                    }, R.id.reply_thx_img);
                } else {
                    holder.setOnClickListener(onThxClickListener, R.id.reply_thx_img);
                }

                holder.setOnClickListener(v -> mView.onItemMoreMenuClick(holder.index()), R.id.more_menu_img);
            }

        };
        adapter.addItemViewDelegate(new TopicHeaderItemDelegate(mView));
        adapter.addItemViewDelegate(new TopicContentItemDelegate(mView));
        TopicReplySortHeaderDelegate sortHeaderDelegate = new TopicReplySortHeaderDelegate(mView);
        sortHeaderDelegate.setSortTypeChangeListener(mView);
        adapter.addItemViewDelegate(sortHeaderDelegate);
        TopicReplyItemDelegate replyItemDelegate = new TopicReplyItemDelegate(mView);
        replyItemDelegate.setMemberClickListener(mView);
        adapter.addItemViewDelegate(replyItemDelegate);
        return adapter;
    }

    @Provides
    @PerActivity
    public TopicContract.IPresenter providePresenter() {
        return new TopicPresenter(mView);
    }

    @Provides
    public TopicAtAdapter provideReplierAdapter() {
        return new TopicAtAdapter(mView.getContext(), R.layout.at_select_replier_list_item);
    }

    public class TopicAtAdapter extends CommonAdapter<TopicInfo.Item> implements Filterable {
        private ValueFilter mValueFilter;

        public TopicAtAdapter(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        protected void convert(ViewHolder holder, TopicInfo.Item reply, int position) {
            View dividerView = holder.getView(R.id.top_cardview_divider);
            dividerView.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            holder.setText(R.id.replier_username_tv, reply.getUserName());
            GlideApp.with(mContext)
                    .load(reply.getAvatar())
                    .placeholder(R.drawable.avatar_placeholder_drawable)
                    .into(holder.getImgView(R.id.replier_avatar_img));
        }

        @Override
        public Filter getFilter() {
            if (mValueFilter == null) {
                mValueFilter = new ValueFilter();
            }
            return mValueFilter;
        }

        private class ValueFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                List<TopicInfo.Item> datum = mView.topicReplyInfo();
                if (Check.isEmpty(constraint)) {
                    filterResults.values = datum;
                    filterResults.count = Utils.listSize(datum);
                    return filterResults;
                }
                List<TopicInfo.Item> resultList = new ArrayList<>();
                for (TopicInfo.Item item : datum) {
                    if (item.getUserName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        resultList.add(item);
                    }
                }
                filterResults.count = resultList.size();
                filterResults.values = resultList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                setData((List<TopicInfo.Item>) results.values);
            }
        }
    }


}
