package com.shaya.poinila.android.presentation.view.activity;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.presentation.view.dialog.DialogLauncher;
import com.shaya.poinila.android.presentation.view.fragments.BaseFragment;
import com.shaya.poinila.android.util.DeviceInfoUtils;
import com.shaya.poinila.android.util.PoinilaPreferences;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

import static com.shaya.poinila.android.util.ConstantsUtils.KEY_STARTED_FROM_SETTING;

public class HelpActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    @Bind(R.id.indicator)
    CircleIndicator mIndicator;
    @Bind(R.id.help_nextSlide)
    ImageButton mNextSlideBtn;
    HelpAdapter mPagerAdapter;
    private boolean startedFromSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initUI() {
        mPagerAdapter = new HelpAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mIndicator.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(this);
        PoinilaPreferences.setSeenHelp(false);
    }

    @Override
    protected void handleToolbar() {
    }

    @Override
    protected void handleIntentExtras() {
        startedFromSetting = getIntent().getBooleanExtra(KEY_STARTED_FROM_SETTING, false);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_help;
    }

    @OnClick(R.id.help_nextSlide)
    public void onNextSlide() {
        if (mViewPager.getCurrentItem() != mPagerAdapter.getCount() - 1) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
        } else if (startedFromSetting) {
            finish();
        } else {
            PoinilaPreferences.setSeenHelp(true);
            PageChanger.goToLoginActivity(getActivity());
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mNextSlideBtn.setImageResource(position == HelpAdapter.POS_ADD_CONTENT
                ? R.drawable.done_white_48dp : R.drawable.arrow_right_white_48dp);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private class HelpAdapter extends android.support.v4.app.FragmentPagerAdapter {
        public static final int ITEM_COUNT = 7;
        public static final int POS_INTRO = 0;
        public static final int POS_DASHBOARD = 1;
        public static final int POS_COLLECTIONS = 2;
        public static final int POS_SEARCH = 3;
        public static final int POS_NOTIFICATIONS = 4;
        public static final int POS_PROFILE = 5;
        public static final int POS_ADD_CONTENT = 6;

        public HelpAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return HelpFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return ITEM_COUNT;
        }
    }

    public static class HelpFragment extends BaseFragment {
        @Bind(R.id.root_view)
        View mRootView;
        @Bind(R.id.image_view)
        ImageView mImageView;
        @Bind(R.id.text_view)
        TextView mTextView;

        private static final String ARG_POSITION = "position";

        private int mPosition;
        private HashMap<Integer, State> mStatesMap;

        public static HelpFragment newInstance(int position) {
            HelpFragment fragment = new HelpFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_POSITION, position);
            fragment.setArguments(args);
            return fragment;
        }

        public HelpFragment() {

        }

        @Override
        public int getLayoutID() {
            return R.layout.fragment_help;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mPosition = getArguments().getInt(ARG_POSITION);
            }
            mStatesMap = initStateMap();
        }

        private HashMap<Integer, State> initStateMap() {
            HashMap<Integer, State> map = new HashMap<>();
            map.put(HelpAdapter.POS_INTRO, new State(R.color.help_intro_color,
                    R.drawable.splash_background, R.string.help_intro));
            map.put(HelpAdapter.POS_DASHBOARD, new State(R.color.help_dashboard_color,
                    R.drawable.help_dashboard, R.string.help_dashboard));
            map.put(HelpAdapter.POS_COLLECTIONS, new State(R.color.help_collections_color,
                    R.drawable.help_collections, R.string.help_collection));
            map.put(HelpAdapter.POS_SEARCH, new State(R.color.help_search_color,
                    R.drawable.help_search, R.string.help_search));
            map.put(HelpAdapter.POS_NOTIFICATIONS, new State(R.color.help_notifications_color,
                    R.drawable.help_notifications, R.string.help_notification));
            map.put(HelpAdapter.POS_PROFILE, new State(R.color.help_profile_color,
                    R.drawable.help_profile, R.string.help_profile));
            map.put(HelpAdapter.POS_ADD_CONTENT, new State(R.color.help_profile_color,
                    R.drawable.help_add_content, R.string.help_add_content));
            return map;
        }

        @Override
        protected void initUI() {
            mRootView.setBackgroundColor(ContextCompat.getColor(getActivity(), mStatesMap.get(mPosition).mColor));
            Picasso.with(getActivity()).load(mStatesMap.get(mPosition).mImage).into(mImageView);
            ViewUtils.setText(mTextView, getString(mStatesMap.get(mPosition).mText));
        }

        private static class State {
            public int mText;
            public int mColor;
            public int mImage;

            public State(@ColorRes int color, @DrawableRes int image, @StringRes int text) {
                this.mColor = color;
                this.mImage = image;
                this.mText = text;
            }
        }
    }
}
