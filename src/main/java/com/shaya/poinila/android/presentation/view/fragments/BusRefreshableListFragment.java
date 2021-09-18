package com.shaya.poinila.android.presentation.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.util.DeviceInfoUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by iran on 2015-08-02.
 */
public abstract class BusRefreshableListFragment<T> extends ListBusFragment<T> implements SwipeRefreshLayout.OnRefreshListener{

    //@Bind(R.actorID.refresh_layout)
    protected SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Child view root layout got from {#getLayoutID} <b>must be</b> a scrollable view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        swipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.swipe_refresh, container, false);

        //View content = inflater.inflate(getLayoutID(), swipeRefreshLayout, false);
        swipeRefreshLayout.addView(super.onCreateView(inflater, container, savedInstanceState), MATCH_PARENT, MATCH_PARENT);
        initSwipeLayout();
        return swipeRefreshLayout;
    }

    private void initSwipeLayout() {
        swipeRefreshLayout.setOnRefreshListener(this);
        if (DeviceInfoUtils.isTablet()) {
            swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        }
        setAppearance();
    }

    private void setAppearance() {
        swipeRefreshLayout.setColorSchemeResources(R.color.flamingo,
                R.color.west_side,
                R.color.holo_blue_bright,
                R.color.holo_green_light);
    }

    public abstract void refresh();


    public final void onRefreshFinished(){
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        /*new Handler().postDelayed(new Runnable() {
            @Override public void run() {

                swipeRefreshLayout.setRefreshing(false);
            }
        }, 5000);*/
        refresh();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (swipeRefreshLayout!=null) {
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.destroyDrawingCache();
            swipeRefreshLayout.clearAnimation();
        }
    }
}
