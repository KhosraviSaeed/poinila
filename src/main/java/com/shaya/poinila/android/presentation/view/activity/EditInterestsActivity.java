package com.shaya.poinila.android.presentation.view.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.shaya.poinila.android.presentation.view.fragments.EditInterestsFragment;
public class EditInterestsActivity extends FragmentHostActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected android.support.v4.app.Fragment getHostedFragment() {
        return EditInterestsFragment.newInstance(mainEntityID);
    }

    @Override
    protected boolean withToolbar() {
        return true;
    }

    @Override
    protected void initUI() {

    }

}
