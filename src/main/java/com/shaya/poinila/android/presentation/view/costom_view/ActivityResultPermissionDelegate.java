package com.shaya.poinila.android.presentation.view.costom_view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.PermissionEvent;
import com.shaya.poinila.android.presentation.view.activity.BaseActivity;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.util.PoinilaPreferences;

import static android.app.Activity.RESULT_OK;

/**
 * Created by iran on 12/13/2015.
 */
public interface ActivityResultPermissionDelegate {
    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);


    // --------------------


    abstract class SimpleActivityResultPermissionDelegate implements ActivityResultPermissionDelegate {

        public void startForResult(AppCompatActivity activity, Intent intent, int requestCode) {
            activity.startActivityForResult(intent, requestCode);
        }

        public void startForResult(android.support.v4.app.Fragment fragment, Intent intent, int requestCode) {
            fragment.startActivityForResult(intent, requestCode);
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode != RESULT_OK)
                return;
            handleValidResults(requestCode, data);
        }

        public abstract void handleValidResults(int requestCode, Intent data);

        public void askForPermission(Activity activity, String permission, int requestCode){
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }

        public void askForPermission(android.support.v4.app.Fragment fragment, String permission, int requestCode){
            fragment.requestPermissions(new String[]{permission}, requestCode);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == BaseActivity.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE){
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    handlePermissionGranted();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    handlePermissionDenied();
                }
            }
        }

        public abstract void handlePermissionDenied();

        public abstract void handlePermissionGranted();
    }

    // ---------------------

    abstract class ImagePickerResultPermissionDelegate extends SimpleActivityResultPermissionDelegate {
        protected String imageAddress;
        public String getImageAddress() {
            return imageAddress;
        }

        @Override
        public void handleValidResults(int requestCode, Intent data) {

            switch (requestCode) {
                case ConstantsUtils.REQUEST_CODE_TAKE_PHOTO:
                    imageAddress = PoinilaPreferences.getCapturedImageAddress(); // using no media_output in capture image intent
                    break;
                case ConstantsUtils.REQUEST_CODE_PICK_IMAGE:
                    imageAddress = data.getData().toString();
                    break;
            }
        }

        public void handlePermissionDenied(){
            Logger.toast(R.string.permission_reason_camera);
        }
    }

    // -----------------------
/*
    class SmsPermissionHandler extends SimpleActivityResultPermissionDelegate{

        @Override
        public void handlePermissionDenied() {
            Logger.longToast(getString(R.string.permission_reason_sms));
            smsPermissionDeclined = true;
        }

        @Override
        public void handlePermissionGranted() {
            getActivity().registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        }

        public registerSmsReceiver(Activity activity){

        }

        @Override
        public void handleValidResults(int requestCode, Intent data) {}// we don't start activity for result, a better design perhaps.
    }*/
}
