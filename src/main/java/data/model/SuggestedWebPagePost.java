package data.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by AlirezaF on 10/15/2015.
 */
@Parcel
public class SuggestedWebPagePost {

    /* fields directly filled with converter (GSON)*/
    @SerializedName(value = "title")
    public String name;
    @SerializedName(value = "caption")
    public String summary;
    public List<String> tags; // comes from server in string format, what can I do? :(
    public List<Image> images;
    public String content;

    /* fields we must set by hand!*/
    public String imageAddress;
    public String siteAddress;

    @SerializedName(value = "video")
    public String videoAddress;
}
