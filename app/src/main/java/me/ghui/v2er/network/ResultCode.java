package me.ghui.v2er.network;

/**
 * Created by ghui on 24/07/2017.
 */

public interface ResultCode {
    int SUCCESS = 0;
    int NETWORK_ERROR = -1;
    int LOGIN_EXPIRED = -2;
    int LOGIN_NEEDED = -3;
    int REDIRECT_TO_HOME = -4;
    int LOGIN_TWO_STEP = -5;
}
