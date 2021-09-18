package com.shaya.poinila.android.presentation.uievent;

/**
 * Created by iran on 2015-08-01.
 */
public class ChangePasswordUIEvent {
    public final String oldPass;
    public final String newPass;

    public ChangePasswordUIEvent(String oldPass, String newPass) {
        this.oldPass = oldPass;
        this.newPass = newPass;
    }
}
