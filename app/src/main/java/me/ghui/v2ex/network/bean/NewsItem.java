package me.ghui.v2ex.network.bean;

import me.ghui.v2ex.htmlpicker.annotations.Select;

/**
 * Created by ghui on 10/04/2017.
 */
@Select("table > tbody > tr")
public class NewsItem {

	@Select(value = "span.item_title > a", attr = "text")
	private String title;
	@Select(value = "span.item_title > a", attr = "href")
	private String link;
	@Select(value = "td > a > img", attr = "src")
	private String avatar;
	@Select(value = "td > a", attr = "href")
	private String avatarLink;
	@Select(value = "span.'small fade' > strong > a", attr = "text")
	private String user;
	@Select(value = "span.'small fade'", attr = "text")
	private String time;
	@Select(value = "span.'small fade' > a", attr = "text")
	private String tagName;
	@Select(value = "span.'small fade' > a", attr = "href")
	private String tagLink;


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getAvatarLink() {
		return avatarLink;
	}

	public void setAvatarLink(String avatarLink) {
		this.avatarLink = avatarLink;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTagName() {
		// TODO: 11/04/2017 filtor
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getTagLink() {
		return tagLink;
	}

	public void setTagLink(String tagLink) {
		this.tagLink = tagLink;
	}

	@Override
	public String toString() {
		return "NewsItem{" +
				"title='" + title + '\'' +
				", link='" + link + '\'' +
				", avatar='" + avatar + '\'' +
				", avatarLink='" + avatarLink + '\'' +
				", user='" + user + '\'' +
				", time='" + time + '\'' +
				", tagName='" + tagName + '\'' +
				", tagLink='" + tagLink + '\'' +
				'}';
	}

}
