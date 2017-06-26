package me.ghui.v2er.widget.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import butterknife.ButterKnife;

/**
 * Created by ghui on 20/03/2017.
 */

public class BaseDialog extends DialogFragment implements View.OnClickListener {

    static final String EXTRA_DIALOG_TITLE_KEY = "extra_dialog_title_key";
    static final String EXTRA_DIALOG_MESSAGE_KEY = "extra_dialog_message_key";
    static final String EXTRA_DIALOG_CANELABLE_KEY = "extra_dialog_cancelable_key";
    static final String EXTRA_NEGATIVE_BTN_KEY = "extra_dialog_negative_btn_key";
    static final String EXTRA_POSITIVE_BTN_KEY = "extra_dialog_positive_btn_key";
    static final String EXTRA_AUTO_DISMISS = "extra_auto_dismiss";


    CharSequence mTitle;
    CharSequence mMsg;
    CharSequence mNegativeBtnText;
    CharSequence mPositiveBtnText;
    private boolean mCancleable;
    private boolean mAutoDismiss;

    private OnDialogClickListener mOnPositiveBtnClickListener;
    private OnDialogClickListener mOnNegativeBtnClickListener;
    private Activity mActivity;

    public BaseDialog() {

    }

    @SuppressLint("ValidFragment")
    public BaseDialog(Builder builder) {
        mActivity = builder.activity;
        this.mOnPositiveBtnClickListener = builder.onPositiveBtnClickListener;
        this.mOnNegativeBtnClickListener = builder.onNegativeBtnClickListner;

        Bundle args = new Bundle();
        args.putCharSequence(EXTRA_DIALOG_TITLE_KEY, builder.title);
        args.putCharSequence(EXTRA_DIALOG_MESSAGE_KEY, builder.msg);
        args.putBoolean(EXTRA_DIALOG_CANELABLE_KEY, builder.cancelable);
        args.putCharSequence(EXTRA_NEGATIVE_BTN_KEY, builder.negativeText);
        args.putCharSequence(EXTRA_POSITIVE_BTN_KEY, builder.positiveText);
        args.putBoolean(EXTRA_AUTO_DISMISS, builder.autoDismiss);
        this.setArguments(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseArgs(getArguments());
        setCancelable(mCancleable);
    }

    @Override
    public void onStart() {
        super.onStart();
        onResizeDialog(getDialog());
    }

    protected void onResizeDialog(Dialog dialog) {
    }

    @CallSuper
    protected void parseArgs(Bundle args) {
        mTitle = args.getCharSequence(EXTRA_DIALOG_TITLE_KEY);
        mMsg = args.getCharSequence(EXTRA_DIALOG_MESSAGE_KEY);
        mNegativeBtnText = args.getCharSequence(EXTRA_NEGATIVE_BTN_KEY);
        mPositiveBtnText = args.getCharSequence(EXTRA_POSITIVE_BTN_KEY);
        mCancleable = args.getBoolean(EXTRA_DIALOG_CANELABLE_KEY);
        mAutoDismiss = args.getBoolean(EXTRA_AUTO_DISMISS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        window.setDimAmount(0.15f);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setBackgroundDrawableResource(android.R.color.transparent);

        View view = null;
        if (attachLayoutResId() != 0) {
            view = inflater.inflate(attachLayoutResId(), container);
            ButterKnife.bind(this, view);
        }
        return view;
    }

    @LayoutRes
    protected int attachLayoutResId() {
        return 0;
    }

    public void show() {
        this.show(mActivity.getFragmentManager(), this.getTag());
    }

    protected final void attachClickListenerTo(View btn, Behavior type) {
        btn.setTag(type);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() == Behavior.NEGATIVE) {
            if (mOnNegativeBtnClickListener != null) {
                mOnNegativeBtnClickListener.onClick(BaseDialog.this);
            }
        } else {
            if (mOnPositiveBtnClickListener != null) {
                mOnPositiveBtnClickListener.onClick(BaseDialog.this);
            }
        }

        if (mAutoDismiss && getDialog().isShowing()) {
            dismiss();
        }
    }

    enum Behavior {
        NEGATIVE, POSITIVE
    }

    public interface OnDialogClickListener {
        void onClick(BaseDialog dialog);
    }

    public static abstract class Builder<T extends Builder> {

        Activity activity;
        CharSequence title;
        CharSequence msg;
        boolean cancelable = true;
        boolean autoDismiss = true;
        CharSequence negativeText;
        CharSequence positiveText;
        OnDialogClickListener onPositiveBtnClickListener;
        OnDialogClickListener onNegativeBtnClickListner;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public T title(CharSequence title) {
            this.title = title;
            return (T) this;
        }

        public T msg(CharSequence msg) {
            this.msg = msg;
            return (T) this;
        }

        public T cancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return (T) this;
        }

        public T negativeText(CharSequence negativeText, OnDialogClickListener onDialogClickListener) {
            this.negativeText = negativeText;
            this.onNegativeBtnClickListner = onDialogClickListener;
            return (T) this;
        }

        public T negativeText(CharSequence negativeText) {
            return this.negativeText(negativeText, null);
        }

        public T negativeText(@StringRes int textResId, OnDialogClickListener onDialogClickListener) {
            return this.negativeText(activity.getText(textResId), onDialogClickListener);
        }

        public T negativeText(@StringRes int textResId) {
            return this.negativeText(activity.getText(textResId), null);
        }

        public T positiveText(CharSequence positiveText, OnDialogClickListener onDialogClickListener) {
            this.positiveText = positiveText;
            this.onPositiveBtnClickListener = onDialogClickListener;
            return (T) this;
        }

        public T positiveText(@StringRes int textResId, OnDialogClickListener onDialogClickListener) {
            return this.positiveText(activity.getText(textResId), onDialogClickListener);
        }

        public T positiveText(CharSequence positiveText) {
            return this.positiveText(positiveText, null);
        }

        public T autoDismiss(boolean autoDismiss) {
            this.autoDismiss = autoDismiss;
            return (T) this;
        }

        public abstract BaseDialog build();

    }

}
