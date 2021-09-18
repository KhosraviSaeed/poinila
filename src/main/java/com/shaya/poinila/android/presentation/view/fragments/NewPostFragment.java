package com.shaya.poinila.android.presentation.view.fragments;

import android.view.ViewGroup;

/**
 * Created by hossein on 8/20/16.
 */
public class NewPostFragment extends BusFragment {
    @Override
    protected boolean sendsRequestAutomatically() {
        return false;
    }

    @Override
    protected void requestInitialData() {

    }

    @Override
    public ViewGroup getLoadableView() {
        return null;
    }

    @Override
    public boolean mustShowProgressView() {
        return false;
    }

    @Override
    public int getLayoutID() {
        return 0;
    }

    @Override
    protected void initUI() {

    }
}
