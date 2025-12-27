package me.ghui.v2er.module.imgur;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import me.ghui.v2er.R;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.network.imgur.ImgurUploadHistory;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.Voast;
import me.ghui.v2er.widget.BaseToolBar;
import me.ghui.v2er.widget.dialog.ConfirmDialog;

/**
 * Activity for displaying upload history
 */
public class MyUploadsActivity extends BaseActivity<BaseContract.IPresenter> {

    @BindView(R.id.uploads_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_view)
    LinearLayout mEmptyView;

    private MyUploadsAdapter mAdapter;

    public static void open(Context context) {
        Navigator.from(context)
                .to(MyUploadsActivity.class)
                .start();
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.act_my_uploads;
    }

    @Override
    protected void configToolBar(BaseToolBar toolBar) {
        super.configToolBar(toolBar);
        toolBar.setTitle(R.string.my_uploads);
        toolBar.inflateMenu(R.menu.my_uploads_menu);
        Utils.setPaddingForStatusBar(toolBar);
        Utils.setPaddingForNavbar(mRootView);
        toolBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_clear_history) {
                showClearHistoryDialog();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void init() {
        mAdapter = new MyUploadsAdapter(this);
        mAdapter.setOnItemClickListener((view, holder, position) -> {
            ImgurUploadHistory.UploadedImage image = mAdapter.getItem(position);
            if (image != null) {
                showImageOptionsDialog(image);
            }
        });

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(mAdapter);

        loadData();
    }

    @Override
    protected void reloadMode(int mode) {
        // No special reload needed for this activity
    }

    private void loadData() {
        List<ImgurUploadHistory.UploadedImage> images = ImgurUploadHistory.get().getUploadedImages();
        mAdapter.setData(images);

        if (images.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showImageOptionsDialog(ImgurUploadHistory.UploadedImage image) {
        new ConfirmDialog.Builder(this)
                .title(getString(R.string.copy_link))
                .msg(image.getLink())
                .positiveText(R.string.copy_link, dialog -> {
                    Utils.copyToClipboard(this, image.getLink());
                    Voast.show(getString(R.string.link_copied));
                })
                .negativeText(R.string.cancel)
                .build().show();
    }

    private void showClearHistoryDialog() {
        new ConfirmDialog.Builder(this)
                .title(getString(R.string.clear_upload_history))
                .msg(getString(R.string.clear_upload_history_confirm))
                .positiveText(R.string.ok, dialog -> {
                    ImgurUploadHistory.get().clearHistory();
                    loadData();
                    Voast.show(getString(R.string.history_cleared));
                })
                .negativeText(R.string.cancel)
                .build().show();
    }
}
