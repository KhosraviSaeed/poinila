package data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by iran on 12/15/2015.
 */
public enum SuggestionReason {
    @SerializedName("picked_for_you")
    PickedForYou,
    @SerializedName("found_in_interest")
    FoundInInterest,
    @SerializedName("found_in_collection")
    Following,
}
