package me.ghui.v2er.network.bean;

import java.io.Serializable;

public class NodesNavInfoWrapper extends BaseInfo implements Serializable {

    public NodesNavInfo nodesNavInfo;

    private NodesNavInfoWrapper(NodesNavInfo nodesNavInfo) {
        this.nodesNavInfo = nodesNavInfo;
    }

    public static NodesNavInfoWrapper wrapper(NodesNavInfo nodesNavInfo) {
        return new NodesNavInfoWrapper(nodesNavInfo);
    }

    @Override
    public boolean isValid() {
        if (nodesNavInfo == null) return false;
        return nodesNavInfo.isValid();
    }
}
