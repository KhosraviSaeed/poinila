package data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by iran on 2015-07-20.
 */
public enum Gender {
    @SerializedName(value = "female")
    FEMALE,
    @SerializedName(value = "male")
    MALE
}
