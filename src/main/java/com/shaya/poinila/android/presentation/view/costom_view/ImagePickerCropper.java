package com.shaya.poinila.android.presentation.view.costom_view;

import android.graphics.Bitmap;

/**
 * Created by iran on 12/1/2015.
 */
public interface ImagePickerCropper {
    void setImage(Bitmap bitmap);
    void setImage(String address);
    Bitmap getImageBitmap();
    Bitmap getCroppedImageBitmap();
    void cropImage();
    void removeImage();
    void rotate(int CWWDegrees);
}
