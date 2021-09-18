package com.shaya.poinila.android.presentation.uievent;

import com.shaya.poinila.android.presentation.view.activity.SettingActivity.SettingType;

/**
 * Created by iran on 2015-07-21.
 */
public class SimpleSettingTextSetEvent {
    public SettingType settingType;
    public String value;
    public int itemPosition;

    public SimpleSettingTextSetEvent(SettingType settingType, String value, int itemPosition) {
        this.settingType = settingType;
        this.value = value;
        this.itemPosition = itemPosition;
    }
}
