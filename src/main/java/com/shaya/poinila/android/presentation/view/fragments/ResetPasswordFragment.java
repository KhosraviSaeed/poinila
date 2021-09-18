package com.shaya.poinila.android.presentation.view.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.SmsReceiver;
import com.shaya.poinila.android.presentation.uievent.SmsReceivedEvent;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.presentation.view.activity.BaseActivity;
import com.shaya.poinila.android.presentation.view.costom_view.ActivityResultPermissionDelegate.SimpleActivityResultPermissionDelegate;
import com.shaya.poinila.android.util.ConnectionUitls;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.utils.PonilaAccountManager;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import data.PoinilaNetService;
import data.event.LoginFailedEvent;
import data.event.LoginSucceedEvent;
import data.event.MyInfoReceivedEvent;
import manager.DataRepository;

/**
 * Created by iran on 12/8/2015.
 */
public class ResetPasswordFragment extends BusFragment implements FragmentCompat.OnRequestPermissionsResultCallback {

    private static final String KEY_IDENTIFICATION_CODE = "identification code";
    @Bind(R.id.submit)
    Button SubmitButton;
    @Bind(R.id.email_or_phone_number)
    EditText verificationCodeInput;
    @Bind(R.id.password_input)
    EditText passwordInput;
    @Bind(R.id.card_title)
    TextView title;
    private String identificationCodeString;
    private SimpleActivityResultPermissionDelegate permissionHandlerDelegate;
    private boolean smsPermissionDeclined;

    @OnClick(R.id.submit)
    public void onResetPass() {

        if (!TextUtils.isEmpty(passwordInput.getText().toString())) {
            showProgressDialog();
            PoinilaNetService.resetPassword(passwordInput.getText().toString().trim(), verificationCodeInput.getText().toString().trim());

        } else
            Logger.toastError(R.string.error_password);
    }

    @Subscribe
    public void onSuccessfulResetPassword(LoginSucceedEvent event) {
        DataRepository.getInstance().getMyInfo(ConnectionUitls.isNetworkOnline(), MyInfoReceivedEvent.MY_INFO_TYPE.LOAD);
    }

    @Subscribe
    public void onFailedResetPassword(LoginFailedEvent event) {
        dismissProgressDialog();
        Logger.toast(getString(R.string.error_change_password_code));
    }

    @Subscribe
    public void onUserInfoReceived(MyInfoReceivedEvent event) {

        if (event.type != MyInfoReceivedEvent.MY_INFO_TYPE.LOAD)
            return;

        onGettingInitDataResponse(event);


        new AsyncTask<Object, Void, Void>() {

            @Override
            protected Void doInBackground(Object... params) {
                DataRepository.syncWithMyInfoResponse((MyInfoReceivedEvent) params[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void o) {
                PonilaAccountManager.getInstance().initUserTag();
                PageChanger.goToDashboard(getActivity());
                dismissProgressDialog();
            }


        }.execute(event);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            identificationCodeString = getArguments().getString(KEY_IDENTIFICATION_CODE, null);
        else if (savedInstanceState != null)
            identificationCodeString = savedInstanceState.getString(KEY_IDENTIFICATION_CODE, null);
        else
            identificationCodeString = "";

        permissionHandlerDelegate = new SimpleActivityResultPermissionDelegate() {
            @Override
            public void handlePermissionDenied() {
                Logger.longToast(getString(R.string.permission_reason_sms));
                smsPermissionDeclined = true;
            }

            @Override
            public void handlePermissionGranted() {
                getActivity().registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
            }

            @Override
            public void handleValidResults(int requestCode, Intent data) {
            }// we don't start activity for result, a better design perhaps...
        };
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_IDENTIFICATION_CODE, identificationCodeString);
    }

    public void fillIdentificationCodeInput(String code) {
        verificationCodeInput.setText(code);
    }

    SmsReceiver smsReceiver = new SmsReceiver();

    @Override
    public void onStart() {
        super.onStart();
        permissionHandlerDelegate.askForPermission(this,
                Manifest.permission.RECEIVE_SMS,
                BaseActivity.MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionHandlerDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onStop() {
        super.onStop();
        // TODO: unregister receiver. but I don't know if it's correct. may be we should listen
        // in all application life cycle
        try {
            if (!smsPermissionDeclined)
                getActivity().unregisterReceiver(smsReceiver);
        } catch (IllegalArgumentException exception) {
            // just ignore it since we don't know user granted permission or not
        }
    }

    @Subscribe
    public void onSmsReceived(SmsReceivedEvent event) {
        verificationCodeInput.setText(event.str);
    }

    @Override
    protected boolean sendsRequestAutomatically() {
        return false;
    }

    @Override
    protected void requestInitialData() {

    }

    @Override
    public ViewGroup getLoadableView() {
        return null;
    }

    @Override
    public boolean mustShowProgressView() {
        return false;
    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_reset_password;
    }

    @Override
    protected void initUI() {
        ViewUtils.setText(title, getString(R.string.reset_password));
        ButterKnife.findById(rootView, R.id.left_arrow).setVisibility(View.GONE);
        if (!TextUtils.isEmpty(identificationCodeString))
            fillIdentificationCodeInput(identificationCodeString);
    }

    public static ResetPasswordFragment newInstance(String code) {

        Bundle args = new Bundle();
        args.putString(KEY_IDENTIFICATION_CODE, code);
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
