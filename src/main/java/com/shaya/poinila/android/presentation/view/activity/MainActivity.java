package com.shaya.poinila.android.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.shaya.poinila.android.presentation.BuildConfig;
import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.HelpMyFollowedCollectionListFragment;
import com.shaya.poinila.android.presentation.uievent.HelpMyProfileFragment;
import com.shaya.poinila.android.presentation.uievent.ShowVerifySnackbarEvent;
import com.shaya.poinila.android.presentation.view.NotificationNumberListener;
import com.shaya.poinila.android.presentation.view.PageSelectedListener;
import com.shaya.poinila.android.presentation.view.dialog.DialogLauncher;
import com.shaya.poinila.android.presentation.view.fragments.AnonymousInfoFragment;
import com.shaya.poinila.android.presentation.view.fragments.BaseFragment;
import com.shaya.poinila.android.presentation.view.fragments.BusFragment;
import com.shaya.poinila.android.presentation.view.fragments.DashboardFragment;
import com.shaya.poinila.android.presentation.view.fragments.MyFollowedCollectionsFragment;
import com.shaya.poinila.android.presentation.view.fragments.MyProfileFragment;
import com.shaya.poinila.android.presentation.view.fragments.NotificationFragment;
import com.shaya.poinila.android.presentation.view.fragments.SearchFragment;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.ConnectionUitls;
import com.shaya.poinila.android.util.DeviceInfoUtils;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.util.PoinilaPreferences;
import com.shaya.poinila.android.utils.PonilaAccountManager;
import com.shaya.poinila.android.utils.PonilaSnackbarManager;
import com.squareup.otto.Subscribe;
import com.tapstream.sdk.Config;
import com.tapstream.sdk.Tapstream;

import java.util.Arrays;
import java.util.Calendar;

import butterknife.Bind;
import data.event.MyInfoReceivedEvent;
import data.event.SystemPreferencesReceivedEvent;
import data.model.Member;
import manager.DBFacade;
import manager.DataRepository;

import static com.shaya.poinila.android.util.StringUtils.isInteger;

