package me.ghui.v2er.network.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

import me.ghui.toolbox.android.Check;

// https://v2ex.com/api/nodes/s2.json

public class NodesInfo extends ArrayList<NodesInfo.Node> implements IBase, Serializable {
    private String mResponseBody;

    @Override
    public boolean isValid() {
        if (size() <= 0) return true;
        return Check.notEmpty(get(0).id);
    }

    @Override
    public String getResponse() {
        return mResponseBody;
    }

    @Override
    public void setResponse(String response) {
        mResponseBody = response;
    }

    public static class Node implements Serializable, Parcelable, Comparable<Node> {
        public String text;
        public int topics;
        public String id;
        public boolean isHot;

        protected Node(Parcel in) {
            text = in.readString();
            topics = in.readInt();
            id = in.readString();
            isHot = in.readByte() != 0;
        }

        public static final Creator<Node> CREATOR = new Creator<Node>() {
            @Override
            public Node createFromParcel(Parcel in) {
                return new Node(in);
            }

            @Override
            public Node[] newArray(int size) {
                return new Node[size];
            }
        };

        @Override
        public String toString() {
            return "Item{" +
                    "text='" + text + '\'' +
                    ", topics=" + topics +
                    ", id='" + id + '\'' +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(text);
            dest.writeInt(topics);
            dest.writeString(id);
            dest.writeByte((byte) (isHot ? 1 : 0));
        }

        @Override
        public int compareTo(@NonNull Node o) {
            int flag1 = this.isHot ? 0 : 1;
            int flag2 = o.isHot ? 0 : 1;
            return flag1 - flag2;
        }
    }

}
