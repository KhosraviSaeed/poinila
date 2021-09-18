package com.shaya.poinila.android.presentation.view.activity;

import android.app.Fragment;
import android.view.MenuItem;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.fragments.NotificationSwitchFragment;


public class NotificationSwitchActivity extends FragmentHostActivity {


    @Override
    protected android.support.v4.app.Fragment getHostedFragment() {
        return NotificationSwitchFragment.newInstance(mainEntityID, requestID);
    }

    @Override
    protected boolean withToolbar() {
        return true;
    }


    @Override
    protected void initUI() {
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
