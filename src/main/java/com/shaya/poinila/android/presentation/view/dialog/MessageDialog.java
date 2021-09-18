package com.shaya.poinila.android.presentation.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.util.ConstantsUtils;

/**
 * Created by iran on 8/3/2016.
 */
public class MessageDialog extends BusDialogFragment {



    public int titleRes;
    public int messageRes;


    public static MessageDialog newInstance(int titleRes, int messageRes){
        MessageDialog fragment = new MessageDialog();

        Bundle data = new Bundle();

        data.putInt("titleRes", titleRes);
        data.putInt("messageRes", messageRes);

        fragment.setArguments(data);
        fragment.saveStateToBundle(data);
        return fragment;
    }


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
    public int getLayoutResId() {
        return LAYOUT_NONE;
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {
        titleRes = getArguments().getInt("titleRes");
        messageRes = getArguments().getInt("messageRes");
    }

    @Override
    protected void saveStateToBundle(Bundle outState) {

    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(titleRes, messageRes, R.string.ok, ConstantsUtils.NO_RESOURCE, ConstantsUtils.NO_RESOURCE);
    }

    @Override
    protected void initUI(Context context) {

    }
}
