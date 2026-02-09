package me.ghui.v2er.module.vshare;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonAdapter;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.ActivityReloader;
import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.bean.VshareVersionInfo;
import me.ghui.v2er.util.L;
import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.BaseRecyclerView;
import me.ghui.v2er.widget.BaseToolBar;

public class VshareActivity extends BaseActivity<BaseContract.IPresenter>
        implements MultiItemTypeAdapter.OnItemClickListener {

    @BindView(R.id.base_recyclerview)
    BaseRecyclerView mRecyclerView;

    private CommonAdapter<VshareVersionInfo.Item> mAdapter;
    private Disposable mDisposable;
    private final RequestOptions mIconOptions = RequestOptions.bitmapTransform(
            new RoundedCorners(ScaleUtils.dp(12)));

    public static void open(Context context) {
        Intent intent = new Intent(context, VshareActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_vshare;
    }

    @Override
    protected void configToolBar(BaseToolBar toolBar) {
        super.configToolBar(toolBar);
        Utils.setPaddingForStatusBar(toolBar);
    }

    @Override
    protected void reloadMode(int mode) {
        ActivityReloader.target(this).reload();
    }

    @Override
    protected void init() {
        mAdapter = new CommonAdapter<VshareVersionInfo.Item>(this, R.layout.item_vshare_product) {
            @Override
            protected void convert(ViewHolder holder, VshareVersionInfo.Item item, int position) {
                holder.setText(R.id.vshare_item_name, item.getName());
                holder.setText(R.id.vshare_item_desc, item.getDescription());

                ImageView iconView = holder.getView(R.id.vshare_item_icon);
                String iconUrl = item.getIcon();
                if (iconUrl != null && iconUrl.startsWith("/")) {
                    iconUrl = "https://v2er.app" + iconUrl;
                }
                GlideApp.with(mContext)
                        .load(iconUrl)
                        .apply(mIconOptions)
                        .placeholder(R.drawable.avatar_placeholder_drawable)
                        .into(iconView);
            }
        };

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        loadData();
    }

    private void loadData() {
        showLoading();
        mDisposable = APIService.get()
                .getVshareVersion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(versionInfo -> {
                    hideLoading();
                    if (versionInfo != null && versionInfo.isValid()
                            && versionInfo.getItems() != null) {
                        mAdapter.setData(versionInfo.getItems());
                    }
                }, throwable -> {
                    hideLoading();
                    L.e("VshareActivity load error: " + throwable.getMessage());
                });
    }

    @Override
    public void onItemClick(View view, ViewHolder holder, int position) {
        VshareVersionInfo.Item item = mAdapter.getDatas().get(position);
        if (item.getUrl() != null) {
            Utils.openInBrowser(item.getUrl(), this);
        }
    }

    @Override
    protected void onDestroy() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        super.onDestroy();
    }
}
