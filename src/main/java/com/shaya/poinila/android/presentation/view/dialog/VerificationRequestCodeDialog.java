package com.shaya.poinila.android.presentation.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.presentation.view.activity.SignUpLoginActivity;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.Logger;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import data.PoinilaNetService;
import data.event.VerificationRequestResponse;
import manager.DBFacade;

/**
 * Created by iran on 7/18/2016.
 */
public class VerificationRequestCodeDialog extends BusDialogFragment {

    @Bind(R.id.radio_group)
    RadioGroup optionsRadioGroup;
    @Bind(R.id.input_field)
    EditText inputField;
    @Bind(R.id.input_filed_icon)
    ImageView inputFieldIcon;
    private boolean mVerificationByEmail;
    private String inputValue;
    private boolean disableRadioBtns = false;
    private int titleRes = R.string.verifying;

    public static VerificationRequestCodeDialog newInstance(){
        VerificationRequestCodeDialog fragment = new VerificationRequestCodeDialog();
        return fragment;
    }

    public static VerificationRequestCodeDialog newInstance(int titleRes, String inputValue, boolean mVerificationByEmail){
        VerificationRequestCodeDialog fragment = new VerificationRequestCodeDialog();
        fragment.inputValue = inputValue;
        fragment.mVerificationByEmail = mVerificationByEmail;
        fragment.disableRadioBtns = true;
        fragment.titleRes = titleRes;
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
        return R.layout.dialog_verification_request;
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {

    }

    @Override
    protected void saveStateToBundle(Bundle outState) {

    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(titleRes, ConstantsUtils.NO_RESOURCE, R.string.send, R.string.cancel, ConstantsUtils.NO_RESOURCE);
    }

    @Override
    protected void initUI(Context context) {
        //backForthButtonsBox.setBackForthListener(this);
        optionsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.email_option:
                        onMailOption();
                        break;
                    case R.id.sms_option:
                        onSmsOption();
                        break;
                }
            }
        });


        optionsRadioGroup.setVisibility(disableRadioBtns ? View.GONE : View.VISIBLE);

        inputField.setText(inputValue);
        if (mVerificationByEmail) onMailOption();
        else onSmsOption();
    }

    private void onSmsOption() {
        optionsRadioGroup.check(R.id.sms_option);
        mVerificationByEmail = false;
        inputField.setInputType(InputType.TYPE_CLASS_PHONE);
        inputFieldIcon.setImageResource(R.drawable.phone_48dp);
    }

    private void onMailOption() {
        optionsRadioGroup.check(R.id.email_option);
        mVerificationByEmail = true;
        inputField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        inputFieldIcon.setImageResource(R.drawable.email_48dp);
    }

    @Subscribe
    public void onVerifyResponse(VerificationRequestResponse event) {
        dismissProgressDialog();

        if (event.succeed) {
            if(disableRadioBtns)
                DialogLauncher.launchInputVerificationCodeDialog(getFragmentManager(),
                    inputField.getText().toString(), mVerificationByEmail, disableRadioBtns);
            else
                DialogLauncher.launchInputVerificationCodeDialog(getFragmentManager(),
                        inputField.getText().toString(), mVerificationByEmail);
            Logger.toast(mVerificationByEmail ? R.string.success_verification_mail : R.string.success_verification_sms);
            dismiss();
        } else {
            Logger.toastError(event.errorExplanation);
        }
        optionsRadioGroup.setEnabled(true);
    }

    @Override
    public void onPositiveButton() {
//        super.onPositiveButton();

        showProgressDialog();
        String userText = inputField.getText().toString().trim();
        if (mVerificationByEmail && !Patterns.EMAIL_ADDRESS.matcher(userText).matches()) {
            inputField.setText("");
            ViewUtils.temporaryError(inputField, getString(R.string.error_invalid_email));
            return;
        } else if (!mVerificationByEmail && !Patterns.PHONE.matcher(userText).matches()) {
            inputField.setText("");
            ViewUtils.temporaryError(inputField, getString(R.string.error_invalid_phone));
            return;
        }
        /*if (!mVerificationByEmail){ // sms
            initData();
        }else{*//*
            Logger.toast(R.string.success_verification_mail);
        //}*/
        optionsRadioGroup.setEnabled(false);
        if(DBFacade.getCachedMyInfo() != null)
            PoinilaNetService.requestVerificationCode(mVerificationByEmail, userText, disableRadioBtns ? 0 : DBFacade.getCachedMyInfo().id);
    }

}
