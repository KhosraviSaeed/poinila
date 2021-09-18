package com.shaya.poinila.android.presentation;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.onesignal.OneSignal;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.ContextHolder;
import com.shaya.poinila.android.util.DeviceInfoUtils;
import com.shaya.poinila.android.presentation.notification.OneSignalNotificationOpenedHelper;

import io.fabric.sdk.android.Fabric;
import org.json.JSONObject;

import java.util.Locale;

import data.PoinilaNetService;
import data.model.Member;
import manager.DBFacade;
import manager.DataRepository;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by iran on 2015-06-03.
 *
 * @author Alireza Farahani
 */
public class PoinilaApplication extends Application {

    //private RefWatcher refWatcher;

    private static Context mContext;

    private static String authToken;

    private Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);
        ContextHolder.setContext(getApplicationContext());
        FlowManager.init(new FlowConfig.Builder(this).build());        //refWatcher = LeakCanary.install(this);
        setLocale();
        //TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/BYekan.ttf");
        //TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/iransans.ttf");
        //TypefaceUtil.overrideFont(getApplicationContext(), "SERIF-LIGHT", "fonts/IRANSansLight.ttf");
        //TypefaceUtil.overrideFont(getApplicationContext(), "SERIF-BOLD", "fonts/IRANSansBold.ttf");
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.default_font_path))
                .setFontAttrId(R.attr.fontPath)
                .build());
        //OneSignal.startInit(this).init();
        //setupCrashReport();

        mContext = this;

        OneSignal
                .startInit(this)
                .setNotificationOpenedHandler(new OneSignalNotificationOpenedHelper(this))
//                .setAutoPromptLocation(true)

                .init();

        OneSignal.enableInAppAlertNotification(false);


        OneSignal.getTags(new OneSignal.GetTagsHandler() {
            @Override
            public void tagsAvailable(JSONObject tags) {

            }
        });


    }

    public  void logUser() {
        // You can call any combination of these three methods
        Member user = DBFacade.getCachedMyInfo();
        if(user != null){
            Crashlytics.setUserIdentifier(user.getId());
//            if(!TextUtils.isEmpty(user.email)) Crashlytics.setUserEmail(user.email);
            Crashlytics.setUserName(user.uniqueName);
        }
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(ConstantsUtils.GOOGLE_ANALYTICS_TRACKING_ID);
            mTracker.enableAutoActivityTracking(true);
            mTracker.setAppVersion(DeviceInfoUtils.CLIENT_VERSION_NAME);
        }
        return mTracker;
    }


    public static Context getAppContext(){
        return mContext;
    }

    public static String getAuthToken(){
        return authToken;
    }

    private void setupCrashReport() {
        // Setup handler for uncaught exceptions.
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                handleUncaughtException(thread, e);
            }
        });
    }

    private void handleUncaughtException(Thread thread, Throwable e) {
        PoinilaNetService.sendReport(ConstantsUtils.REPORT_TYPE_BUG, e.getClass().getSimpleName(), Log.getStackTraceString(e));
        throw new RuntimeException("");
        /*android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1); // crash with "unfortunately stopped blah blah*/

    }

    /*   public static RefWatcher getRefWatcher(Context context) {
           PoinilaApplication application = (PoinilaApplication) context.getApplicationContext();
           return application.refWatcher;
       }*/
    public void setLocale() {
        Locale myLocale = new Locale("fa");
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }


}


