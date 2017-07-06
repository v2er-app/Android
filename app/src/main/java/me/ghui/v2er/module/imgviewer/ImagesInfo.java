package me.ghui.v2er.module.imgviewer;


import java.io.Serializable;
import java.util.ArrayList;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;

/**
 * Created by ghui on 06/07/2017.
 */

public class ImagesInfo implements Serializable {
    private int position;
    private Images images;

    public ImagesInfo(int position, Images images) {
        this.position = position;
        this.images = images;
    }

    public int getPosition() {
        return position;
    }

    public Images getImages() {
        return images;
    }

    @Pick(value = "img")
    public static class Images extends ArrayList<Images.Image> implements Serializable {

        public static class Image implements Serializable {
            @Pick(attr = Attrs.SRC)
            private String url;

            public String getUrl() {
                return url;
            }

            @Override
            public String toString() {
                return "Image{" +
                        "url='" + url + '\'' +
                        '}';
            }
        }
    }

}
