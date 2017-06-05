package me.ghui.v2ex.module.create;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.ghui.v2ex.R;
import me.ghui.v2ex.adapter.base.ItemViewDelegate;
import me.ghui.v2ex.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2ex.adapter.base.ViewHolder;
import me.ghui.v2ex.network.bean.CreateTopicPageInfo;
import me.ghui.v2ex.widget.BaseRecyclerView;

import static me.ghui.v2ex.util.Utils.KEY;

/**
 * Created by ghui on 05/06/2017.
 */

public class NoteSelectFragment extends DialogFragment {
    private static final String NODE_ALL = KEY("node_all");
    private static final String NODE_HOT = KEY("node_hot");

    @BindView(R.id.note_select_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.note_select_recyclerview)
    BaseRecyclerView mBaseRecyclerView;

    public static NoteSelectFragment newInstance(CreateTopicPageInfo topicPageInfo) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(NODE_ALL, (ArrayList<? extends Parcelable>) topicPageInfo.getNodes());
        args.putParcelableArrayList(NODE_HOT, (ArrayList<? extends Parcelable>) topicPageInfo.getHotNodes());
        NoteSelectFragment fragment = new NoteSelectFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.note_select, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mBaseRecyclerView.addDivider();
        mBaseRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MultiItemTypeAdapter<CreateTopicPageInfo.BaseNode> mAdapter = new MultiItemTypeAdapter<>(getActivity());
        List<CreateTopicPageInfo.BaseNode> nodes = getArguments().getParcelableArrayList(NODE_HOT);
        List<CreateTopicPageInfo.BaseNode> allNodes = getArguments().getParcelableArrayList(NODE_HOT);
        nodes.addAll(allNodes);
        mAdapter.setData(nodes);
        mAdapter.addItemViewDelegate(new ItemViewDelegate<CreateTopicPageInfo.BaseNode>(getActivity()) {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_select_hot_node;
            }

            @Override
            public boolean isForViewType(@javax.annotation.Nullable CreateTopicPageInfo.BaseNode item, int position) {
                return item instanceof CreateTopicPageInfo.HotNode;
            }

            @Override
            public void convert(ViewHolder holder, CreateTopicPageInfo.BaseNode baseNode, int position) {
                CreateTopicPageInfo.HotNode hotNode = (CreateTopicPageInfo.HotNode) baseNode;
                holder.setText(R.id.node_name_tv, hotNode.getTitle());
            }
        });

        mAdapter.addItemViewDelegate(new ItemViewDelegate<CreateTopicPageInfo.BaseNode>(getActivity()) {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_select_node;
            }

            @Override
            public boolean isForViewType(@javax.annotation.Nullable CreateTopicPageInfo.BaseNode item, int position) {
                return item instanceof CreateTopicPageInfo.Node;
            }

            @Override
            public void convert(ViewHolder holder, CreateTopicPageInfo.BaseNode baseNode, int position) {
                CreateTopicPageInfo.Node node = (CreateTopicPageInfo.Node) baseNode;
                holder.setText(R.id.node_name_tv, node.getTitle());
            }
        });
        mBaseRecyclerView.setAdapter(mAdapter);
    }

    @OnClick(R.id.note_select_cancle_btn)
    void onCancleClicked() {
        dismiss();
    }

}
