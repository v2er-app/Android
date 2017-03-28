package me.ghui.v2ex.network.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ghui on 25/03/2017.
 */

public class DailyHotInfo extends BaseInfo {

	private String id;
	private String title;
	private String url;
	private String content;
	private int replies;
	@SerializedName("created")
	private long createdTime;
	@SerializedName("last_modified")
	private long lastModifiedTime;
	private Member member;
	private Node node;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getReplies() {
		return replies;
	}

	public void setReplies(int replies) {
		this.replies = replies;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public long getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public static class Member {
		private String id;
		private String userName;
		private String avatar;
	}

	public static class Node {
		private String id;
		private String name;
		private String title;
		private String url;
		private int topics;
		private String avatar;
	}

}
