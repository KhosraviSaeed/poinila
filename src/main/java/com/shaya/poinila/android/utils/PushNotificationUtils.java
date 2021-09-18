package com.shaya.poinila.android.utils;

import android.content.Intent;
import android.util.Log;

import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.PoinilaApplication;
import com.shaya.poinila.android.presentation.view.activity.OthersProfileActivity;
import com.shaya.poinila.android.util.NavigationUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import data.PoinilaNetService;
import data.model.FriendRequestAnswer;
import manager.DataRepository;

import static com.shaya.poinila.android.util.ConstantsUtils.KEY_MEMBER_ID;

/**
 * Created by iran on 6/12/2016.
 */
public class PushNotificationUtils {

    // Notification Type
    public enum NOTIFICATION_TYPE{
        POST_SUGGESTION, FRIENDSHIP_REQUEST, FRIENDSHIP_ANSWER, LIKE, FOLLOW, COMMENT
    }

    // Notification Key
    public static final String NOTIF_TYPE = "notif_type";
    public static final String MEMBERS = "members";
    public static final String NUMBER = "number";
    public static final String FULL_NAME = "member_full_name";
    public static final String MEMBER = "member";
    public static final String CIRCLE_ID = "circle_id";
    public static final String MEMBER_ID = "member_id";
    public static final String MEMBER_REQUEST = "member_request";
    public static final String MEMBER_ANSWER = "member_answer";
    public static final String COLLECTIONS = "collections";
    public static final String POSTS = "posts";

    public static NOTIFICATION_TYPE getNotificationType(JSONObject notificationData){
        String notif_type = notificationData.optString(NOTIF_TYPE);
        return NOTIFICATION_TYPE.valueOf(notif_type.toUpperCase());
    }

    private static void log(String str){
        Log.i(PushNotificationUtils.class.getName(), str);
    }


    public static void fireNotificationAction(JSONObject additionalData){

        String actionSelected = additionalData.optString("actionSelected");
        switch (actionSelected){
            case "accept":
                FriendshipAccept(additionalData);
//                NotificationOpenedFriendship(additionalData);
                break;
            case "decline":
                FriendshipDecline(additionalData);
//                NotificationOpenedFriendship(additionalData);
                break;
            default:
                fireNotificationOpened(additionalData);

        }
    }

    public static void fireNotificationOpened(JSONObject additionalData){
        switch (getNotificationType(additionalData)){
            case POST_SUGGESTION:
                notificationOpenedSuggestion(additionalData);
                break;
            case FRIENDSHIP_REQUEST:
                NotificationOpenedFriendship(additionalData);
                break;
            case FRIENDSHIP_ANSWER:
                NotificationOpenedFriendship(additionalData);
                break;
            case LIKE:
                notificationOpenedLike(additionalData);
                break;
            case FOLLOW:
                notificationOpenedFollow(additionalData);
                break;
            case COMMENT:
                notificationOpenedComment(additionalData);
                break;
        }
    }

    public static void notificationOpenedFollow(JSONObject additionalData){
        JSONArray collectionListJs = NotificationQueue.getInstance().get(NOTIFICATION_TYPE.FOLLOW.toString().toLowerCase());
        PageChanger.gotToNotificationActivity(NOTIFICATION_TYPE.FOLLOW, collectionListJs.toString());
    }

    public static void notificationOpenedLike(JSONObject additionalData){
        JSONArray postListJs = NotificationQueue.getInstance().get(NOTIFICATION_TYPE.LIKE.toString().toLowerCase());
        PageChanger.gotToNotificationActivity(NOTIFICATION_TYPE.LIKE, postListJs.toString());
    }

    public static void NotificationOpenedFriendship(JSONObject additionalData){
        String memberId = String.valueOf(additionalData.optInt("object"));
        DataRepository.getInstance().putTempModel(null);
        NavigationUtils.goToActivity(OthersProfileActivity.class,
                PoinilaApplication.getAppContext(), KEY_MEMBER_ID, memberId, Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static void notificationOpenedComment(JSONObject additionalData){
        JSONArray postListJs = NotificationQueue.getInstance().get(NOTIFICATION_TYPE.COMMENT.toString().toLowerCase());
        PageChanger.gotToNotificationActivity(NOTIFICATION_TYPE.COMMENT, postListJs.toString());
    }

    public static void notificationOpenedSuggestion(JSONObject additionalData){

        PageChanger.gotToNotificationActivity(NOTIFICATION_TYPE.POST_SUGGESTION, additionalData.optJSONArray("object").toString());
    }

    /**
     * Friendship Accept
     * @param additionalData
     */
    public static void FriendshipAccept(JSONObject additionalData){
        int member_id = additionalData.optInt("object");
//        int circle_id = additionalData.optInt("");
        PoinilaNetService.answerFriendRequest(member_id, FriendRequestAnswer.ACCEPT, 0);

    }

    /**
     * Friendship Decline
     * @param additionalData
     */
    public static void FriendshipDecline(JSONObject additionalData){
        int member_id = additionalData.optInt("object");
//        int circle_id = additionalData.optInt(CIRCLE_ID);
        PoinilaNetService.answerFriendRequest(member_id, FriendRequestAnswer.REJECT, 0);
    }

}
