package me.ghui.v2ex;

import junit.framework.TestCase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;

import me.ghui.v2ex.htmlpicker.HtmlPicker;
import me.ghui.v2ex.network.bean.NewsInfo;

/**
 * Created by ghui on 16/04/2017.
 */

public class HtmlPickerTest extends TestCase {
	HtmlPicker htmlPicker;
	Element html;

	@Override
	protected void setUp() throws Exception {
		html = Jsoup.connect("https://www.v2ex.com/?tab=all").get();
		htmlPicker = new HtmlPicker();
	}

	public void testPicker() throws IOException {
		NewsInfo newsInfo = htmlPicker.fromHtml(html, NewsInfo.class);
		assert (newsInfo.getItems().size() > 50);
		System.out.println("newsInfo: " + newsInfo);
	}
}
