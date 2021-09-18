package com.shaya.poinila.android.presentation.view.activity;

import android.app.Fragment;
import android.os.Bundle;

import com.shaya.poinila.android.presentation.view.fragments.InvitationNotifListFragment;

public class InvitationNotifListActivity extends FragmentHostActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initUI() {

    }

    @Override
    protected android.support.v4.app.Fragment getHostedFragment() {
        return InvitationNotifListFragment.newInstance();
    }

    @Override
    protected boolean withToolbar() {
        return true;
    }

    //TODO accept all option!
}
