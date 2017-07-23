package me.ghui.v2er.network.bean;

/**
 * Created by ghui on 23/07/2017.
 */

public class IgnoreReplyResultInfo extends BaseInfo {

    @Override
    public boolean isValid() {
        return rawResponse != null && rawResponse.contentLength() == 0;
    }

}
