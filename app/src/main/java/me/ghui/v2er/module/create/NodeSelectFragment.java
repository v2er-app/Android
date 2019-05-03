package me.ghui.v2er.module.create;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckedTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.ghui.toolbox.android.Check;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonAdapter;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.network.bean.CreateTopicPageInfo;
import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.BaseRecyclerView;

import static me.ghui.v2er.util.Utils.KEY;

/**
 * Created by ghui on 05/06/2017.
 */

public class NodeSelectFragment extends DialogFragment implements MultiItemTypeAdapter.OnItemClickListener {
    private static final String NODE_ALL = KEY("node_all");

    @BindView(R.id.node_select_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.base_recyclerview)
    BaseRecyclerView mBaseRecyclerView;

    private FilterAdapter mAdapter;
    private List<CreateTopicPageInfo.BaseNode> mNodes;

    public static NodeSelectFragment newInstance(CreateTopicPageInfo topicPageInfo) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(NODE_ALL, (ArrayList<? extends Parcelable>) topicPageInfo.getNodes());
        NodeSelectFragment fragment = new NodeSelectFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNodes = getArguments().getParcelableArrayList(NODE_ALL);
        mAdapter = new FilterAdapter(getActivity(), R.layout.item_select_node);
        mAdapter.setData(mNodes);
        mAdapter.setOnItemClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        window.setDimAmount(0.45f);
        window.setBackgroundDrawableResource(R.drawable.confirm_dialog_bg);
        View view = inflater.inflate(R.layout.node_select, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        resize(getDialog());
    }

    private void resize(Dialog dialog) {
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        float screenWidth = ScaleUtils.getScreenW();
        layoutParams.width = (int) (screenWidth * 0.9);
        layoutParams.height = (int) (ScaleUtils.getScreenContentH() * 0.8f);
        dialog.getWindow().setAttributes(layoutParams);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mToolbar.setNavigationIcon(null);
        mToolbar.inflateMenu(R.menu.search_note_menu);
        mToolbar.setOnMenuItemClickListener(menuItem -> {
            Toast.makeText(getActivity(), "do search", Toast.LENGTH_SHORT).show();
            return true;
        });
        SearchView searchView = (SearchView) mToolbar.getMenu().findItem(R.id.action_search_node).getActionView();
        searchView.setQueryHint("搜索全部节点");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mAdapter.getFilter().filter(query);
                return true;
            }
        });

        mBaseRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBaseRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(View view, ViewHolder holder, int position) {
        CheckedTextView checkedTextView = holder.getView(R.id.node_name_tv);
        checkedTextView.setChecked(true);
        OnSelectedListener onSelectedListener = (OnSelectedListener) getActivity();
        onSelectedListener.onSelected(mAdapter.getItem(position));
        dismiss();
    }

    public interface OnSelectedListener {
        void onSelected(CreateTopicPageInfo.BaseNode node);
    }

    private class FilterAdapter extends CommonAdapter<CreateTopicPageInfo.BaseNode> implements Filterable {
        private ValueFilter mValueFilter;

        public FilterAdapter(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        protected void convert(ViewHolder holder, CreateTopicPageInfo.BaseNode node, int position) {
            if (position == 0 ||
                    (node instanceof CreateTopicPageInfo.Node && getItem(position - 1) instanceof CreateTopicPageInfo.HotNode)) {
                holder.getView(R.id.node_item_section_tv).setVisibility(View.VISIBLE);
                if (node instanceof CreateTopicPageInfo.HotNode) {
                    holder.setText(R.id.node_item_section_tv, "热门节点");
                } else {
                    holder.setText(R.id.node_item_section_tv, "全部节点");
                }
            } else {
                holder.getView(R.id.node_item_section_tv).setVisibility(View.GONE);
            }
            holder.setText(R.id.node_name_tv, node.getTitle() + " / " + node.getId());
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
                if (Check.isEmpty(constraint)) {
                    filterResults.values = mNodes;
                    filterResults.count = Utils.listSize(mNodes);
                    return filterResults;
                }
                List<CreateTopicPageInfo.BaseNode> resultList = new ArrayList<>();
                for (CreateTopicPageInfo.BaseNode node : mNodes) {
                    if (node.getId().contains(constraint) || node.getTitle().contains(constraint)) {
                        resultList.add(node);
                    }
                }
                filterResults.count = resultList.size();
                filterResults.values = resultList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                setData((List<CreateTopicPageInfo.BaseNode>) results.values);
                notifyDataSetChanged();
            }
        }
    }

}

