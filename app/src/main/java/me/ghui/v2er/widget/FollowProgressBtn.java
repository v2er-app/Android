package me.ghui.v2er.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.ghui.v2er.R;

/**
 * Created by ghui on 27/07/2017.
 */

public class FollowProgressBtn extends FrameLayout {

    @BindView(R.id.status_imgview)
    ImageView statusView;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;


    public FollowProgressBtn(Context context) {
        super(context);
        init(context, null);
    }

    public FollowProgressBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FollowProgressBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.follow_progress_btn, this);
        ButterKnife.bind(this);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FollowProgressBtn, 0, 0);
        try {
            String initText = a.getString(R.styleable.FollowProgressBtn_initText);
            int initIcon = a.getResourceId(R.styleable.FollowProgressBtn_initIcon, R.drawable.progress_button_follow_normal_icon);
            mTitleTv.setText(initText);
            statusView.setImageResource(initIcon);
        } finally {
            a.recycle();
        }
    }

    public void setStatus(@StatusInt int status, String hint, @DrawableRes int drawableId) {
        mTitleTv.setText(hint);
        switch (status) {
            case NORMAL:
                statusView.setVisibility(VISIBLE);
                mProgressBar.setVisibility(INVISIBLE);
                statusView.setImageResource(drawableId);
                break;
            case FINISHED:
                statusView.setVisibility(VISIBLE);
                mProgressBar.setVisibility(INVISIBLE);
                statusView.setImageResource(drawableId);
                break;
            case UPDATING:
                statusView.setVisibility(INVISIBLE);
                mProgressBar.setVisibility(VISIBLE);
                break;
        }
    }

    public void startUpdate() {
        setStatus(UPDATING, mTitleTv.getText().toString(), -1);
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NORMAL, UPDATING, FINISHED})
    public @interface StatusInt {
    }

    public static final int NORMAL = 0;
    public static final int UPDATING = 1;
    public static final int FINISHED = 2;

}
