package com.shaya.poinila.android;

import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationPayload;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.utils.NotificationQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import data.model.Member;
import data.model.Post;
import manager.DBFacade;

import static com.shaya.poinila.android.utils.PushNotificationUtils.*;

/**
 * Created by iran on 6/7/2016.
 */
public class NotificationService extends NotificationExtenderService {



    @Override
    protected boolean onNotificationProcessing(final OSNotificationPayload notification) {

        Log.i(getClass().getName(), "additionalData : " + notification.additionalData);

        OverrideSettings overrideSettings = new OverrideSettings();
        overrideSettings.extender = new NotificationCompat.Extender() {
            @Override
            public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {

//                String message = createNotificationMessage(notification.additionalData);

//                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
//                builder.setContentText(message);
//                builder.setTicker(message);

                NOTIFICATION_TYPE group = getNotificationType(notification.additionalData);
                JSONArray data = NotificationQueue.getInstance().get(group.toString().toLowerCase());

                if(!group.equals(NOTIFICATION_TYPE.FRIENDSHIP_REQUEST)
                        && !group.equals(NOTIFICATION_TYPE.FRIENDSHIP_ANSWER)
                        && !group.equals(NOTIFICATION_TYPE.POST_SUGGESTION)){
                    JSONObject jsonModel = notification.additionalData.optJSONObject("object");

                    Logger.log("push data additionalData = " + notification.additionalData, Logger.LEVEL_INFO);


                    if(data == null)
                        data = new JSONArray();
                    try {
                        JSONObject ownerJs = new JSONObject(new Gson().toJson(DBFacade.getCachedMyInfo(), Member.class));
                        switch (group){
                            case FOLLOW:
                                JSONObject collectionJs = jsonModel.optJSONObject("collection");
                                collectionJs.put("owner", ownerJs);
                                data.put(collectionJs);
                                break;
                            default:
                                JSONObject postJs = jsonModel.optJSONObject("post");
                                postJs.put("poster", ownerJs);
                                JSONObject collJs = jsonModel.optJSONObject("post_collection");
                                postJs.put("collection", collJs);
                                data.put(postJs);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    NotificationQueue.getInstance().put(group.toString().toLowerCase(), data);
                }

                return builder;

            }
        };

        displayNotification(overrideSettings);

        return true;
    }


}
