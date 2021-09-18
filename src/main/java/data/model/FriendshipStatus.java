package data.model;

import com.google.gson.annotations.SerializedName;

/**

 * Created by iran on 2015-11-14.
 */
public enum FriendshipStatus {
    @SerializedName("friend") IsFriend,
    @SerializedName("not_friend") NotFriend,
    @SerializedName("waiting_for_my_response") WaitingForAction,
    @SerializedName("my_request_pending") Pending,
}
