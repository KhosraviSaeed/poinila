package com.shaya.poinila.android.presentation.view.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.activity.BaseActivity;

/**
 * Created by iran on 2015-08-09.
 */
public abstract class ToolbarActivity extends BaseActivity {

    protected Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View getActivityView() {
        ViewGroup vp = (ViewGroup) getLayoutInflater().inflate(getLayoutResourceId(), null);
        toolbar = (Toolbar) getLayoutInflater().inflate(R.layout.toolbar, vp, false);
        handleToolbar();
        vp.addView(toolbar, 0);
        return vp;
    }

    @Override
    protected void handleToolbar() {
        //Toolbar toolbar = ButterKnife.findById(this, R.actorID.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }
}
