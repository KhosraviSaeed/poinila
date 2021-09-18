package com.shaya.poinila.android.utils;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Settings;

import com.shaya.poinila.android.presentation.PoinilaApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iran on 6/19/2016.
 */
public class Utils {

    public static String getBrowserAvailablePackageName(){
        Intent activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"));
        PackageManager pm = PoinilaApplication.getAppContext().getPackageManager();
        List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(
                activityIntent, PackageManager.MATCH_ALL);
        List<String> packagesSupportingCustomTabs = new ArrayList<>();
        for (ResolveInfo info : resolvedActivityList) {
            Intent serviceIntent = new Intent();
            serviceIntent.setAction("android.support.customtabs.action.CustomTabsService");
            serviceIntent.setPackage(info.activityInfo.packageName);
            if (pm.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info.activityInfo.packageName);
            }
        }
        return packagesSupportingCustomTabs.size() > 0 ? packagesSupportingCustomTabs.get(0) : null;
    }

    public static boolean isEnabledAutoRotate(){
        return android.provider.Settings.System.getInt(PoinilaApplication.getAppContext().getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
    }
}
