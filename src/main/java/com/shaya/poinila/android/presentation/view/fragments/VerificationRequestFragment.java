package com.shaya.poinila.android.presentation.view.fragments;

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
import com.shaya.poinila.android.util.Logger;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import data.PoinilaNetService;
import data.event.VerificationRequestResponse;

/**
 * Created by iran on 12/6/2015.
 */
public class VerificationRequestFragment extends BusFragment {
    @Bind(R.id.card_title)
    TextView titleTextView;
    @Bind(R.id.radio_group)
    RadioGroup optionsRadioGroup;
    @Bind(R.id.input_field)
    EditText inputField;
    @Bind(R.id.input_filed_icon)
    ImageView inputFieldIcon;
    @Bind(R.id.verify_button)
    Button verifyButton;
    private boolean mVerificationByEmail;

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
        return R.layout.fragment_request_verification_code;
    }

    @Override
    protected void initUI() {
        //backForthButtonsBox.setBackForthListener(this);
        ButterKnife.findById(rootView, R.id.left_arrow).setVisibility(View.GONE);
        titleTextView.setText(R.string.verifying);
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
        if (mVerificationByEmail) onMailOption();
        else onSmsOption();
    }

    private void onSmsOption() {
        mVerificationByEmail = false;
        inputField.setInputType(InputType.TYPE_CLASS_PHONE);
        inputFieldIcon.setImageResource(R.drawable.phone_48dp);
    }

    private void onMailOption() {
        mVerificationByEmail = true;
        inputField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        inputFieldIcon.setImageResource(R.drawable.email_48dp);
    }

    @Subscribe
    public void onVerifyResponse(VerificationRequestResponse event) {
        dismissProgressDialog();
        if (event.succeed) {
            ((SignUpLoginActivity) getActivity()).goToRegister(null, event.byEmail, event.emailOrPhone);
            Logger.toast(mVerificationByEmail ? R.string.success_verification_mail : R.string.success_verification_sms);
        } else {
            ViewUtils.temporaryError(inputField, event.errorExplanation);
        }
        optionsRadioGroup.setEnabled(true);
    }

    @OnClick(R.id.verify_button)
    public void onVerificationRequest() {
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
        PoinilaNetService.requestVerificationCode(mVerificationByEmail, userText);
    }


}
