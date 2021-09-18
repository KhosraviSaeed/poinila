package com.shaya.poinila.android.presentation.uievent;

import android.net.Uri;

/**
 * Created by iran on 2015-09-13.
 */
public class ProfilePicSelectedEvent {
    public String absolutePath;
    public Uri mediaPath;

    public ProfilePicSelectedEvent(String absolutePath, Uri mediaPath) {
        this.absolutePath = absolutePath;
        this.mediaPath = mediaPath;
    }
}
