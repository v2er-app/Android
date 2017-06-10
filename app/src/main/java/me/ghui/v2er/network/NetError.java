package me.ghui.v2er.network;

/**
 * Created by kyle on 2017/3/21.
 */

public class NetError extends Throwable {
	private int mCode;
	private String mErrMsg;

	public NetError(int code, String message) {
		super(message);
		mCode = code;
		mErrMsg = message;
	}

	public int getErrCode() {
		return mCode;
	}

	public String getErrMsg() {
		return mErrMsg;
	}

	@Override
	public String toString() {
		return "CIError{" +
				"mCode=" + mCode +
				", mErrMsg='" + mErrMsg + '\'' +
				'}';
	}
}
