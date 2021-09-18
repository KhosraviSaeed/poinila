package com.shaya.poinila.android.presentation.uievent;

import android.net.Uri;

/**
 * Created by AlirezaF on 7/17/2015.
 */
public class ImageSelectedUIEvent {
    public String absolutePath;
    public Uri mediaPath;

    public ImageSelectedUIEvent(String absolutePath, Uri mediaPath) {
        this.absolutePath = absolutePath;
        this.mediaPath = mediaPath;
    }

    public ImageSelectedUIEvent(ImageSelectedUIEvent event){
        this(event.absolutePath, event.mediaPath);
    }
}
