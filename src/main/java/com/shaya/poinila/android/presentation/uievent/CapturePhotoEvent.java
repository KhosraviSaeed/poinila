package com.shaya.poinila.android.presentation.uievent;

import android.content.Intent;

/**
 * Created by AlirezaF on 7/17/2015.
 */
public class CapturePhotoEvent {
    public final int requestCode;
    public final Intent intent;

    public CapturePhotoEvent(Intent takePictureIntent, int requestTakePhoto) {
        intent = takePictureIntent;
        requestCode = requestTakePhoto;
    }
}
