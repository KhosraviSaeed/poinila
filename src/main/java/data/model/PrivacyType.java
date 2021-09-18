package data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by iran on 2015-07-06.
 */
public enum PrivacyType {
    @SerializedName(value = "public")
    PUBLIC,
    @SerializedName(value = "private")
    PRIVATE
}
