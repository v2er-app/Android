package me.ghui.v2ex;

import junit.framework.TestCase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;

import me.ghui.v2ex.htmlpicker.Fruit;
import me.ghui.v2ex.network.bean.NewsInfo;

/**
 * Created by ghui on 16/04/2017.
 */

public class FruitTest extends TestCase {
	Fruit mFruit;
	Element html;

	@Override
	protected void setUp() throws Exception {
		html = Jsoup.connect("https://www.v2ex.com/?tab=all").get();
		mFruit = new Fruit();
	}

	public void testPicker() throws IOException {
		NewsInfo newsInfo = mFruit.fromHtml(html, NewsInfo.class);
		assert (newsInfo.getItems().size() > 50);
		System.out.println("newsInfo: " + newsInfo);
	}
}
