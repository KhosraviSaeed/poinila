package com.shaya.poinila.android.presentation.view.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.shaya.poinila.android.presentation.PoinilaApplication;
import com.shaya.poinila.android.presentation.view.OnHelpShowListener;
import com.shaya.poinila.android.utils.uisynchronize.UISynchronizeBus;
import com.shaya.poinila.android.utils.uisynchronize.UISynchronizeReceiver;

import java.io.Serializable;

import butterknife.ButterKnife;

/**
 * Created by iran on 2015-06-21.
 * @author Alireza Farahani
 *
 * Abstract class, father of all fragments for poinila app
 *
 */
public abstract class BaseFragment extends Fragment implements UISynchronizeReceiver.OnLoadDataSynchronizeListener{

    protected ViewGroup rootView;
    private String defaultTag = getClass().getName();

    boolean viewedHelp = false;
    protected boolean selected = false;
    protected boolean ready = false;

    private Tracker mTracker;

    /**
     * Avoiding memory leak by not returning a activity.
     * We need getting context from activity in many cases.
     * @return Application context
     */
    public Context getContext(){
        return getActivity();
    }

    public abstract @LayoutRes int getLayoutID();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PoinilaApplication application = (PoinilaApplication)getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        mTracker.setScreenName(getDefaultTag());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        UISynchronizeBus.getInstance().getReceiver().setOnLoadDataSynchronizeListener(this);


        /**
         * Preventing loss of data on configuration change(like rotation).
         * May be saving data on a fragment object and retrieving by fragmentManager is a
         * better idea.
         *
         FragmentManager fm = getFragmentManager();
         dataFragment = (DataFragment) fm.findFragmentByTag(“data”);

         // create the fragment and data the first time
         if (dataFragment == null) {
            // add the fragment
            dataFragment = new DataFragment();
            fm.beginTransaction().add(dataFragment, “data”).commit();
            // load the data from the web
            dataFragment.setData(loadMyData());
         }

         */
        //setRetainInstance(true);
    }

    /*private BroadcastReceiver mRequestFailedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            requestingIsLocked = false;
        }
    };*/

    @Override
    public void onStart() {
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        super.onStart();
        /*LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRequestFailedReceiver,
                new IntentFilter(ConstantsUtils.INTENT_FILTER_REQUEST_FAILED));*/
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = ((ViewGroup) inflater.inflate(getLayoutID(), container, false));
        ButterKnife.bind(this, rootView);
        initUI();
        return rootView;
    }

    protected abstract void initUI();

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public String getDefaultTag() {
        return defaultTag;
    }

    public boolean isReady() {
        return ready;
    }

    @Override
    public UISynchronizeBus.UI_SYNCHRONIZE_ACTION getSynchronizeAction() {
        return UISynchronizeBus.UI_SYNCHRONIZE_ACTION.OFF;
    }

    @Override
    public void loadDataForSynchronize(Serializable data, UISynchronizeBus.UI_SYNCHRONIZE_ACTION action) {
        Log.i(getClass().getName(), "loadDataForSynchronize");
    }
}
