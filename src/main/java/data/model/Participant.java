package data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by iran on 2015-08-15.
 */
public class Participant implements data.model.Identifiable {
    public int id;
    @SerializedName(value = "images") public data.model.ImageUrls imageUrls;
    @SerializedName(value = "type") public data.model.ImageUrls.ImageType type;
    @SerializedName(value = "unique_name")
    public String userName; // for member
    @SerializedName(value = "name")
    public String collectionName; // for collection
    @SerializedName(value = "title")
    public String postTitle; // post

    @Override
    public String getId() {
        return String.valueOf(id);
    }
}
