package com.shaya.poinila.android.presentation.notification;

import android.content.Context;
import android.util.Log;

import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import data.PoinilaNetService;
import data.model.FriendRequestAnswer;

import static com.shaya.poinila.android.utils.PushNotificationUtils.*;

/**
 * Created by iran on 6/8/2016.
 */
public class OneSignalNotificationOpenedHelper implements  OneSignal.NotificationOpenedHandler {


    Context context;

    public OneSignalNotificationOpenedHelper(Context context){
        this.context = context;
    }

    @Override
    public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {

        Log.i(getClass().getName(), "additionalData = " + additionalData);
        Log.i(getClass().getName(), "message = " + message);
        Log.i(getClass().getName(), "isActive = " + isActive);

        JSONArray notifArray = additionalData.optJSONArray("stacked_notifications");
        JSONObject objJS = null;
        if(notifArray != null){
            try {
                objJS = notifArray.length()> 0 ? notifArray.getJSONObject(0) : null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        fireNotificationAction(objJS == null ? additionalData: objJS);

    }


}
