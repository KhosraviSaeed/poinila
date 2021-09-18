package com.shaya.poinila.android.presentation.uievent;

import android.content.Intent;

/**
 * Created by AlirezaF on 7/17/2015.
 */
public class SelectImageEvent {
    public final Intent intent;
    public int requestCode;

    public SelectImageEvent(Intent selectImageIntent, int requestSelectImage) {
        intent = selectImageIntent;
        requestCode = requestSelectImage;
    }
}
