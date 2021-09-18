package com.shaya.poinila.android.presentation.view.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.NavigationUtils;
import com.shaya.poinila.android.util.PoinilaPreferences;

import butterknife.ButterKnife;
import manager.DataRepository;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.shaya.poinila.android.util.ConstantsUtils.INTENT_FILTER_JWT;
import static com.shaya.poinila.android.util.ConstantsUtils.INTENT_FILTER_SERVER_TIME;

/**
 * Created by iran on 2015-06-21.
 *
 * @author Alireza Farahani
 */
public abstract class BaseActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100;
    public static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 200;
    protected boolean requestOnFirstTime = true;

    protected String titleParameter;
    protected ProgressDialog progressDialog;
    private View progressView;
    private ViewGroup rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntentExtras();
        if(isForcePortrait())
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // TODO: crash reporting here.
    /*    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                if (paramThrowable instanceof AuthorizationException) {

                } else {
                    paramThrowable.printStackTrace();
                }
            }
        });*/
        setContentView(getActivityView());

        ButterKnife.bind(this);

        initLoadingDialog();

        initUI();

    }

    public boolean isForcePortrait(){
        return true;
    }

    private void initLoadingDialog() {
        progressDialog = new ProgressDialog(this);
        //progressDialog.setTitle(R.string.loading);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    protected void showProgressDialog(){
        progressDialog.show();
    }

    protected void dismissProgressDialog(){
        progressDialog.dismiss();
    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mJWTReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String activityName = BaseActivity.this.getClass().getSimpleName();
            if (activityName.equals(SignUpLoginActivity.class.getSimpleName()) ||
                    activityName.equals(SplashActivity.class.getSimpleName()) ||
                    activityName.equals(HelpActivity.class.getSimpleName()))
                return;

            /*PoinilaPreferences.putAuthToken(null);
            DataRepository.setUserAsAnonymous(true);*/
            DataRepository.logout();
            PageChanger.goToLoginActivity(BaseActivity.this);
        }
    };

    private BroadcastReceiver mServerTimeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long timeDifference = intent.getLongExtra(ConstantsUtils.KEY_TIME_DIFFERENCE, (long) (3.5 * 60 * 60 * 1000)); // ba utc 3.5 hour tafavot darim
            DataRepository.getInstance().putServerTimeDifference(timeDifference);
        }
    };

/*    private boolean requestingIsLocked;
    private BroadcastReceiver mRequestFailedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            requestingIsLocked = false;
        }
    };*/

    protected View getActivityView() {
        return getLayoutInflater().inflate(getLayoutResourceId(), null);
    }

    @Override
    protected void onStart() {
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        super.onStart();
        BusProvider.getBus().register(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mJWTReceiver,
                new IntentFilter(INTENT_FILTER_JWT));
        LocalBroadcastManager.getInstance(this).registerReceiver(mServerTimeReceiver,
                new IntentFilter(INTENT_FILTER_SERVER_TIME));
        /*LocalBroadcastManager.getInstance(this).registerReceiver(mRequestFailedReceiver,
                new IntentFilter(ConstantsUtils.INTENT_FILTER_REQUEST_FAILED));*/
    }

    @Override
    protected void onStop() {
        BusProvider.getBus().unregister(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mJWTReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mServerTimeReceiver);
        super.onStop();
    }

    protected abstract void initUI();


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                handleUpNavigation();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void handleUpNavigation() {
        // I don't know if this trick is flawless or not!
        // code from http://stackoverflow.com/a/20631508/1660013
        /*if (!TextUtils.isEmpty(NavUtils.getParentActivityName(this))) {
//            NavUtils.navigateUpFromSameTask(this);
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            if (NavUtils.shouldUpRecreateTask(this, upIntent)
                    || getIntent().getAction() != null) { // deep linked: force new stack
                // create new task
                TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent)
                        .startActivities();
            } else {
                // Stay in same task
                NavUtils.navigateUpTo(this, upIntent);
            }
        } else*/ finish();
    }

    protected abstract void handleToolbar();


    protected abstract int getLayoutResourceId();

    protected Activity getActivity() {
        return this;
    }

    protected void handleIntentExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            titleParameter = bundle.getString(ConstantsUtils.KEY_PAGE_TITLE_PARAMETER, "");
        }
    }

//    protected void showProgressDialog() {
//        //ViewUtils.enableLayoutChildes(((ViewGroup) findViewById(R.id.content_container)), false);
//        //progressDialog.show();
//
//        //findViewById(android.R.id.content).setVisibility(View.INVISIBLE);
//        rootView = (ViewGroup) findViewById(android.R.id.content);
//        progressView = getLayoutInflater().inflate(R.layout.progress, rootView, false);
//        ViewUtils.enableLayoutChildes(((ViewGroup) findViewById(android.R.id.content)), false);
//        rootView.addView(progressView, MATCH_PARENT, MATCH_PARENT);
//
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (rootView.findViewById(R.id.progress_view) != null)
//                    dismissProgress(false);
//            }
//        }, ConstantsUtils.CONNECT_TIME_OUT_MILLISECONDS);
//    }


    protected void dismissProgress(boolean successful) {
        rootView.removeView(progressView);
        if (successful) {
            ViewUtils.enableLayoutChildes(((ViewGroup) findViewById(android.R.id.content)), true);
        } else {
            ViewUtils.enableLayoutChildes(((ViewGroup) findViewById(R.id.toolbar)), true);
        }
    }

    public void onSuccessfulResponse() {
        dismissProgress(true);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
