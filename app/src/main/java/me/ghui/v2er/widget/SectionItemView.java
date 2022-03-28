package me.ghui.v2er.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import me.ghui.v2er.R;
import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.util.Utils;

public class SectionItemView extends RelativeLayout implements View.OnClickListener {
    private Drawable icon;
    private String title;
    private boolean showDivider;
    private TextView sectionTitle;
    private ImageView sectionIcon;
    private DividerView sectionDivider;

    private OnSectionClickListener onSectionClickListener;

    public void setOnSectionClickListener(OnSectionClickListener onSectionClickListener) {
        this.onSectionClickListener = onSectionClickListener;
    }

    public interface OnSectionClickListener {
        void onClick(View v);
    }

    public SectionItemView(@NonNull Context context) {
        super(context);
        initView(context, null);
    }

    public SectionItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public SectionItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public SectionItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray styledAttrs = getContext().getTheme().obtainStyledAttributes(attrs,
                    R.styleable.SectionItemView, 0, 0);
            icon = styledAttrs.getDrawable(R.styleable.SectionItemView_icon);
            title = styledAttrs.getString(R.styleable.SectionItemView_title);
            showDivider = styledAttrs.getBoolean(R.styleable.SectionItemView_show_divider, true);
            styledAttrs.recycle();
        }
    }

    private ViewGroup rootView;
    private RelativeLayout sectionLayout;

    private void initView(Context context, AttributeSet attrs) {
        initAttrs(context, attrs);
        rootView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.section_item_view_layout, this, true);
        sectionLayout = rootView.findViewById(R.id.section_layout);
        sectionTitle = rootView.findViewById(R.id.section_title);
        sectionIcon = rootView.findViewById(R.id.section_icon);
        sectionDivider = rootView.findViewById(R.id.section_divider);
        sectionLayout.setOnClickListener(this);
        if (icon != null) {
            sectionIcon.setImageDrawable(icon);
        }
        if (!TextUtils.isEmpty(title)) {
            sectionTitle.setText(title);
        }
        if (!showDivider) {
            sectionDivider.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (onSectionClickListener != null) {
            onSectionClickListener.onClick(this);
        }
    }

}
