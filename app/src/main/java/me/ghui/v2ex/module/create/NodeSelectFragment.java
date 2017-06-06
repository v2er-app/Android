package me.ghui.v2ex.module.create;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

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
import me.ghui.v2ex.util.ScaleUtils;
import me.ghui.v2ex.widget.BaseRecyclerView;

import static me.ghui.v2ex.util.Utils.KEY;

/**
 * Created by ghui on 05/06/2017.
 */

public class NodeSelectFragment extends DialogFragment {
    private static final String NODE_ALL = KEY("node_all");
    private static final String NODE_HOT = KEY("node_hot");

    @BindView(R.id.node_select_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.node_select_recyclerview)
    BaseRecyclerView mBaseRecyclerView;

    public static NodeSelectFragment newInstance(CreateTopicPageInfo topicPageInfo) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(NODE_ALL, (ArrayList<? extends Parcelable>) topicPageInfo.getNodes());
        args.putParcelableArrayList(NODE_HOT, (ArrayList<? extends Parcelable>) topicPageInfo.getHotNodes());
        NodeSelectFragment fragment = new NodeSelectFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        resize(getDialog());
        View view = inflater.inflate(R.layout.node_select, container);
        ButterKnife.bind(this, view);
        return view;
    }

    private void resize(Dialog dialog) {
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        float screenWidth = ScaleUtils.getScreenW();
        layoutParams.width = (int) (screenWidth * 0.8);
        dialog.getWindow().setAttributes(layoutParams);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mToolbar.inflateMenu(R.menu.search_note_menu);
        mToolbar.setOnMenuItemClickListener(menuItem -> {
            Toast.makeText(getActivity(), "do search", Toast.LENGTH_SHORT).show();
            return true;
        });
        // TODO: 06/06/2017

//        mBaseRecyclerView.addDivider();
        mBaseRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<CreateTopicPageInfo.BaseNode> nodes = getArguments().getParcelableArrayList(NODE_HOT);
        List<CreateTopicPageInfo.BaseNode> allNodes = getArguments().getParcelableArrayList(NODE_ALL);
        nodes.addAll(allNodes);
        MultiItemTypeAdapter<CreateTopicPageInfo.BaseNode> mAdapter = new MultiItemTypeAdapter<>(getActivity());
        mAdapter.setData(nodes);
        mAdapter.addItemViewDelegate(new ItemViewDelegate<CreateTopicPageInfo.BaseNode>(getActivity()) {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_select_node;
            }

            @Override
            public boolean isForViewType(@javax.annotation.Nullable CreateTopicPageInfo.BaseNode item, int position) {
                return true;
            }

            @Override
            public void convert(ViewHolder holder, CreateTopicPageInfo.BaseNode node, int position) {
                if (position == 0 || position == nodes.size() - allNodes.size()) {
                    holder.getView(R.id.node_item_section_tv).setVisibility(View.VISIBLE);
                    if (position == 0) {
                        holder.setText(R.id.node_item_section_tv, "热门节点");
                    } else {
                        holder.setText(R.id.node_item_section_tv, "全部节点");
                    }
                } else {
                    holder.getView(R.id.node_item_section_tv).setVisibility(View.GONE);
                }
                holder.setText(R.id.node_name_tv, node.getTitle());
            }
        });
        mBaseRecyclerView.setAdapter(mAdapter);
    }

    @OnClick(R.id.node_select_cancle_btn)
    void onCancleClicked() {
        dismiss();
    }

}
