package com.shaya.poinila.android.presentation.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.AfterVerifyResponse;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.Logger;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import data.PoinilaNetService;
import data.event.VerificationRequestResponse;
import manager.DBFacade;

/**
 * Created by iran on 7/17/2016.
 */
public class InputVerificationCodeDialog extends BusDialogFragment {


    @Bind(R.id.input_field)
    EditText verificationCode;

    private String mobileOrEmail;
    private boolean byEmail;
    private boolean disableResend;

    public static InputVerificationCodeDialog newInstance(String mobileOrEmail, boolean byEmail){
        InputVerificationCodeDialog fragment = new InputVerificationCodeDialog();

        fragment.mobileOrEmail = mobileOrEmail;
        fragment.byEmail = byEmail;


        return fragment;
    }

    public static InputVerificationCodeDialog newInstance(String mobileOrEmail, boolean byEmail, boolean disableResend){
        InputVerificationCodeDialog fragment = new InputVerificationCodeDialog();

        fragment.mobileOrEmail = mobileOrEmail;
        fragment.byEmail = byEmail;
        fragment.disableResend = disableResend;

        return fragment;
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
    public int getLayoutResId() {
        return R.layout.input_verification_code;
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {

    }

    @Override
    protected void saveStateToBundle(Bundle outState) {

    }

    @Override
    public void onPositiveButton() {
//        super.onPositiveButton();

        if(DBFacade.getCachedMyInfo() != null){
            showProgressDialog();
            PoinilaNetService.verifyPhoneOrMobile(
                    verificationCode.getText().toString(),
                    DBFacade.getCachedMyInfo().id,
                    mobileOrEmail,
                    byEmail
            );

        } else
            Logger.toast(R.string.error_user_not_found);


    }

    @Override
    public void onNeutralButton() {
        super.onNeutralButton();

        DialogLauncher.launchRequestVerificationDialog(getFragmentManager());
    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        if(disableResend)
            return new GeneralDialogData(R.string.verification_code, ConstantsUtils.NO_RESOURCE, R.string.send, R.string.cancel, ConstantsUtils.NO_RESOURCE);
        else
            return new GeneralDialogData(R.string.verification_code, ConstantsUtils.NO_RESOURCE, R.string.send, R.string.cancel, R.string.resend);
    }

    @Override
    protected void initUI(Context context) {

    }

    @Subscribe
    public void onVerifyResponse(VerificationRequestResponse event) {

        dismissProgressDialog();

        dismiss();

        switch (event.code){
            case 200:
                Toast.makeText(getActivity(), R.string.success_verification, Toast.LENGTH_LONG).show();
                BusProvider.getBus().post(new AfterVerifyResponse());
                break;
            case 446:
                Toast.makeText(getActivity(), R.string.error_verification_wrong_code, Toast.LENGTH_LONG).show();
                break;
        }


    }
}
