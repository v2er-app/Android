package me.ghui.v2er.util;

import android.util.TypedValue;
import android.widget.TextView;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.ViewHolder;

/**
 * Helper class to apply dynamic font sizing to ViewHolder items
 * Centralizes font size application logic to avoid code duplication
 */
public class ViewHolderFontHelper {

    /**
     * Apply standard font sizing to common list item layout
     * @param holder The ViewHolder containing the views
     * @param title The title text
     * @param userName The username text
     * @param time The time text
     * @param tag The tag text
     * @param commentNum The comment number text
     */
    public static void applyCommonListItemFonts(ViewHolder holder,
            String title, String userName, String time, String tag, String commentNum) {

        // Title
        TextView titleTv = holder.getTextView(R.id.title_tv);
        titleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getTitleSize());
        titleTv.setText(title);

        // Username
        if (userName != null) {
            TextView userNameTv = holder.getTextView(R.id.user_name_tv);
            userNameTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getSubTextSize());
            userNameTv.setText(userName);
        }

        // Time
        if (time != null) {
            TextView timeTv = holder.getTextView(R.id.time_tv);
            timeTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getSubTextSize());
            timeTv.setText(time);
        }

        // Tag
        if (tag != null) {
            TextView tagTv = holder.getTextView(R.id.tagview);
            tagTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getSubTextSize());
            tagTv.setText(tag);
        }

        // Comment number
        if (commentNum != null) {
            TextView commentTV = holder.getTextView(R.id.comment_num_tv);
            commentTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getSubTextSize());
            commentTV.setText(commentNum);
            ViewUtils.highlightCommentNum(commentTV);
        }
    }

    /**
     * Apply font size with custom IDs
     */
    public static void setTextWithSize(ViewHolder holder, int viewId, String text, float size) {
        TextView tv = holder.getTextView(viewId);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        tv.setText(text);
    }

    /**
     * Apply scaled font size to any TextView based on its current size
     * @param textView The TextView to scale
     */
    public static void applyScaledFontSize(TextView textView) {
        float currentSize = textView.getTextSize();
        float scaledSize = currentSize * FontSizeUtil.getScalingRatio();
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, scaledSize);
    }

    /**
     * Apply scaled font size to a TextView in ViewHolder
     */
    public static void applyScaledFontSize(ViewHolder holder, int viewId) {
        TextView tv = holder.getTextView(viewId);
        applyScaledFontSize(tv);
    }
}