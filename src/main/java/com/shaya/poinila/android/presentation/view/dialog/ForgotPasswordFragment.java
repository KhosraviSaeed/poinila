package com.shaya.poinila.android.presentation.view.dialog;

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
import com.shaya.poinila.android.presentation.view.fragments.BusFragment;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import data.JsonRequestBodyMaker;
import data.PoinilaNetService;
import data.event.VerificationRequestResponse;

/**
 * Created by AlirezaF on 12/7/2015.
 */
public class ForgotPasswordFragment extends BusFragment implements View.OnClickListener {
    private static final String KEY_RECOVERY_TYPE = "recovery type";
//    @Bind(R.id.radio_group)
//    RadioGroup optionsRadioGroup;
    @Bind(R.id.input_field)
    EditText inputField;
    @Bind(R.id.input_filed_icon)
    ImageView inputFieldIcon;
    @Bind(R.id.submit)
    Button SubmitButton;
    @Bind(R.id.card_title)
    TextView title;

    @Bind(R.id.email_option)
    TextView emailOption;

    @Bind(R.id.sms_option)
    TextView smsOption;

    @Bind(R.id.unique_name_option)
    TextView uniqueNameOption;

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.email_option:
                onMailOption();
                break;
            case R.id.sms_option:
                onSmsOption();
                break;
            case R.id.unique_name_option:
                onUniqueNameOption();
                break;
        }
    }

    public enum RECOVERY_PASS_TYPE{
        EMAIL,
        MOBILE_NUMBER,
        UNIQUE_NAME
    }

    private RECOVERY_PASS_TYPE recoveryPassType;

    @Override
    public int getLayoutID() {
        return R.layout.fragment_forgot_password;
    }

    @Override
    protected void initUI() {
        ButterKnife.findById(rootView, R.id.left_arrow).setVisibility(View.GONE);
        emailOption.setOnClickListener(this);
        smsOption.setOnClickListener(this);
        uniqueNameOption.setOnClickListener(this);

        smsOption.setSelected(true);
        inputFieldIcon.setImageResource(R.drawable.phone_48dp);
        recoveryPassType = RECOVERY_PASS_TYPE.MOBILE_NUMBER;

//        optionsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (checkedId) {
//                    case R.id.email_option:
//                        onMailOption();
//                        break;
//                    case R.id.sms_option:
//                        onSmsOption();
//                        break;
//                    case R.id.unique_name_option:
//                        onUniqueNameOption();
//                        break;
//                }
//            }
//        });
        ViewUtils.setText(title, getString(R.string.recover_password));
    }

    private void onMailOption() {
        emailOption.setSelected(true);
        smsOption.setSelected(false);
        uniqueNameOption.setSelected(false);
        recoveryPassType = RECOVERY_PASS_TYPE.EMAIL;
        inputField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        inputFieldIcon.setImageResource(R.drawable.email_48dp);
    }

    private void onSmsOption() {
        emailOption.setSelected(false);
        smsOption.setSelected(true);
        uniqueNameOption.setSelected(false);

        recoveryPassType = RECOVERY_PASS_TYPE.MOBILE_NUMBER;
        inputField.setInputType(InputType.TYPE_CLASS_PHONE);
        inputFieldIcon.setImageResource(R.drawable.phone_48dp);
    }

    private void onUniqueNameOption() {
        emailOption.setSelected(false);
        smsOption.setSelected(false);
        uniqueNameOption.setSelected(true);
        recoveryPassType = RECOVERY_PASS_TYPE.UNIQUE_NAME;
        inputField.setInputType(InputType.TYPE_CLASS_TEXT);
        inputFieldIcon.setImageResource(R.drawable.form_login_user);
    }

    @OnClick(R.id.submit)
    public void onRequestResetPassCode() {
        String userText = inputField.getText().toString().trim();
        if (recoveryPassType.equals(RECOVERY_PASS_TYPE.EMAIL) && !Patterns.EMAIL_ADDRESS.matcher(userText).matches()) {
            inputField.setText("");
            ViewUtils.temporaryError(inputField, getString(R.string.error_invalid_email));
            return;
        } else if (recoveryPassType.equals(RECOVERY_PASS_TYPE.MOBILE_NUMBER) && !Patterns.PHONE.matcher(userText).matches()) {
            inputField.setText("");
            ViewUtils.temporaryError(inputField, getString(R.string.error_invalid_phone));
            return;
        }else if(recoveryPassType.equals(RECOVERY_PASS_TYPE.UNIQUE_NAME)){

        }
        PoinilaNetService.recoverPassword(recoveryPassType, userText);
    }

    @Subscribe
    public void onRequestSent(VerificationRequestResponse event) {
        if (event.succeed) {
            ((SignUpLoginActivity) getActivity()).goToResetPassword(null);
        } else if (event.code == 446){
            switch (recoveryPassType){
                case EMAIL:
                    ViewUtils.temporaryError(inputField, getString(R.string.error_no_occurance_email));
                    break;
                case MOBILE_NUMBER:
                    ViewUtils.temporaryError(inputField, getString(R.string.error_no_occurance_phone));
                    break;
                case UNIQUE_NAME:
                    ViewUtils.temporaryError(inputField, getString(R.string.error_no_occurance_unique_name));
                    break;
            }
        }
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

  /*  @Override
    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_RECOVERY_TYPE, mRecoveryByEmail);
        return bundle;
    }

    @Override
    public void setBundle(Bundle bundle) {
        mRecoveryByEmail = bundle.getBoolean(KEY_RECOVERY_TYPE);
    }*/
}
