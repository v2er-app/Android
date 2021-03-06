package me.ghui.v2er.adapter.base;

import android.content.Context;
import androidx.annotation.CallSuper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.List;

import me.ghui.v2er.util.Check;
import me.ghui.v2er.R;
import me.ghui.v2er.general.Pref;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.LoadMoreRecyclerView;

/**
 * Created by zhy on 16/4/9.
 */
public class MultiItemTypeAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    protected Context mContext;
    protected List<T> mDatas;

    protected ItemViewDelegateManager mItemViewDelegateManager;
    protected OnItemClickListener mOnItemClickListener;
    protected OnItemLongClickListener mOnItemLongClickListener;
    protected OnItemCountChangedListener mOnItemCountChangedListener;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;

    public MultiItemTypeAdapter(Context context) {
        mContext = context;
        mItemViewDelegateManager = new ItemViewDelegateManager();
    }

    public void setData(List<T> data, boolean shouldAppend) {
        if (shouldAppend) {
            if (Check.isEmpty(data)) return;
            mDatas.addAll(data);
            notifyItemRangeChanged(mDatas.size() - data.size(), data.size());
        } else {
            mDatas = data;
            notifyDataSetChanged();
        }
        if (mOnItemCountChangedListener != null) {
            mOnItemCountChangedListener.onCountChanged(Utils.listSize(mDatas));
        }
    }

    public void setData(List<T> data) {
        setData(data, false);
    }

    public T getItem(int position) {
        return Check.isEmpty(mDatas) ? null : mDatas.get(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        mLayoutManager = recyclerView.getLayoutManager();
    }

    @Override
    public int getItemViewType(int position) {
        if (!useItemViewDelegateManager()) return super.getItemViewType(position);
        return mItemViewDelegateManager.getItemViewType(getItem(position), position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewDelegate itemViewDelegate = mItemViewDelegateManager.getItemViewDelegate(viewType);
        int layoutId = itemViewDelegate.getItemViewLayoutId();
        ViewHolder holder;
        if (itemViewDelegate instanceof ItemViewDelegateAdapter && ((ItemViewDelegateAdapter) itemViewDelegate).getItemView() != null) {
            holder = ViewHolder.createViewHolder(mContext, ((ItemViewDelegateAdapter) itemViewDelegate).getItemView());
        } else {
            holder = ViewHolder.createViewHolder(mContext, parent, layoutId);
        }
        onViewHolderCreated(holder, holder.getConvertView());
        bindListener(holder, viewType);
        return holder;
    }

    public void onViewHolderCreated(ViewHolder holder, View itemView) {

    }

    public void convert(ViewHolder holder, T t) {
        mItemViewDelegateManager.convert(holder, t, holder.getAdapterPosition());
    }

    protected boolean isEnabled(int viewType) {
        return true;
    }

    @CallSuper
    protected void bindListener(ViewHolder holder, int viewType) {
        if (!isEnabled(viewType)) return;
        holder.getConvertView().setOnClickListener(v -> {
            if (mOnItemClickListener != null && !(v instanceof LoadMoreRecyclerView.ILoadMoreFooter)) {
                mOnItemClickListener.onItemClick(v, holder, holder.getAdapterPosition());
            }
        });

        holder.getConvertView().setOnLongClickListener(v -> {
            if (mOnItemLongClickListener != null) {
                return mOnItemLongClickListener.onItemLongClick(v, holder, holder.getAdapterPosition());
            }
            return false;
        });
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        convert(holder, getItem(position));
        if (shouldAnimate() && !Pref.readBool(R.string.pref_key_close_list_animate)) {
            clearAnimation(holder.itemView);
            animate(holder.itemView, position);
        }
    }

    protected boolean shouldAnimate() {
        return false;
    }

    private void clearAnimation(View itemView) {
        if (itemView.getAnimation() != null) {
            itemView.clearAnimation();
        }
    }

    private void animate(View itemView, int position) {
        int state = mRecyclerView.getScrollState();
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            return;
        }
        if (mLayoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager layoutmanager = (LinearLayoutManager) mLayoutManager;
            int firstVisiableItem = layoutmanager.findFirstVisibleItemPosition();
            int lastVisiableItem = layoutmanager.findLastVisibleItemPosition();
            if (position > lastVisiableItem) {
                animateIn(itemView, position);
            } else if (position < firstVisiableItem) {
                animateOut(itemView, position);
            }
        }
    }

    protected void animateIn(View itemView, int position) {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.list_item_scroll_down);
        itemView.startAnimation(animation);
    }

    protected void animateOut(View itemView, int position) {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.list_item_scroll_up);
        itemView.startAnimation(animation);
    }


    @Override
    public int getItemCount() {
        return Utils.listSize(mDatas);
    }


    public List<T> getDatas() {
        return mDatas;
    }

    public MultiItemTypeAdapter addItemViewDelegate(ItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(itemViewDelegate);
        return this;
    }

    public MultiItemTypeAdapter addItemViewDelegate(int viewType, ItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(viewType, itemViewDelegate);
        return this;
    }

    protected boolean useItemViewDelegateManager() {
        return mItemViewDelegateManager.getItemViewDelegateCount() > 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemCountChangedListener(OnItemCountChangedListener onItemCountChangedListener) {
        mOnItemCountChangedListener = onItemCountChangedListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, ViewHolder holder, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, ViewHolder holder, int position);
    }

    public interface OnItemCountChangedListener {
        void onCountChanged(int newCount);
    }

}
