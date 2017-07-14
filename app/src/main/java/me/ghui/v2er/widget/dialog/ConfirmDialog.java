package me.ghui.v2er.widget.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import me.ghui.v2er.R;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.widget.MaxHeightScrollView;

/**
 * Created by ghui on 20/03/2017.
 */

public class ConfirmDialog extends BaseDialog {

    @BindView(R.id.dialog_root)
    LinearLayout mDialogRootView;
    @BindView(R.id.dialog_title)
    TextView mTitleTV;
    @BindView(R.id.dialog_msg_text)
    TextView mMsgTV;
    @BindView(R.id.negative_btn)
    Button mNegativeBtn;
    @BindView(R.id.positive_btn)
    Button mPositiveBtn;
    @BindView(R.id.dialog_msg_container_scrollview)
    MaxHeightScrollView scrollView;
    @BindView(R.id.dialog_btn_wrapper)
    LinearLayout mBtnWrapper;
    private boolean mOnlyOneBtn;

    public ConfirmDialog() {

    }

    @SuppressLint("ValidFragment")
    private ConfirmDialog(Builder builder) {
        super(builder);
    }

    @Override
    protected int attachLayoutResId() {
        return R.layout.confirm_dialog_content_layout;
    }

    @Override
    protected void onResizeDialog(Dialog dialog) {
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        float screenWidth = ScaleUtils.getScreenW();
        layoutParams.width = (int) (screenWidth * 0.85);
        int maxHeight = (int) (ScaleUtils.getScreenContentH() * 0.45f);
        scrollView.setMaxHeight(maxHeight);
        dialog.getWindow().setAttributes(layoutParams);

        //relayout btn wrapper
        if (mOnlyOneBtn) {
            int paddingH = layoutParams.width / 4;
            mBtnWrapper.setPadding(paddingH,
                    mBtnWrapper.getPaddingTop(), paddingH, ScaleUtils.dp(24));
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        // TODO: 14/07/2017 一个按钮时显示异常
        if (PreConditions.isEmpty(mTitle)) {
            mTitleTV.setVisibility(View.GONE);
        } else {
            mTitleTV.setVisibility(View.VISIBLE);
            mTitleTV.setText(mTitle);
        }

        if (PreConditions.isEmpty(mMsg)) {
            mMsgTV.setVisibility(View.GONE);
        } else {
            mMsgTV.setVisibility(View.VISIBLE);
            mMsgTV.setText(mMsg);
        }
        mOnlyOneBtn = (TextUtils.isEmpty(mPositiveBtnText) && !TextUtils.isEmpty(mNegativeBtnText) ||
                !TextUtils.isEmpty(mPositiveBtnText) && TextUtils.isEmpty(mNegativeBtnText));
        if (!TextUtils.isEmpty(mPositiveBtnText)) {
            attachClickListenerTo(mPositiveBtn, Behavior.POSITIVE);
            mPositiveBtn.setText(mPositiveBtnText);
            if (mOnlyOneBtn) {
                mNegativeBtn.setVisibility(View.GONE);
            }
        }
        if (!TextUtils.isEmpty(mNegativeBtnText)) {
            attachClickListenerTo(mNegativeBtn, Behavior.NEGATIVE);
            mNegativeBtn.setText(mNegativeBtnText);
            if (mOnlyOneBtn) {
                mPositiveBtn.setVisibility(View.GONE);
            }
        }

        if (TextUtils.isEmpty(mPositiveBtnText) && TextUtils.isEmpty(mNegativeBtnText)) {
            mBtnWrapper.setVisibility(View.GONE);
            setCancelable(true);
        }
    }

    public static class Builder extends BaseDialog.Builder<Builder> {

        public Builder(Activity activity) {
            super(activity);
        }

        @Override
        public ConfirmDialog build() {
            return new ConfirmDialog(this);
        }
    }

}


