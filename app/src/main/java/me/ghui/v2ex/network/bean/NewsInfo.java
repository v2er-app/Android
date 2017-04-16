package me.ghui.v2ex.network.bean;


import java.util.List;

import me.ghui.v2ex.htmlpicker.annotations.Select;

/**
 * Created by ghui on 04/04/2017.
 */

@Select("div.box")
public class NewsInfo {
	@Select("div.cell.item")
	private List<NewsItem> items;

	public List<NewsItem> getItems() {
		return items;
	}

	public void setItems(List<NewsItem> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return "NewsInfo{" +
				"items=" + items +
				'}';
	}
}
