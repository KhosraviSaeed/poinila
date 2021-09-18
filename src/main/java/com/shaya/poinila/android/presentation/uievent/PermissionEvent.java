package com.shaya.poinila.android.presentation.uievent;


import data.event.BaseEvent;

/**
 * Created by iran on 1/18/2016.
 */
public class PermissionEvent extends BaseEvent {
    public String permissionString;

    public PermissionEvent(String permissionString) {

        this.permissionString = permissionString;
    }
    /*public final boolean granted;
    public final int requestCode;

    public PermissionEvent(boolean granted, int requestCode) {
        this.granted = granted;
        this.requestCode = requestCode;
    }*/
}
