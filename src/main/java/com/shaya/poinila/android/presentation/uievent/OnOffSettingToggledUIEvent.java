package com.shaya.poinila.android.presentation.uievent;

/**
 * Created by iran on 2015-09-07.
 */
public class OnOffSettingToggledUIEvent {
    public int adapterPosition;
    public boolean settingOn;

    public OnOffSettingToggledUIEvent(int adapterPosition, boolean settingOn) {
        this.adapterPosition = adapterPosition;
        this.settingOn = settingOn;
    }
}
