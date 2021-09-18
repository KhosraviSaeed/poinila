package data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by iran on 2015-08-15.
 */
public class Notification {

    @SerializedName(value = "notifiable_action_type") public NotificationType type;
    @SerializedName(value = "main_actor") public Participant mainActor;
    public boolean seen;
    public List<Participant> participants;
    // TODO: chera "last"?. mage update mishe?
    @SerializedName(value = "last_creation_time") public Date lastCreationTime;

    public ImageUrls.ImageType getParticipantImageType() {
        switch (type){
            case MY_POST_LIKED:
            case MY_COLLECTION_FOLLOWED:
            case COMMENT_MY_POST:
            case MY_POST_REPOSTED:
            case COMMENT_AFTER_YOUR_COMMENT:
            case FRIENDSHIP_ACCEPTED:
                return ImageUrls.ImageType.MEMBER;

            case FRIENDS_FOLLOWED_COLLECTIONS:
            case FRIENDS_CREATED_COLLECTIONS:
                return ImageUrls.ImageType.COLLECTION;
            case FRIENDS_LIKED_POSTS:
                return ImageUrls.ImageType.POST;
            default:
                return ImageUrls.ImageType.MEMBER;
        }

    }
    // for now its unused
    // @SerializedName(value = "notification_view_type") public Date lastCreationTime;



    public enum NotificationType{
        /*-----My notifs-------*/
        @SerializedName(value = "other_like_own_post")
        MY_POST_LIKED,
        @SerializedName(value = "other_follow_own_collection")
        MY_COLLECTION_FOLLOWED,
        @SerializedName(value = "comment_on_own_post")
        COMMENT_MY_POST,
        @SerializedName(value = "repost_post")
        MY_POST_REPOSTED,
        @SerializedName(value = "comment_on_post_that_people_commented_on")
        COMMENT_AFTER_YOUR_COMMENT,
        @SerializedName(value = "accepted_friendship_invitation")
        FRIENDSHIP_ACCEPTED,

        /*-----Other's Notifs-------*/
        @SerializedName(value = "friend_follow_other_collection")
        FRIENDS_FOLLOWED_COLLECTIONS,
        @SerializedName(value = "friend_create_collection")
        FRIENDS_CREATED_COLLECTIONS,
        @SerializedName(value = "friend_like_other_post")
        FRIENDS_LIKED_POSTS,

    }
}
