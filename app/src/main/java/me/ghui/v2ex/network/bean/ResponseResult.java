package me.ghui.v2ex.network.bean;

/**
 * Created by ghui on 25/03/2017.
 */

public class ResponseResult<T> {

	private int code;
	private String message;
	private T data;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
