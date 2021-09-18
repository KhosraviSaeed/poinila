package com.shaya.poinila.android.presentation.view.activity;

import android.os.Bundle;
import android.os.Handler;

import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.util.ConnectionUitls;
import com.shaya.poinila.android.util.NavigationUtils;
import com.shaya.poinila.android.util.PoinilaPreferences;
import com.shaya.poinila.android.utils.PonilaAccountManager;
import com.squareup.otto.Subscribe;

import data.PoinilaNetService;
import data.event.MyInfoReceivedEvent;
import data.model.Member;
import manager.DBFacade;
import manager.DataRepository;


public class SplashActivity extends BaseActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2500;
    private long start;
    private boolean responseIsReceived = false;
    private long MIN_SPLASH_DURATION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void handleToolbar() {

    }

    @Override
    protected void onStart() {
        super.onStart();

        sendPreliminaryRequests();
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if (!responseIsReceived) {
                    navigateFromSplashScreen();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void initUI() {

    }

    private void navigateFromSplashScreen() {
//        Member cachedMember = DBFacade.getCachedMyInfo();
//        boolean hasAnonymouslyLoggedInBefore = responseIsReceived ?
//                DataRepository.isUserAnonymous() :
//                (cachedMember == null || cachedMember.isAnonymous); // first time || has cached guest info

        if(PonilaAccountManager.getInstance().ponilaAccountExists()){
            goToDashboard();
        }else if (!PoinilaPreferences.hasSeenHelp())
            PageChanger.goToHelpActivity(getActivity(), false);
        else
            goToLogin();

//        if (!TextUtils.isEmpty(PoinilaPreferences.getAuthToken()) && !hasAnonymouslyLoggedInBefore) {
//            // normal authenticated user
//            goToDashboard();
//        } else if (!PoinilaPreferences.hasSeenHelp())
//            PageChanger.goToHelpActivity(getActivity(), false);
//        else {
//            goToLogin();
//        }
    }

    private void sendPreliminaryRequests() {
        //start = System.currentTimeMillis();
        DataRepository.getInstance();
        PoinilaNetService.getRemainedInvites(); // DataRepository receives the event
        PoinilaNetService.getServerTime();
        PoinilaNetService.getSystemPreferences();
    }

    private void goToLogin() {
        PageChanger.goToLoginActivity(getActivity());
        finish();
    }

    private void goToDashboard() {
        NavigationUtils.goToActivity(MainActivity.class, getActivity());
        finish();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_splash;
    }


}
