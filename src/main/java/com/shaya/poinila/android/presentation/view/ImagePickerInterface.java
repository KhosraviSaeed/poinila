package com.shaya.poinila.android.presentation.view;

import android.net.Uri;

/**
 * Created by iran on 2015-07-21.
 */
public interface ImagePickerInterface {
    void onRemoveSelectedImage();

    void onImageReceived(String absolutePath, Uri mediaPath);

    void onCapturePhoto();

    void onSelectFromGallery();

}

