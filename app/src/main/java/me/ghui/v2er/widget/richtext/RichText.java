package me.ghui.v2er.widget.richtext;

/**
 * Created by ghui on 05/07/2017.
 */

//RichText
//        .from(text) // 数据源
//        .type(RichText.TYPE_MARKDOWN) // 数据格式,不设置默认是Html,使用fromMarkdown的默认是Markdown格式
//        .autoFix(true) // 是否自动修复，默认true
//        .autoPlay(true) // gif图片是否自动播放
//        .showBorder(true) // 是否显示图片边框
//        .borderColor(Color.RED) // 图片边框颜色
//        .borderSize(10) // 边框尺寸
//        .borderRadius(50) // 图片边框圆角弧度
//        .scaleType(ImageHolder.ScaleType.FIT_CENTER) // 图片缩放方式
//        .size(ImageHolder.MATCH_PARENT, ImageHolder.WRAP_CONTENT) // 图片占位区域的宽高
//        .fix(imageFixCallback) // 设置自定义修复图片宽高
//        .fixLink(linkFixCallback) // 设置链接自定义回调
//        .noImage(true) // 不显示并且不加载图片
//        .resetSize(false) // 默认false，是否忽略img标签中的宽高尺寸（只在img标签中存在宽高时才有效），true：忽略标签中的尺寸并触发SIZE_READY回调，false：使用img标签中的宽高尺寸，不触发SIZE_READY回调
//        .clickable(true) // 是否可点击，默认只有设置了点击监听才可点击
//        .imageClick(onImageClickListener) // 设置图片点击回调
//        .imageLongClick(onImageLongClickListener) // 设置图片长按回调
//        .urlClick(onURLClickListener) // 设置链接点击回调
//        .urlLongClick(onUrlLongClickListener) // 设置链接长按回调
//        .placeHolder(placeHolder) // 设置加载中显示的占位图
//        .error(errorImage) // 设置加载失败的错误图
//        .cache(Cache.ALL) // 缓存类型，默认为Cache.ALL（缓存图片和图片大小信息和文本样式信息）
//        .imageGetter(yourImageGetter) // 设置图片加载器，默认为DefaultImageGetter，使用okhttp实现
//        .bind(tag) // 绑定richText对象到某个object上，方便后面的清理
//        .done(callback) // 解析完成回调
//        .into(textView); // 设置目标TextView

public class RichText {

    public static RichTextConfig from(String htmlText) {
        return new RichTextConfig(htmlText);
    }

}
