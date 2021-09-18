package com.shaya.poinila.android.utils.uisynchronize;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.Serializable;

/**
 * Created by iran on 7/3/2016.
 */
public class UISynchronizeReceiver extends BroadcastReceiver {

    private UISynchronizeBus.UI_SYNCHRONIZE_ACTION action;
    private OnLoadDataSynchronizeListener onLoadDataSynchronizeListener;


    @Override
    public void onReceive(Context context, Intent intent) {
        Serializable data = intent.getSerializableExtra("data");
        UISynchronizeBus.UI_SYNCHRONIZE_ACTION action = UISynchronizeBus.UI_SYNCHRONIZE_ACTION.valueOf(intent.getAction());
        if(action == onLoadDataSynchronizeListener.getSynchronizeAction() || action == UISynchronizeBus.UI_SYNCHRONIZE_ACTION.ALL)
            onLoadDataSynchronizeListener.loadDataForSynchronize(data, action);
    }

    public UISynchronizeReceiver setOnLoadDataSynchronizeListener(OnLoadDataSynchronizeListener onLoadDataSynchronizeListener) {
        this.onLoadDataSynchronizeListener = onLoadDataSynchronizeListener;
        return this;
    }

    public interface OnLoadDataSynchronizeListener {
        public UISynchronizeBus.UI_SYNCHRONIZE_ACTION getSynchronizeAction();
        public void loadDataForSynchronize(Serializable data, UISynchronizeBus.UI_SYNCHRONIZE_ACTION action);
    }
}
