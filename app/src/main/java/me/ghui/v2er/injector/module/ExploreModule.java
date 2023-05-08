package me.ghui.v2er.injector.module;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.lang.ref.WeakReference;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonAdapter;
import me.ghui.v2er.adapter.base.ItemViewDelegate;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.module.home.ExploreContract;
import me.ghui.v2er.module.home.ExploreFragment;
import me.ghui.v2er.module.home.ExplorePresenter;
import me.ghui.v2er.module.node.NodeTopicActivity;
import me.ghui.v2er.module.topic.TopicActivity;
import me.ghui.v2er.module.user.UserHomeActivity;
import me.ghui.v2er.network.bean.DailyHotInfo;
import me.ghui.v2er.network.bean.ExplorePageInfo;
import me.ghui.v2er.network.bean.NewsInfo;
import me.ghui.v2er.network.bean.NodesNavInfo;
import me.ghui.v2er.network.bean.TopicBasicInfo;
import me.ghui.v2er.util.ViewUtils;
import me.ghui.v2er.widget.NavNodesWrapper;

/**
 * Created by ghui on 22/05/2017.
 */

@Module
public class ExploreModule {

    private ExploreFragment mExploreFragment;
    private WeakReference<Context> weakContext;

    public ExploreModule(ExploreFragment mExploreFragment) {
        this.mExploreFragment = mExploreFragment;
        if (mExploreFragment.getContext() != null) {
            weakContext = new WeakReference<>(mExploreFragment.getContext());
        }
    }

    @Provides
    public MultiItemTypeAdapter<Serializable> provideAdapter() {
        if (weakContext.get() != null) {
            MultiItemTypeAdapter<Serializable> multiItemTypeAdapter = new
                    MultiItemTypeAdapter<Serializable>(mExploreFragment.getContext());

            multiItemTypeAdapter.addItemViewDelegate(new ItemViewDelegate<Serializable>(weakContext.get()) {

                @Override
                public int getItemViewLayoutId() {
                    return R.layout.common_list_item;
                }

                @Override
                public void convert(ViewHolder holder, Serializable serializable, int position) {
                    super.convert(holder, serializable, position);
                    DailyHotInfo.Item item = (DailyHotInfo.Item) serializable;
                    GlideApp.with(mContext)
                            .load(item.getMember().getAvatar())
                            .placeholder(R.drawable.avatar_placeholder_drawable)
                            .into((ImageView) holder.getView(R.id.avatar_img));
                    holder.setText(R.id.user_name_tv, item.getMember().getUserName());
                    holder.setText(R.id.time_tv, item.getTime());
                    holder.setText(R.id.tagview, item.getNode().getTitle());
                    holder.setText(R.id.title_tv, item.getTitle());
                    TextView commentTV = holder.getTextView(R.id.comment_num_tv);
                    commentTV.setText("评论" + item.getReplies());
                    ViewUtils.highlightCommentNum(commentTV);

                    holder.setOnClickListener(v -> {
                                DailyHotInfo.Item.Member member = item.getMember();
                                UserHomeActivity.open(member.getUserName(), mContext, holder.getImgView(R.id.avatar_img), member.getAvatar());
                            },
                            R.id.avatar_img, R.id.user_name_tv);

                    holder.setOnClickListener(v -> {
                        View shareView = holder.getView(R.id.avatar_img);
                        TopicBasicInfo basicInfo = new TopicBasicInfo.Builder(item.getTitle(), item.getMember().getAvatar())
                                .author(item.getMember().getUserName())
                                .tag(item.getNode().getTitle())
                                .tagLink(item.getNode().getUrl().substring(item.getNode().getUrl().indexOf("/go")))
                                .commentNum(item.getReplies())
                                .build();
                        TopicActivity.open(item.getUrl(),
                                weakContext.get(), shareView, basicInfo);
                            }, R.id.common_list_item_root_layout);

                    holder.setOnClickListener(v ->
                                    NodeTopicActivity.open(item.getNode().getUrl(), mContext),
                            R.id.tagview);
                }

                @Override
                public boolean isForViewType(@Nullable Serializable item, int position) {
                    return item instanceof DailyHotInfo.Item;
                }
            });

            multiItemTypeAdapter.addItemViewDelegate(new ItemViewDelegate<Serializable>(weakContext.get()) {
                @Override
                public int getItemViewLayoutId() {
                    return R.layout.common_title;
                }

                @Override
                public void convert(ViewHolder holder, Serializable serializable, int position) {
                    super.convert(holder, serializable, position);
                    String title = (String) serializable;
                    holder.setText(R.id.common_title, title);
                }

                @Override
                public boolean isForViewType(@Nullable Serializable item, int position) {
                    return item instanceof String;
                }
            });

            multiItemTypeAdapter.addItemViewDelegate(new ItemViewDelegate<Serializable>(weakContext.get()) {
                @Override
                public int getItemViewLayoutId() {
                    return R.layout.nodes_nav_item;
                }

                @Override
                public void convert(ViewHolder holder, Serializable serializable, int position) {
                    super.convert(holder, serializable, position);
                    NodesNavInfo.Item item = (NodesNavInfo.Item) serializable;
                    holder.setText(R.id.node_nav_category_tv, item.getCategory());
                    NavNodesWrapper nodesWrapper = holder.getView(R.id.nodes_nav_node_wrapper);
                    nodesWrapper.setData(item.getNodes());
                }

                @Override
                public boolean isForViewType(@Nullable Serializable item, int position) {
                    return item instanceof NodesNavInfo.Item;
                }

            });
            return multiItemTypeAdapter;
        } else {
            return null;
        }

    }

    @Provides
    public ExploreContract.IPresenter providePresenter() {
        return new ExplorePresenter(mExploreFragment);
    }

}
