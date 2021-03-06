package me.ghui.v2er.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.ItemViewDelegateAdapter;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.util.L;
import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.util.Theme;

/**
 * Created by ghui on 26/04/2017.
 */

public class LoadMoreRecyclerView extends BaseRecyclerView {

    private OnLoadMoreListener mOnLoadMoreListener;
    private LoadMoreScrollListener mScrollListener;
    private boolean mLoading = false;
    private ILoadMoreFooter.IdleStatus mStatus = ILoadMoreFooter.IdleStatus.INIT;
    // 是否正序浏览
    private boolean mIsLoadInOrder = true;
    private int mWillLoadPage = 1;

    private ILoadMoreFooter mLoadMoreFooter;

    public LoadMoreRecyclerView(Context context) {
        super(context);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        mOnLoadMoreListener = loadMoreListener;
        if (mOnLoadMoreListener != null) {
            addOnScrollListener(mScrollListener = new LoadMoreScrollListener());
        } else {
            removeOnScrollListener(mScrollListener);
        }
    }

    /**
     * 设置是否正序加载
     *
     * @param isInOrder 是否正序加载
     */
    public void setLoadOrder(boolean isInOrder) {
        mIsLoadInOrder = isInOrder;
    }


    public void resetWillLoadPage() {
        mWillLoadPage = mIsLoadInOrder ? 1 : 999;
    }

    public void setWillLoadPage(int willLoadPage) {
        mWillLoadPage = willLoadPage;
    }

    public void setHasMore(int total) {
        Adapter loadMoreAdapter = (Adapter) getAdapter();
        boolean hasMore = total > loadMoreAdapter.getContentItemCount();
        setHasMore(hasMore);
    }

    public void setHasMore(boolean hasMore) {
        mLoading = false;
        if (hasMore) {
            mStatus = ILoadMoreFooter.IdleStatus.HAS_MORE;
        } else {
            mStatus = ILoadMoreFooter.IdleStatus.NO_MORE;
        }
        if (mLoadMoreFooter != null) {
            mLoadMoreFooter.onIdle(mStatus);
        }
        mWillLoadPage = mIsLoadInOrder ? mWillLoadPage + 1 : mWillLoadPage - 1;
        L.i("mWillLoadPage: " + mWillLoadPage);
    }

    public boolean hasMore() {
        return mStatus == ILoadMoreFooter.IdleStatus.HAS_MORE;
    }

    public ILoadMoreFooter.IdleStatus getStatus() {
        return mStatus;
    }

    public void setAdapter(Adapter loadMoreAdapter) {
        super.setAdapter(loadMoreAdapter);
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int willLoadPage);
    }

    public interface ILoadMoreFooter {
        void onIdle(IdleStatus status);

        void onLoading();

        public enum IdleStatus {
            INIT, HAS_MORE, NO_MORE
        }
    }

    public static class CommonLoadMoreFooter extends androidx.appcompat.widget.AppCompatTextView implements ILoadMoreFooter {

        public CommonLoadMoreFooter(Context context) {
            super(context);
            init();
        }

        public CommonLoadMoreFooter(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public CommonLoadMoreFooter(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        private void init() {
//            setBackgroundColor(Color.WHITE);
            setVisibility(GONE);//default is gone
            setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.smallTextSize));
            setTextColor(Theme.getColor(R.attr.bodyTextColor, getContext()));
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            setLayoutParams(layoutParams);
            int padding = ScaleUtils.dp(20, getContext());
            setPadding(padding, padding, padding, padding);
            setGravity(Gravity.CENTER);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (getParent() instanceof LoadMoreRecyclerView) {
                LoadMoreRecyclerView parent = (LoadMoreRecyclerView) getParent();
                onIdle(parent.getStatus());
            }
        }

        @Override
        public void onIdle(IdleStatus status) {
            if (status == IdleStatus.HAS_MORE) {
                setVisibility(VISIBLE);
                setText("继续滑动加载更多");
            } else if (status == IdleStatus.NO_MORE) {
                setVisibility(VISIBLE);
                setText("全部加载完成");
            } else {
                setVisibility(GONE);
                setText(null);
            }
        }

        @Override
        public void onLoading() {
            setVisibility(VISIBLE);
            setText("加载中...");
        }
    }

    public static class Adapter<T> extends MultiItemTypeAdapter<T> {

        public Adapter(final Context context) {
            super(context);

            addItemViewDelegate(new ItemViewDelegateAdapter<T>(context) {

                @Override
                public View getItemView() {
                    return new LoadMoreRecyclerView.CommonLoadMoreFooter(context);
                }

                @Override
                public boolean isForViewType(T item, int position) {
                    return isLoadMoreFooterItem(position);
                }

            });
        }

        protected boolean isLoadMoreFooterItem(int position) {
            return position == getItemCount() - 1;
        }

        @Override
        public T getItem(int position) {
            if (isLoadMoreFooterItem(position)) return null;
            return super.getItem(position);
        }

        @Override
        public int getItemCount() {
            return super.getItemCount() + 1;
        }

        public int getContentItemCount() {
            return getItemCount() - 1;
        }

    }

    private class LoadMoreScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (!mLoading && hasMore() && newState == RecyclerView.SCROLL_STATE_IDLE) {
                LayoutManager layoutManager = getLayoutManager();
                View lastItem = null;
                if (layoutManager instanceof LinearLayoutManager) {
                    lastItem = layoutManager.findViewByPosition(((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition());
                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    int[] pos = ((StaggeredGridLayoutManager) layoutManager).findLastCompletelyVisibleItemPositions(null);
                    for (int p : pos) {
                        if ((lastItem = layoutManager.findViewByPosition(p)) instanceof ILoadMoreFooter)
                            break;
                    }
                }

                if (lastItem != null && lastItem instanceof ILoadMoreFooter) {
                    mLoading = true;
                    mLoadMoreFooter = (ILoadMoreFooter) lastItem;
                    mLoadMoreFooter.onLoading();
                    mOnLoadMoreListener.onLoadMore(mWillLoadPage);
                }
            }
        }
    }

}
