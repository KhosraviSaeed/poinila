package com.shaya.poinila.android.presentation.view.activity;



import android.support.v4.app.Fragment;

import com.shaya.poinila.android.presentation.view.fragments.PostListFragment;
import com.shaya.poinila.android.util.ConstantsUtils;

public class ExploreActivity extends FragmentHostActivity{

    @Override
    protected Fragment getHostedFragment() {
        return PostListFragment.newInstance(mainEntityID, ConstantsUtils.REQUEST_EXPLORE);
    }
}
