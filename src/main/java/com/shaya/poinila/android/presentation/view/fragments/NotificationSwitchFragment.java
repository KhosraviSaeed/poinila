package com.shaya.poinila.android.presentation.view.fragments;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.CompoundButton;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.uievent.OnOffSettingToggledUIEvent;
import com.shaya.poinila.android.presentation.view.activity.FragmentHostActivity;
import com.shaya.poinila.android.presentation.viewholder.SwitchTextViewHolder;
import com.shaya.poinila.android.util.ResourceUtils;
import com.squareup.otto.Subscribe;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import data.PoinilaNetService;
import data.event.BaseEvent;
import data.event.NotificationSettingsReceivedEvent;
import data.model.OnOffSetting;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_ENTITY;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_REQUEST_ID;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_APPLICATION_NOTIFICATION;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_EMAIL_NOTIFICATION;

/**
 * Created by iran on 2015-09-07.
 */
public class NotificationSwitchFragment extends ListBusFragment<OnOffSetting> {

    private int requestID;
    private OnOffSetting emailOnOffSetting;
    private SwitchCompat emailOnOffSwitch;

    public static NotificationSwitchFragment newInstance(String actorID, int requestID){
        NotificationSwitchFragment f = new NotificationSwitchFragment();
        f.requestID = requestID;
        Bundle b = new Bundle();
        b.putInt(KEY_REQUEST_ID, requestID);
        b.putString(KEY_ENTITY, actorID);
        f.setArguments(b);
        return f;
    }

    @Override
    protected RecyclerView.OnScrollListener getRecyclerViewListener() {
        return new RecyclerView.OnScrollListener() {
        };
    }

    @Override
    public int getLayoutID() {
        return R.layout.recycler_view_full;
    }

    @Override
    protected void initUI() {
        Bundle b = getArguments();
        requestID = b.getInt(KEY_REQUEST_ID);

        int padding = (int) ResourceUtils.getDimen(R.dimen.padding_lvl1);
        mRecyclerView.setPadding(padding, padding, padding, padding);
        mRecyclerView = new RecyclerViewProvider(mRecyclerView).
                setLinearLayoutManager(VERTICAL).
                setAdapter(getRecyclerViewAdapter()).
                bindViewToAdapter();
        mRecyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(getContext())
                        .marginResId(R.dimen.margin_lvl1)
                        .build());
        switch (requestID){
            case REQUEST_APPLICATION_NOTIFICATION:
                getActivity().setTitle(R.string.title_activity_application_notification);
                setHasOptionsMenu(false);
                break;
            case REQUEST_EMAIL_NOTIFICATION:
                emailOnOffSetting = new OnOffSetting();
                getActivity().setTitle(R.string.title_activity_email_notification);
                setHasOptionsMenu(true);
                emailOnOffSwitch = (SwitchCompat) getActivity().getLayoutInflater().inflate(R.layout.poinila_switch, null);

                emailOnOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) emailOnOffSetting.on();
                        else emailOnOffSetting.off();
                        activeAllChilds(getRecyclerViewAdapter(), isChecked);
                        PoinilaNetService.setEmailNotificationSetting(emailOnOffSetting);
                        //enableLayoutChildes(mRecyclerView, isChecked);
                    }
                });

                Toolbar.LayoutParams lp = new Toolbar.LayoutParams(Gravity.RIGHT);
                lp.setMargins(0,0, ((int) ResourceUtils.getDimen(R.dimen.margin_lvl2)), 0);
                ((FragmentHostActivity) getActivity()).getToolbar().addView(emailOnOffSwitch, lp);
                break;
        }
    }

    private void activeAllChilds(RecyclerViewAdapter<OnOffSetting, ?> recyclerViewAdapter, boolean isChecked) {
        for (Object setting : recyclerViewAdapter.getItems()) {
            ((OnOffSetting)setting).enabled = isChecked;
        }
        recyclerViewAdapter.notifyItemRangeChanged(0, recyclerViewAdapter.getItemCount());
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Subscribe public void onNotificationSettingsReceived(NotificationSettingsReceivedEvent event){
        onGettingInitDataResponse(event);
    }

    @Subscribe public void onSettingChanged(OnOffSettingToggledUIEvent event){
        OnOffSetting setting = getRecyclerViewAdapter().getItem(event.adapterPosition);

        if (event.settingOn) setting.on();
        else setting.off();

        switch (requestID){
            case REQUEST_EMAIL_NOTIFICATION:
                PoinilaNetService.setEmailNotificationSetting(setting);
                break;
            case REQUEST_APPLICATION_NOTIFICATION:
                PoinilaNetService.setApplicationNotificationSetting(setting);
                break;
        }
    }

/*------------------*/
    @Override
    protected boolean sendsRequestAutomatically() {
        return true;
    }

    @Override
    protected void requestInitialData() {
        switch (requestID){
            case REQUEST_APPLICATION_NOTIFICATION:
                PoinilaNetService.getApplicationNotification();
                break;
            case REQUEST_EMAIL_NOTIFICATION:
                PoinilaNetService.getEmailNotification();
                break;
        }
    }

    @Override
    public boolean mustShowProgressView() {
        return true;
    }

    @Override
    public void requestForMoreData() {

    }

    @Override
    public RecyclerViewAdapter<OnOffSetting, ? extends SwitchTextViewHolder> createAndReturnRVAdapter() {
        return new RecyclerViewAdapter<OnOffSetting, SwitchTextViewHolder>(getActivity(), R.layout.switch_text_setting) {
            @Override
            protected SwitchTextViewHolder getProperViewHolder(View v, int viewType) {
                return new SwitchTextViewHolder(v);
            }
        };
    }

    @Override
    public void onSuccessfulInitData(BaseEvent baseEvent) {
        super.onSuccessfulInitData(baseEvent);
        NotificationSettingsReceivedEvent event = ((NotificationSettingsReceivedEvent) baseEvent);
        if (requestID == REQUEST_EMAIL_NOTIFICATION) {
            // reverse loop preventing concurrent modification exception
            for (int i = event.notificationSettings.size() - 1; i >= 0 ; i--) {
                if (event.notificationSettings.get(i).code.equals("email_notification")){
                    emailOnOffSetting = event.notificationSettings.get(i);
                    event.notificationSettings.remove(i);
                }
            }

            for (OnOffSetting setting : event.notificationSettings){
                setting.enabled = emailOnOffSetting.value == OnOffSetting.ON;
            }

            emailOnOffSwitch.setChecked(emailOnOffSetting.value == OnOffSetting.ON);
        }
        getRecyclerViewAdapter().resetData(event.notificationSettings);
    }
}
