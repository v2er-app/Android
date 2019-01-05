package me.ghui.v2er.module.topic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.orhanobut.logger.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.Observable;
import me.ghui.toolbox.android.Assets;
import me.ghui.v2er.general.App;
import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.module.gallery.GalleryActivity;
import me.ghui.v2er.module.imgviewer.ImagesInfo;
import me.ghui.v2er.util.RxUtils;
import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.util.UriUtils;
import me.ghui.v2er.util.Utils;
import okio.Okio;

public class HtmlView extends WebView {
    private List<String> mImgs = new ArrayList<>();
    private OnHtmlRenderListener onHtmlRenderListener;

    public void setOnHtmlRenderListener(OnHtmlRenderListener onHtmlRenderListener) {
        this.onHtmlRenderListener = onHtmlRenderListener;
    }

    public HtmlView(Context context) {
        super(context);
        init();
    }

    public HtmlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        WebView.setWebContentsDebuggingEnabled(true);
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setBlockNetworkImage(false); // 解决图片不显示
        settings.setDatabaseEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        setWebViewClient(new V2exWebViewClient());
        setVerticalScrollBarEnabled(false);
        addJavascriptInterface(new ImgClickJSInterface(), "imagelistener");
    }

    public void loadContentView(String contentStr) {
        // add css style to contentView
        String formattedHtml = replaceImageSrc(contentStr);
        String container = Assets.getString("html/v2er.html", getContext());
        formattedHtml = String.format(container, formattedHtml);
        loadDataWithBaseURL(null, formattedHtml, "text/html", "UTF-8", null);
    }

    /**
     * 处理IMG标签
     * 将src属性值保存到original_src中，将默认的loading图路径赋值给src
     *
     * @param html
     * @return
     */
    private String replaceImageSrc(String html) {
        mImgs.clear();
        Document doc = Jsoup.parse(html);
        Elements es = doc.getElementsByTag("img");
        for (Element e : es) {
            String imgUrl = UriUtils.checkSchema(e.attr("src"));
            mImgs.add(imgUrl);
            e.attr("original_src", imgUrl);
            e.attr("src", "file:///android_asset/html/image_holder_loading.gif");
        }
        return doc.outerHtml();
    }

    private class V2exWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Utils.openWap(request.getUrl().toString(), getContext());
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // download the imgs in mImgs list
            downloadImgs();
            if (onHtmlRenderListener != null) {
                onHtmlRenderListener.onRenderCompleted();
            }
            addIMGClickListener();
        }

        /**
         * 获取本地资源
         */
        @Nullable
        private WebResourceResponse getWebResourceResponse(String url) {
            if(!mImgs.contains(url)) {
                return null;
            }
            try {
                File imgFile = GlideApp.with(App.get())
                        .downloadOnly()
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .submit(ScaleUtils.getScreenW(), ScaleUtils.getScreenW())
                        .get();
                try {
                    return new WebResourceResponse(UriUtils.getMimeType(url), "UTF-8", new FileInputStream(imgFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void downloadImgs() {
            for (String url : mImgs) {
                Logger.d("start download image: " + url);
                GlideApp.with(App.get())
                        .downloadOnly()
                        .load(url)
                        .listener(new RequestListener<File>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                                // TODO: 2018/12/23
                                return false;
                            }

                            @SuppressLint("CheckResult")
                            @Override
                            public boolean onResourceReady(File file, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                                Logger.d("image download finished: " + url);
                                Observable.just(file)
                                        .compose(RxUtils.io_main())
                                        .map(rawFile -> {
                                            String directory =rawFile.getParentFile().getPath();
                                            String name = rawFile.getName().split("\\.")[0] + "." + Utils.getTypeFromImgUrl(url);
                                            // check file is exist
                                            File imgFile = new File(directory, name);
                                            if(!imgFile.exists()) {
                                                try {
                                                    Okio.buffer(Okio.source(rawFile))
                                                            .readAll(Okio.sink(imgFile));
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            return "file://" + directory + File.separator + name;
                                        }).subscribe(localPath -> {
                                            Logger.d("reload image: " + localPath);
                                            HtmlView.this.loadUrl("javascript:reloadImg(" + "'" + url + "'" + "," + "'" + localPath + "'" + ");");
                                        });
                                return false;
                            }
                        }).submit();
            }
        }

//        /**
//         * 根据glide返回的文件及对应的图片url，返回对应的图片(png/jpg等格式的压缩图片，非.0格式的图片)
//         * 若本地无对应的图片，则从file中生成一份
//         * @param file
//         * @param url
//         * @return 本地压缩后的图片路径, file:///...
//         */
//        private String getLocalImagePath(File file, String url) {
//
//        }

        private void addIMGClickListener() {
            HtmlView.this.post(() -> HtmlView.this.loadUrl("javascript:addClickToImg()"));
        }

    }

    private class ImgClickJSInterface {
        @JavascriptInterface
        public void openImage(int index, String[] imgs) {
            ImagesInfo imagesInfo = new ImagesInfo(index, imgs);
            GalleryActivity.open(imagesInfo, getContext());
        }

    }

    public interface OnHtmlRenderListener {
        void onRenderCompleted();
    }

}