// testing git
public class MainActivity extends BaseActivity implements
        NotificationNumberListener, ViewPager.OnPageChangeListener {

    private static final long DELAY_HIDE_KEYBOARD = 100;
    private static final String TAG_HOME = "home page";
    private long start;
    private TextView notifNumView;
    private RelativeLayout notifTabView;
    private SimpleFragmentPagerAdapter mAdapter;
    private boolean backPressed = false;

     

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (BuildConfig.DEBUG)
            Logger.toast("debug mode");//"***************<font color=\"#EE0000\"><big>DEBUG MODE!></big></font>*********");

        if (savedInstanceState == null)
            initRatingPrompt();

        handleDeepLink(getIntent());

        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        setupTab();

        if (!ConnectionUitls.isNetworkOnline())
            Logger.toast(R.string.warning_connect_to_network);


        Config config = new Config("ponila", "_aOzJdJ9SMGNYGo08LxytA");
        Tapstream.create(getApplication(), config);
    }

    /**
     * Fabric(Crashlytics): Log User Info
     */


    private void initRatingPrompt() {
        PoinilaPreferences.updateOpenApplicationCount();
        PoinilaPreferences.setFirstLoginDateTimeIfNotSet(Calendar.getInstance());
        DataRepository.calculateIsTimeToAskAboutRating();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleDeepLink(intent);
    }

    private void handleDeepLink(Intent intent) {
        if (intent == null || intent.getData() == null)
            return;

        Member cachedInfo = DBFacade.getCachedMyInfo();
        DataRepository.setUserAsAnonymous(cachedInfo == null || cachedInfo.isAnonymous);

        Uri shareUri = intent.getData();
        String mainEntityId, secondEntityId;
        if (shareUri != null
                && shareUri.getPathSegments() != null
                && !shareUri.getPathSegments().isEmpty()) {
            if(Arrays.asList("resetpassword", "register").contains(shareUri.getPathSegments().get(0))) { // ponila.com/register/{token}/
                PageChanger.goToLoginActivity(this, shareUri);
            }//urls in form of ponila.com/post/{post_id}
            else if (shareUri.getPathSegments().get(0).equals("post") &&
                    isInteger(mainEntityId = shareUri.getPathSegments().get(1))) {
                //actorID = shareUri.getPathSegments().get(postSegmentIndex);
                PageChanger.goToPost(this, mainEntityId);
            } //urls in form of ponila.com/{unique_name}/{collection_name}
            else if (shareUri.getPathSegments().size() == 2) {
                mainEntityId = shareUri.getPathSegments().get(1); // collection name
                secondEntityId = shareUri.getPathSegments().get(0); // user name
                PageChanger.goToCollection(this, null, mainEntityId, secondEntityId);
            } //urls in form of ponila.com/{unique_name}
            else if (shareUri.getPathSegments().size() == 1) {
                mainEntityId = shareUri.getPathSegments().get(0); // user name
                PageChanger.goToProfile(this, mainEntityId);
            } else {
                Logger.toast(R.string.error_invalid_url);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.this.finish();
                    }
                }, 1500);
            }
        }
    }

    @Override
    protected void initUI() {

        DataRepository.getInstance().getMyInfo(true,  MyInfoReceivedEvent.MY_INFO_TYPE.VERIFY);

    }

    @Subscribe
    public void onUserInfoReceived(MyInfoReceivedEvent event) {

        if (event.type != MyInfoReceivedEvent.MY_INFO_TYPE.VERIFY) return;

        if(!event.me.isEmailVerified && !event.me.isMobileVerified && !PoinilaPreferences.isUserAnonymous())
            PonilaSnackbarManager.getInstance().showVerifySnackbar(findViewById(R.id.main_content), this);
        else if(!event.me.isPassword)
            PonilaSnackbarManager.getInstance().showChangeUserPassSnackBar(findViewById(R.id.main_content), this);


        new AsyncTask<Object, Void, Void>() {

            @Override
            protected Void doInBackground(Object... params) {
                DataRepository.syncWithMyInfoResponse((MyInfoReceivedEvent) params[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void o) {

            }


        }.execute(event);

    }

    @Override
    public void onBackPressed() {
        if(!backPressed){
            backPressed = true;
            Logger.toast(R.string.exit_with_back_message);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressed = false;
                }
            }, 5000);
        }else {
            backPressed = false;
            System.exit(0);
//            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void handleToolbar() {

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }


    // ----------TODO: just for testing if problem with viewpager is by nested fragments

    private static final int TABS_SIZE = 5;
    @Bind(R.id.view_pager)
    public ViewPager viewPager;
    @Bind(R.id.tabs)
    public TabLayout tabLayout;

    private void setupTab() {
        int[] icons = new int[]{R.drawable.tab_dashboard_selector, R.drawable.tab_collection_selector,
                R.drawable.tab_search_selector, R.drawable.tab_notification_selector,
                R.drawable.tab_profile_selector};

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(),
                MainActivity.this);
        viewPager.setAdapter(mAdapter);

        viewPager.setOffscreenPageLimit(5);

        viewPager.addOnPageChangeListener(this);

        // Give the TabLayout the ViewPager
        tabLayout.setupWithViewPager(viewPager);


        setupNotificationTab();

        for (int i = 0; i < TABS_SIZE; i++){
            if( i == 3){
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                tab.setCustomView(notifTabView);
            }else {
                tabLayout.getTabAt(i).setIcon(icons[i]);

            }
        }
    }

    private void setupNotificationTab(){
        notifTabView = (RelativeLayout)LayoutInflater.from(MainActivity.this).inflate(R.layout.tab_notification_layout, null);
        notifNumView = (TextView) notifTabView.findViewById(R.id.notification_number);
    }

    @Override
    public void onNotificationNumber(int number) {
        if(number > -10){
            notifNumView.setVisibility(View.VISIBLE);
            notifNumView.setText(String.valueOf(number));
            return;
        }
        notifNumView.setVisibility(View.GONE);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(mAdapter.getItem(position) instanceof MyFollowedCollectionsFragment){
            BusProvider.getBus().post( new HelpMyFollowedCollectionListFragment());
        }else if(mAdapter.getItem(position) instanceof MyProfileFragment){
            BusProvider.getBus().post( new HelpMyProfileFragment());
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class SimpleFragmentPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        private String[] tabTitles;
        private Context context;

        public SimpleFragmentPagerAdapter(android.support.v4.app.FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
            tabTitles = context.getResources().getStringArray(R.array.tab_titles);
        }


        @Override
        public int getCount() {
            return TABS_SIZE;
        }

        // in case of entering as an anonymous user, return static pages for collections, notifications, profile tabs.
        @Override
        public BaseFragment getItem(int position) {
            switch (position) {
                case 0: return DashboardFragment.newInstance();
                case 1: return DataRepository.isUserAnonymous() ?
                        AnonymousInfoFragment.newInstance(AnonymousInfoFragment.FOLLOWING_COLLECTIONS) :
                        MyFollowedCollectionsFragment.newInstance();
                //pages.add(FragmentPagerItem.of("", HomeFragment.TestFragment.class));
                case 2: return SearchFragment.newInstance();
                case 3: return DataRepository.isUserAnonymous() ?
                        AnonymousInfoFragment.newInstance(AnonymousInfoFragment.NOTIFICATIONS) :
                        NotificationFragment.newInstance();
                case 4: return DataRepository.isUserAnonymous() ?
                        AnonymousInfoFragment.newInstance(AnonymousInfoFragment.PROFILE) :
                        MyProfileFragment.newInstance();
                default:
                    return null;
            }

        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return "";//tabTitles[position];
        }
    }

    @Subscribe
    public void keepSystemPreferences(SystemPreferencesReceivedEvent event){
        DataRepository.setSystemPreferences(event.systemPreferences);
    }
}
