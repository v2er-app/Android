package me.ghui.v2er.module.topic;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.network.bean.TopicBasicInfo;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.L;
import me.ghui.v2er.widget.LoadMoreRecyclerView;

/**
 * Fragment that displays topic content in landscape split view
 */
public class TopicContentFragment extends Fragment 
        implements LoadMoreRecyclerView.OnLoadMoreListener, HtmlView.OnHtmlRenderListener {

    private static final String ARG_TOPIC_ID = "topic_id";
    private static final String ARG_TOPIC_BASIC_INFO = "topic_basic_info";

    @BindView(R.id.topic_content_recyclerview)
    LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.empty_topic_content)
    View mEmptyView;

    private LoadMoreRecyclerView.Adapter<TopicInfo.Item> mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private String mTopicId;
    private TopicBasicInfo mTopicBasicInfo;
    private TopicInfo mTopicInfo;
    private boolean mIsScanInOrder = true;

    public static TopicContentFragment newInstance(String topicId, TopicBasicInfo basicInfo) {
        TopicContentFragment fragment = new TopicContentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOPIC_ID, topicId);
        args.putSerializable(ARG_TOPIC_BASIC_INFO, basicInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topic_content, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createAdapter();
        init();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTopicId = getArguments().getString(ARG_TOPIC_ID);
            mTopicBasicInfo = (TopicBasicInfo) getArguments().getSerializable(ARG_TOPIC_BASIC_INFO);
        }
    }

    private void createAdapter() {
        mAdapter = new LoadMoreRecyclerView.Adapter<>(this);
        mAdapter.addItemViewDelegate(new TopicHeaderItemDelegate(getContext()));
        mAdapter.addItemViewDelegate(new TopicContentItemDelegate(getContext()));
        mAdapter.addItemViewDelegate(new TopicReplyItemDelegate(getContext()));
    }

    @Override
    protected void init() {
        if (mTopicId != null) {
            mEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            setupRecyclerView();
            
            if (mTopicBasicInfo != null) {
                // Show basic info first
                List<TopicInfo.Item> data = new ArrayList<>();
                data.add(TopicInfo.HeaderInfo.build(mTopicBasicInfo));
                mAdapter.setData(data);
            }
            
            // Load full topic data
            loadTopicData();
        } else {
            showEmptyState();
        }
    }

    private void setupRecyclerView() {
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreListener(this);
    }

    private void showEmptyState() {
        mEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void loadTopicData() {
        // TODO: Implement actual topic loading
        // For now, just show loading state
        showLoading();
    }

    public void loadTopic(String topicId, TopicBasicInfo basicInfo) {
        mTopicId = topicId;
        mTopicBasicInfo = basicInfo;
        
        if (isAdded() && getView() != null) {
            init();
        }
    }

    private void showLoading() {
        // TODO: Show loading state
    }

    private void hideLoading() {
        // TODO: Hide loading state
    }

    // LoadMoreRecyclerView.OnLoadMoreListener implementation
    @Override
    public void onLoadMore(int page) {
        // TODO: Implement topic loading for pagination
        // For now, just show a simple message
        L.d("onLoadMore called for page: " + page);
    }

    // HtmlView.OnHtmlRenderListener implementation
    @Override
    public void onRenderCompleted() {
        hideLoading();
    }
}