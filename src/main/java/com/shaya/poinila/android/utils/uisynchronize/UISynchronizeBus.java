package com.shaya.poinila.android.utils.uisynchronize;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.shaya.poinila.android.presentation.PoinilaApplication;

import java.io.Serializable;

/**
 * Created by iran on 7/3/2016.
 */
public class UISynchronizeBus {

    private static UISynchronizeBus instance;
    private final String INTENT_FILTER_NAME = "com.shaya.poinila.ui.synchronize";
    private IntentFilter intentFilter;
    private Intent intent;
    private LocalBroadcastManager localBroadcastManager;
    private UISynchronizeReceiver uiSynchronizeReceiver;

    public enum UI_SYNCHRONIZE_ACTION{
        OFF,
        ALL,
        UPDATE_DASHBOARD_POST
    }


    private UISynchronizeBus(){

        uiSynchronizeReceiver = new UISynchronizeReceiver();

        localBroadcastManager = LocalBroadcastManager.getInstance(PoinilaApplication.getAppContext());

        intentFilter = new IntentFilter(INTENT_FILTER_NAME);

        for (UI_SYNCHRONIZE_ACTION action : UI_SYNCHRONIZE_ACTION.values()) {
            intentFilter.addAction(action.toString());
        }

        intent = new Intent(INTENT_FILTER_NAME);


        localBroadcastManager.registerReceiver(
                uiSynchronizeReceiver,
                intentFilter
        );

    }

    public UISynchronizeReceiver getReceiver(){
        return uiSynchronizeReceiver;
    }

    public static UISynchronizeBus getInstance(){

        if(instance == null){
            instance = new UISynchronizeBus();
        }

        return instance;
    }

    public void sendData(UI_SYNCHRONIZE_ACTION action, Serializable data){
        intent.setAction(action.toString());
        intent.putExtra("data", data);
        localBroadcastManager.sendBroadcast(intent);
    }



}
