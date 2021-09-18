package com.shaya.poinila.android.presentation.view.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.text.TextUtils;
import android.util.Log;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.utils.Utils;

import java.lang.ref.WeakReference;

import static com.shaya.poinila.android.util.ConstantsUtils.KEY_WEBSITE_URL;

/**
 * Created by iran on 6/19/2016.
 */
public class ChromeActivity extends Activity {

    private CustomTabsSession mCustomTabsSession;
    private CustomTabsClient mClient;
    private CustomTabsServiceConnection mConnection;
    private String mPackageNameToBind;
    private static WeakReference<CustomTabsSession> sCurrentSession;

    String url;
    String referrer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chrome_activity);

        url = getIntent().getStringExtra(KEY_WEBSITE_URL);
        referrer = getIntent().getStringExtra("referrer");

        bindCustomTabsService();

    }

    private void bindCustomTabsService() {
        if (mClient != null) return;
        mPackageNameToBind = Utils.getBrowserAvailablePackageName();
        if (TextUtils.isEmpty(mPackageNameToBind)) {
            finish();
        }

        mConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                mClient = customTabsClient;

                if(mClient != null){
                    mClient.warmup(0);
                    CustomTabsSession session = getSession();
                    if (mClient != null && session != null) session.mayLaunchUrl(Uri.parse(url), null, null);
                    launchUrl();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                finish();
            }
        };

        CustomTabsClient.bindCustomTabsService(ChromeActivity.this, mPackageNameToBind, mConnection);


    }

    @Override
    protected void onDestroy() {
        unbindCustomTabsService();
        super.onDestroy();
    }

    private void unbindCustomTabsService() {
        if (mConnection == null) return;
        unbindService(mConnection);
        mClient = null;
        mCustomTabsSession = null;
    }

    private CustomTabsSession getSession() {
        if (mClient == null) {
            mCustomTabsSession = null;
        } else if (mCustomTabsSession == null) {
            mCustomTabsSession = mClient.newSession(new CustomTabsCallback(){
                @Override
                public void onNavigationEvent(int navigationEvent, Bundle extras) {
                    super.onNavigationEvent(navigationEvent, extras);

                    switch (navigationEvent){
                        case 6: // Go to Parent Activity
                            ChromeActivity.this.finish();
                            break;
                    }
                }
            });
            setCurrentSession(mCustomTabsSession);
        }
        return mCustomTabsSession;
    }

    /**
     * @return The current {@link CustomTabsSession} object.
     */
    public static @Nullable
    CustomTabsSession getCurrentSession() {
        return sCurrentSession == null ? null : sCurrentSession.get();
    }

    /**
     * Sets the current session to the given one.
     * @param session The current session.
     */
    public static void setCurrentSession(CustomTabsSession session) {
        sCurrentSession = new WeakReference<CustomTabsSession>(session);
    }

    private void launchUrl(){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(getSession());
        builder
                .setToolbarColor(getResources().getColor(R.color.poinila_gray))
                .setShowTitle(true);
//        prepareMenuItems(builder);
//        prepareActionButton(builder);
//        prepareBottombar(builder);
//        builder.setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left);
//        builder.setExitAnimations(this, R.anim.slide_in_left, R.anim.slide_out_right);
//        builder.setCloseButtonIcon(
//                BitmapFactory.decodeResource(getResources(), R.drawable.ic_arrow_back));
        CustomTabsIntent customTabsIntent = builder.build();
        if(!TextUtils.isEmpty(referrer))
            customTabsIntent.intent.putExtra(Intent.EXTRA_REFERRER,Uri.parse(Intent.URI_ANDROID_APP_SCHEME + "//" + getPackageName()));
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

//    private void prepareMenuItems(CustomTabsIntent.Builder builder) {
//        Intent menuIntent = new Intent();
//        menuIntent.setClass(getApplicationContext(), this.getClass());
//        // Optional animation configuration when the user clicks menu items.
//        Bundle menuBundle = ActivityOptions.makeCustomAnimation(this, android.R.anim.slide_in_left,
//                android.R.anim.slide_out_right).toBundle();
//        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, menuIntent, 0,
//                menuBundle);
//        builder.addMenuItem("Menu entry 1", pi);
//    }
}
