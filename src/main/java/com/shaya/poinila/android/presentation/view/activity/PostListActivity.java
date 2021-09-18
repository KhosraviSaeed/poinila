package com.shaya.poinila.android.presentation.view.activity;

import android.app.Fragment;
import android.content.pm.ActivityInfo;

import com.shaya.poinila.android.presentation.view.fragments.CollectionPageFragment;
import com.shaya.poinila.android.presentation.view.fragments.PostAndRelatedPostFragment;
import com.shaya.poinila.android.presentation.view.fragments.PostListFragment;

import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_COLLECTION_POSTS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_EXPLORE;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_MEMBER_POSTS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_POST_RELATED_POSTS;

public class PostListActivity extends FragmentHostActivity {

    @Override
    public void handleIntentExtras() {
        super.handleIntentExtras();
        // handling shareable urls
    }

    @Override
    protected void initUI() {

    }

    @Override
    protected android.support.v4.app.Fragment getHostedFragment() {
        switch (requestID) {
            case REQUEST_COLLECTION_POSTS:
                return CollectionPageFragment.newInstance(mainEntityID, secondEntityID, requestID);
            case REQUEST_EXPLORE:
                return PostListFragment.newInstance(mainEntityID, secondEntityID, requestID);
            case REQUEST_MEMBER_POSTS:
                return PostListFragment.newInstance(mainEntityID, secondEntityID, requestID);
            case REQUEST_POST_RELATED_POSTS:
                return PostAndRelatedPostFragment.newInstance(mainEntityID, secondEntityID, requestID);
            default: // never call default mode
                return PostListFragment.newInstance(mainEntityID, secondEntityID, requestID);
        }
    }

    @Override
    protected boolean withToolbar() {
        return true;
    }
}
