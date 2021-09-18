package com.shaya.poinila.android.presentation.view.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.fragments.NewWebSitePostInputURLFragment;
import com.shaya.poinila.android.presentation.view.fragments.NewWebSitePostSelectMediaFragment;

import data.model.PostType;
import data.model.SuggestedWebPagePost;

public class NewPostActivity extends BaseActivity {


    private static final String TAG_INPUT_URL_FRAGMENT = NewWebSitePostInputURLFragment.class.getName();
    private static final String TAG_SELECT_IMAGE_FRAGMENT = NewWebSitePostSelectMediaFragment.class.getName();


    Toolbar toolbar;

    @Override
    protected void initUI() {

        goToInputURLFragment();

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        handleToolbar();

    }

    @Override
    protected void handleToolbar() {
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_new_web_site_post;
    }

    public void goToInputURLFragment() {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.container, new NewWebSitePostInputURLFragment(), TAG_INPUT_URL_FRAGMENT).
                //addToBackStack(TAG_LOGIN_FRAGMENT).
                        commit();
    }

    public void goToSelectMediaFragment(PostType postType, String siteAddress) {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.container, NewWebSitePostSelectMediaFragment.newInstance(postType, siteAddress), TAG_SELECT_IMAGE_FRAGMENT).
                //addToBackStack(TAG_LOGIN_FRAGMENT).
                        commit();
    }

    public void goToNewPostFragment(SuggestedWebPagePost suggestedPost){

    }

    @Override
    protected View getActivityView() {
        ViewGroup vp = (ViewGroup) getLayoutInflater().inflate(getLayoutResourceId(), null);
        toolbar = (Toolbar) getLayoutInflater().inflate(R.layout.toolbar, vp, false);
        handleToolbar();
        vp.addView(toolbar, 0);
        return vp;
    }




}
