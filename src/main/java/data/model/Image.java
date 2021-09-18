package data.model;

import org.parceler.Parcel;

/**
 * Created by iran on 2015-07-20.
 */
@Parcel
public class Image {
    public Image(){}
    public Image(String url){
        this.url = url;
    }

    public String url;
    public int width;
    public int height;

    public Image(String url, int width, int height) {
        this.url = url;
        this.width = width;
        this.height = height;
    }
}
