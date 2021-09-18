package com.shaya.poinila.android.presentation.view.fragments;

import android.os.Bundle;
import android.widget.TextView;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.util.ConstantsUtils;

import butterknife.Bind;
import butterknife.OnClick;
import manager.DataRepository;

/**
 * Created by iran on 2/28/2016.
 */
public class AnonymousInfoFragment extends BaseFragment {
    public static final int FOLLOWING_COLLECTIONS = 1;
    public static final int NOTIFICATIONS = 3;
    public static final int PROFILE = 4;

    @Bind(R.id.page_info_text)
    public TextView anonymousInfoText;

    public static AnonymousInfoFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ConstantsUtils.KEY_ANONYMOUS_PAGE, page);
        AnonymousInfoFragment fragment = new AnonymousInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_anonymous;
    }

    @Override
    protected void initUI() {
        int page = getArguments().getInt(ConstantsUtils.KEY_ANONYMOUS_PAGE);
        String info = null;
        if (page == FOLLOWING_COLLECTIONS)
            info = getString(R.string.anonymous_info_following_collections);
        else if (page == NOTIFICATIONS)
            info = getString(R.string.anonymous_info_notifications);
        else if (page == PROFILE)
            info = getString(R.string.anonymous_info_profile);
        ViewUtils.setText(anonymousInfoText, info);
    }

    @OnClick(R.id.back_to_login) public void onExitGuestToLogin(){
        DataRepository.logoutEvent();
    }

}
