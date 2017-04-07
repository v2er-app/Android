package me.ghui.v2ex.network.bean;

import com.github.florent37.retrojsoup.annotations.JsoupSrc;

/**
 * Created by ghui on 04/04/2017.
 */

public class NewsInfo {
//	@JsoupText(".entry-title a")
//	public String title;
//
//	@JsoupHref(".read-more a")
//	public String href;
//
//	@JsoupSrc(".entry-thumb img")
//	public String image;
//
//	@JsoupText(".entry-content p")
//	public String description;
//
//	public Article() {
//	}
//
//	@Override
//	public String toString() {
//		return title + "\n" +
//				href + "\n" +
//				image + "\n" +
//				description + "\n";
//	}

	@JsoupSrc(".cell item img")
	public String avatar;

	@Override
	public String toString() {
		return "NewsInfo{" +
				"avatar='" + avatar + '\'' +
				'}';
	}
}
