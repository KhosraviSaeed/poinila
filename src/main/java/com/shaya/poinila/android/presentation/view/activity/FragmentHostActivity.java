package com.shaya.poinila.android.presentation.view.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.util.ConstantsUtils;

/**
 * Created by iran on 2015-08-09.
 */
public abstract class FragmentHostActivity extends BaseActivity {
    protected int requestID;
    protected String mainEntityID;
    protected String secondEntityID;

    public Toolbar getToolbar() {
        return toolbar;
    }

    protected Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handleIntentExtras();

        /*if (getFragmentManager().findFragmentById(R.id.content) == null){
            addFragment(DataRepository.getInstance().getTempModel(BaseFragment.class), false);
        }*/

        getSupportFragmentManager().beginTransaction().add(R.id.content,
                getHostedFragment(), getHostedFragmentTag()).commit();

    }

    protected abstract android.support.v4.app.Fragment getHostedFragment();

    private String getHostedFragmentTag() {
        return null;
    }

    /**
     * Called in on create hook method. So beware of some conditions like constructing fragments
     * after super.onCreate. in those conditions fragment doesn't received valid parameters.
     * @return
     */

    protected boolean withToolbar(){
        return true;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_fragment_host;
    }

    @Override
    protected View getActivityView() {
        ViewGroup vp = (ViewGroup) getLayoutInflater().inflate(getLayoutResourceId(), null);
        if (withToolbar()){
            toolbar = (Toolbar) getLayoutInflater().inflate(R.layout.toolbar, vp, false);
            //toolbar = ButterKnife.findById(vp, R.actorID.toolbar);
            vp.addView(toolbar, 0);
            handleToolbar();
        }
        return vp;
    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void handleToolbar() {
        //Toolbar toolbar = ButterKnife.findById(this, R.actorID.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

            // TODO: corresponding fragments should handle this. They must generate activity title.
            switch (requestID){
                /*----collections-----*/
                case ConstantsUtils.REQUEST_POST_REPOSTING_COLLECTIONS:
                    setTitle(R.string.title_activity_reposts_list);
                    break;
                case ConstantsUtils.REQUEST_MEMBER_COLLECTIONS:
                    setTitle(R.string.title_activity_member_collections);
                    break;
                case ConstantsUtils.REQUEST_MEMBER_FOLLOWED_COLLECTIONS:
                    setTitle(R.string.title_activity_member_followed_collections);
                    break;

                /*----members-----*/
                case ConstantsUtils.REQUEST_MEMBER_FRIENDS:
                    setTitle(R.string.title_activity_member_friends);
                    break;
                case ConstantsUtils.REQUEST_POST_LIKERS:
                    setTitle(R.string.title_activity_post_likers);
                    break;
                case ConstantsUtils.REQUEST_MEMBER_FOLLOWERS:
                    setTitle(R.string.title_activity_member_followers);
                    break;

                /*-----Posts----*/
                case ConstantsUtils.REQUEST_MEMBER_FAVED_POSTS:
                    setTitle(R.string.title_activity_member_faved_posts);
                    break;
                case ConstantsUtils.REQUEST_MEMBER_POSTS:
                    setTitle(R.string.title_activity_member_posts);
                    break;
            }
        }
    }

    public void handleIntentExtras(){
        Bundle b = getIntent().getExtras();
        if (b != null) {
            requestID = b.getInt(ConstantsUtils.KEY_REQUEST_ID, -1);
            mainEntityID = b.getString(ConstantsUtils.KEY_ENTITY);
            secondEntityID = b.getString(ConstantsUtils.KEY_SECOND_ENTITY_ID);
            titleParameter = b.getString(ConstantsUtils.KEY_PAGE_TITLE_PARAMETER, "");
        }
    }

/*    public void addFragment(BaseFragment fragment, boolean addToBackStack){
        @SuppressLint("CommitTransaction") // committed few lines below
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.content,
                fragment, fragment.getDefaultTag());
        if (addToBackStack) ft.addToBackStack(fragment.getDefaultTag());
        ft.commit();
    }*/
}
