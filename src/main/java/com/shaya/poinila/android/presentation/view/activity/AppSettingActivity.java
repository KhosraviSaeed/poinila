package com.shaya.poinila.android.presentation.view.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.fragments.AppSettingFragment;

/**
 * Created by iran on 2015-11-07.
 */
public class AppSettingActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup vp = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_fragment_host, null);
        Toolbar toolbar = (Toolbar) getLayoutInflater().inflate(R.layout.toolbar, vp, false);
        //toolbar = ButterKnife.findById(vp, R.actorID.toolbar);
        vp.addView(toolbar, 0);
        setContentView(vp);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        //forceRTLIfSupported();

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.content, new AppSettingFragment())
                .commit();

    }

    /*@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }*/
}
