package me.ghui.v2er.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;

import java.io.ByteArrayOutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.ghui.v2er.R;
import me.ghui.v2er.general.App;
import me.ghui.v2er.general.PreConditions;

/**
 * Created by ghui on 29/08/2017.
 */

public class ShareManager {

    private static final int THUMB_SIZE = 150;
    private ShareData mShareData;
    private Context mContext;

    public ShareManager(ShareData shareData, Context context) {
        mShareData = shareData;
        mContext = context;
    }

    public static class ShareData {
        private String title;
        private String content;
        private String img;
        private String link;

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getImg() {
            return img;
        }

        public String getLink() {
            return link;
        }

        private ShareData() {
        }

        public static class Builder {
            private String title;
            private String content;
            private String img;
            private String link;

            public Builder(String title) {
                this.title = title;
            }

            public Builder content(String content) {
                this.content = content;
                return this;
            }

            public Builder img(String img) {
                this.img = img;
                return this;
            }

            public Builder link(String link) {
                this.link = link;
                return this;
            }

            public ShareData build() {
                ShareData shareData = new ShareData();
                shareData.title = title;
                shareData.content = content;
                shareData.img = img;
                shareData.link = link;
                return shareData;
            }

        }

        public static final int SESSION = 0;
        public static final int CIRCLE = 1;
        public static final int FAVORITE = 2;

        @IntDef({SESSION, CIRCLE, FAVORITE})
        @Retention(RetentionPolicy.SOURCE)
        public @interface SENCE {
        }
    }

    private Target mTarget;

    public void shareToWechat(@ShareData.SENCE int type) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = mShareData.link;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = PreConditions.notEmpty(mShareData.title) && mShareData.title.length() > 512 ? mShareData.title.substring(0, 512) : mShareData.title;
        msg.description = PreConditions.notEmpty(mShareData.content) && mShareData.content.length() > 1024 ? mShareData.content.substring(0, 1024) : mShareData.content;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = msg;
        req.scene = type;

        if (PreConditions.notEmpty(mShareData.img)) {
            mTarget = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    msg.thumbData = bmpToByteArray(bitmap, false);
                    App.get().wechat().sendReq(req);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    msg.thumbData = bmpToByteArray(getDefaultBitmap(), false);
                    App.get().wechat().sendReq(req);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };
            Picasso.with(mContext).load(mShareData.img)
                    .error(R.mipmap.ic_launcher)
                    .resize(THUMB_SIZE, THUMB_SIZE)
                    .into(mTarget);
        } else {
            msg.thumbData = bmpToByteArray(getDefaultBitmap(), false);
            App.get().wechat().sendReq(req);
        }
    }

    private static Bitmap getDefaultBitmap() {
        Bitmap bmp = BitmapFactory.decodeResource(App.get().getResources(), R.drawable.ic_v2er);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        return thumbBmp;
    }


    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
