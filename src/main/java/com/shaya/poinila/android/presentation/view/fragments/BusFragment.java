package com.shaya.poinila.android.presentation.view.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.PoinilaPreferences;

import butterknife.ButterKnife;
import data.RequestTracker;
import data.event.BaseEvent;

/**
 * Created by iran on 2015-06-30.
 * General Behavior a fragment must do if it subscribes on the bus
 */
public abstract class BusFragment extends BaseFragment {
    protected boolean initDataResponseReceived = false;
    protected ViewGroup progressView;
    private View loadableView;
    private ViewGroup loadableViewParent;
    private ViewGroup.LayoutParams loadableViewLayoutParams;
    protected RequestTracker requestTracker;
    private int loadableViewIndex;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestTracker = new RequestTracker();

        mProgressDialog = new ProgressDialog(getActivity(), 0);
        mProgressDialog.setMessage(getString(R.string.progress_dialog_message));
    }

    @Override
    public void onStart() {
        super.onStart();
        BusProvider.getBus().register(this);
        if (sendsRequestAutomatically() && !initDataResponseReceived)
            initData();
    }

    protected abstract boolean sendsRequestAutomatically();

    @Override
    public void onStop() {
        super.onStop();
        BusProvider.getBus().unregister(this);
    }

    protected void onGettingInitDataResponse(BaseEvent event) {
        if (isInitDataResponseValid(event))
            onSuccessfulInitData(event);
    }

    /**
     * called only on creating view. (with regard to {@link #initDataResponseReceived}
     * if somethings need to be updated, update with events and bus.
     */
    public void initData() {
        initDataResponseReceived = false;
        if (mustShowProgressView())
            showProgress();
        //requestTracker.addInitRequestID(RandomUtils.getRandomInt());
        requestInitialData();
    }

    protected abstract void requestInitialData();

    public abstract ViewGroup getLoadableView();

    protected void showProgress() {
        progressView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(getProgressViewLayoutID(), rootView, false);
        initProgressBar(getProgressBar());
        /*int progressBarSize = getResources().getDimensionPixelSize(R.dimen.icon_big);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(progressBarSize, progressBarSize);
        lp.gravity = Gravity.CENTER;
        progressView.addView(getProgressBar(), lp);*/

        loadableView = getLoadableView();
//        if(loadableView != null){
//            loadableViewLayoutParams = loadableView.getLayoutParams();
//            loadableViewParent = (ViewGroup) loadableView.getParent();
//            //TODO: null pointer sometimes. but why?
//            if (loadableViewParent != null) {
//                loadableViewIndex = loadableViewParent.indexOfChild(loadableView);
//                loadableViewParent.removeView(loadableView);
//                loadableViewParent.addView(progressView, loadableViewIndex, loadableViewLayoutParams);
//            }
//
//            if (getProgressBar().isIndeterminate()) {
//                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (rootView.findViewById(R.id.progress_view) != null)
//                            dismissProgress(false);
//                    }
//                }, ConstantsUtils.CONNECT_TIME_OUT_MILLISECONDS);
//            }
//        }

    }

    @LayoutRes
    protected int getProgressViewLayoutID() {
        return R.layout.progress;
    }

    protected void initProgressBar(ProgressBar progressBar) {

    }


    public void showProgressDialog(){
        if(mProgressDialog != null){
            mProgressDialog.show();
        }

    }

    public void dismissProgressDialog(){
        mProgressDialog.dismiss();
    }

    public ProgressBar getProgressBar() {
        /*ProgressBar progressBar = ButterKnife.findById(progressView, R.id.progress_bar);
        if (progressBar == null){
            progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
        }
        return progressBar;*/
        return ButterKnife.findById(progressView, R.id.progress_bar);
    }

    // TODO: sometimes getting null loadableViewParent. But what times exactly?
    protected void dismissProgress(boolean dataLoadSuccessful) {
        if (loadableViewParent == null) return;
        loadableViewParent.removeView(progressView);
        // -- kasif!
        if (loadableView.getParent() != null)
            ((ViewGroup) loadableView.getParent()).removeView(loadableView);
        //
        if (dataLoadSuccessful) {
            //ViewUtils.enableLayoutChildes(getLoadableView(), true);
            loadableViewParent.addView(loadableView, loadableViewIndex, loadableViewLayoutParams);
        }
    }

    public void onSuccessfulInitData(BaseEvent baseEvent) {
        if (mustShowProgressView() && !initDataResponseReceived)
            dismissProgress(true);
        initDataResponseReceived = true;
    }

    public abstract boolean mustShowProgressView();

    protected boolean isInitDataResponseValid(BaseEvent baseEvent) {
        // in many cases of list pages, requestForInitData just calls the requestForLoadMore so we call this
        // function on getting response data of list. In order to avoid view hierarchy traversing on every
        // list data response, we check the requestOnFirstTime flag to be true.
        return true;
    }
    /*---temporary validity check functions----*/
    //TODO: later, these methods must
}
