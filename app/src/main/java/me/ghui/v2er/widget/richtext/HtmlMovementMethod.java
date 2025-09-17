package me.ghui.v2er.widget.richtext;

import androidx.annotation.NonNull;
import android.text.Layout;
import android.text.NoCopySpan;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import me.ghui.v2er.module.gallery.GalleryActivity;
import me.ghui.v2er.module.imgviewer.ImagesInfo;
import me.ghui.v2er.util.Utils;

/**
 * A movement method that traverses links in the text buffer and scrolls if necessary.
 * Supports clicking on links with DPad Center or Enter.
 * modified from {@link LinkMovementMethod} in Android 5.1
 */
public class HtmlMovementMethod extends ScrollingMovementMethod {
    private static final int CLICK = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;
    private static Object FROM_BELOW = new NoCopySpan.Concrete();
    private OnUrlClickListener mUrlClickListener;
    private OnImageClickListener mImageClickListener;
    private ImagesInfo.Images imgs;
    private TextView mTextView;

    public HtmlMovementMethod(OnUrlClickListener urlClickListener
            , OnImageClickListener imageClickListener, ImagesInfo.Images imgs, TextView textView) {
        this.mUrlClickListener = urlClickListener;
        this.mImageClickListener = imageClickListener;
        this.imgs = imgs;
        this.mTextView = textView;
    }

    @Override
    public boolean canSelectArbitrarily() {
        return true;
    }

    @Override
    protected boolean handleMovementKey(TextView widget, Spannable buffer, int keyCode,
                                        int movementMetaState, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if (KeyEvent.metaStateHasNoModifiers(movementMetaState)) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getRepeatCount() == 0 && action(CLICK, widget, buffer)) {
                        return true;
                    }
                }
                break;
        }
        return super.handleMovementKey(widget, buffer, keyCode, movementMetaState, event);
    }

    @Override
    protected boolean up(@NonNull TextView widget, Spannable buffer) {
        if (action(UP, widget, buffer)) {
            return true;
        }

        return super.up(widget, buffer);
    }

    @Override
    protected boolean down(@NonNull TextView widget, Spannable buffer) {
        if (action(DOWN, widget, buffer)) {
            return true;
        }

        return super.down(widget, buffer);
    }

    @Override
    protected boolean left(@NonNull TextView widget, Spannable buffer) {
        if (action(UP, widget, buffer)) {
            return true;
        }

        return super.left(widget, buffer);
    }

    @Override
    protected boolean right(@NonNull TextView widget, Spannable buffer) {
        if (action(DOWN, widget, buffer)) {
            return true;
        }

        return super.right(widget, buffer);
    }

    private boolean action(int what, TextView widget, Spannable buffer) {
        Layout layout = widget.getLayout();

        int padding = widget.getTotalPaddingTop() +
                widget.getTotalPaddingBottom();
        int areatop = widget.getScrollY();
        int areabot = areatop + widget.getHeight() - padding;

        int linetop = layout.getLineForVertical(areatop);
        int linebot = layout.getLineForVertical(areabot);

        int first = layout.getLineStart(linetop);
        int last = layout.getLineEnd(linebot);

        int a = Selection.getSelectionStart(buffer);
        int b = Selection.getSelectionEnd(buffer);

        int selStart = Math.min(a, b);
        int selEnd = Math.max(a, b);

        if (selStart < 0) {
            if (buffer.getSpanStart(FROM_BELOW) >= 0) {
                selStart = selEnd = buffer.length();
            }
        }

        if (selStart > last)
            selStart = selEnd = Integer.MAX_VALUE;
        if (selEnd < first)
            selStart = selEnd = -1;

        if (what == CLICK) {
            if (selStart == selEnd) {
                return false;
            }

            ClickableSpan[] link = buffer.getSpans(selStart, selEnd, ClickableSpan.class);

            if (link.length != 1) {
                return false;
            }

            onSpanClick(link[0]);

            return false;
        }

        ClickableSpan[] candidates = buffer.getSpans(first, last, ClickableSpan.class);

        switch (what) {
            case UP:
                int beststart, bestend;

                beststart = -1;
                bestend = -1;

                for (ClickableSpan candidate : candidates) {
                    int end = buffer.getSpanEnd(candidate);

                    if (end < selEnd || selStart == selEnd) {
                        if (end > bestend) {
                            beststart = buffer.getSpanStart(candidate);
                            bestend = end;
                        }
                    }
                }

                if (beststart >= 0) {
                    Selection.setSelection(buffer, bestend, beststart);
                    return true;
                }

                break;

            case DOWN:
                beststart = Integer.MAX_VALUE;
                bestend = Integer.MAX_VALUE;

                for (ClickableSpan candidate : candidates) {
                    int start = buffer.getSpanStart(candidate);

                    if (start > selStart || selStart == selEnd) {
                        if (start < beststart) {
                            beststart = start;
                            bestend = buffer.getSpanEnd(candidate);
                        }
                    }
                }

                if (bestend < Integer.MAX_VALUE) {
                    Selection.setSelection(buffer, beststart, bestend);
                    return true;
                }

                break;
        }

        return false;
    }

    private void onSpanClick(CharacterStyle span) {
        if (span instanceof URLSpan) {
            if (mUrlClickListener != null) {
                mUrlClickListener.onUrlClick(((URLSpan) span).getURL());
            } else {
                Utils.openWap(((URLSpan) span).getURL(), mTextView.getContext());
            }
        } else if (span instanceof ImageSpan) {
            String currentImg = ((ImageSpan) span).getSource();
            int index = 0;
            // Add null checks to prevent NullPointerException
            if (imgs != null && currentImg != null) {
                for (int i = 0; i < imgs.size(); i++) {
                    String imgUrl = imgs.get(i) != null ? imgs.get(i).getUrl() : null;
                    if (currentImg.equals(imgUrl)) {
                        index = i;
                        break;
                    }
                }
            }
            ImagesInfo imagesInfo = new ImagesInfo(index, imgs);
            if (mImageClickListener != null) {
                mImageClickListener.onImgClick(imagesInfo);
            } else {
//                ImageViewer.open(imagesInfo, mTextView.getContext());
                GalleryActivity.open(imagesInfo, mTextView.getContext());
            }
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull TextView widget, @NonNull Spannable buffer,
                                @NonNull MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            CharacterStyle[] spans = buffer.getSpans(off, off, ImageSpan.class);
            if (spans.length == 0) {
                spans = buffer.getSpans(off, off, ClickableSpan.class);
            }

            if (spans.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    onSpanClick(spans[0]);
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(spans[0]),
                            buffer.getSpanEnd(spans[0]));
                }
                return true;
            } else {
                Selection.removeSelection(buffer);
            }
        }

        return super.onTouchEvent(widget, buffer, event);
    }

    @Override
    public void initialize(TextView widget, Spannable text) {
        Selection.removeSelection(text);
        text.removeSpan(FROM_BELOW);
    }

    @Override
    public void onTakeFocus(@NonNull TextView view, Spannable text, int dir) {
        Selection.removeSelection(text);

        if ((dir & View.FOCUS_BACKWARD) != 0) {
            text.setSpan(FROM_BELOW, 0, 0, Spannable.SPAN_POINT_POINT);
        } else {
            text.removeSpan(FROM_BELOW);
        }
    }
}
