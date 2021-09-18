package com.shaya.poinila.android.presentation.view;

import android.view.View;

/**
 * Created by iran on 5/28/2016.
 */
public interface OnHelpShowListener {

    public void onHelpShow();

    public void setSelected(boolean status);

    public boolean isSelected();

    public void setReady(boolean status);

    public boolean isReady();

    public void setViewHelpStatus(boolean status);

    public boolean isViewedHelp();


}
