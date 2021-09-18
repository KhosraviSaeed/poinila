package com.shaya.poinila.android.presentation.view.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.Length;;
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

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import data.PoinilaNetService;
import data.event.MyInfoReceivedEvent;
import data.event.RegisterResponseEvent;
import data.event.UserNameValidityEvent;
import data.model.Gender;
import manager.DataRepository;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

public class RegisterFragment extends BusFragment implements Validator.ValidationListener, FragmentCompat.OnRequestPermissionsResultCallback {
    private static final String KEY_VERIFICATION_CODE = "verification code";

    @Bind(R.id.card_title)
    TextView title;
    //actually doesn't exist in graphic design. I chose a similar layout
    //for reusability purposes.
    @Bind(R.id.left_arrow)
    ImageView arrow;

    @Bind(R.id.verification_code)
    EditText verificationCode;

    @Bind(R.id.fullname_input)
    EditText fullName;

    /*@Length(min = 6, max = 32, messageResId = R.string.error_username_length)
    @Pattern(regex = "[a-zA-Z0-9_\\.\\u0600-\\u06FF\\uFB8A\\u067E\\u0686\\u06AF]+", messageResId = R.string.error_username_rule)*/
    @Bind(R.id.username_input)
    EditText userName;

    /*@Order(value = 1)
    @Email
    @Bind(R.id.email_input)
    EditText email;*/

    @Length(max = 40, min = 6, messageResId = R.string.error_password_length)
    @Bind(R.id.password_input)
    EditText password;

    /*@Order(value = 2)
    @ConfirmPassword
    @Bind(R.id.confirm_password_input)
    EditText confirm_password;*/

    @Bind(R.id.gender_container)
    RadioGroup genderRadioGroup;
    @Bind(R.id.male)
    RadioButton maleRadioBtn;
    @Bind(R.id.female)
    RadioButton femaleRadioBtn;

    @Checked(messageResId = R.string.error_must_agree_terms)
    @Bind(R.id.terms_checkbox)
    CheckBox termsCheckBox;

    @Bind(R.id.terms_textview)
    TextView termsTextView;

    @Bind(R.id.signup)
    Button signupButton;

    @Bind(R.id.toggle_visibility)
    ImageView toggleVisibilityBtn;

    private String verificationCodeString;
    private Validator validator;
    private boolean passwordVisible = false;
    private boolean smsPermissionDeclined;
    private SimpleActivityResultPermissionDelegate permissionHandlerDelegate;

    private boolean byEmail;
    private String emailOrPhone;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public int getLayoutID() {
        return R.layout.fragment_register;
    }

    public static RegisterFragment newInstance(String code, boolean byEmail, String emailOrPhone) {
        Bundle args = new Bundle();
        if(code != null)
            args.putString(KEY_VERIFICATION_CODE, code);

        RegisterFragment fragment = new RegisterFragment();

        fragment.byEmail = byEmail;
        fragment.emailOrPhone = emailOrPhone;
        fragment.setArguments(args);
        return fragment;
    }

    public static RegisterFragment newInstance(String code) {

        Bundle args = new Bundle();
        args.putString(KEY_VERIFICATION_CODE, code);
        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validator = new Validator(this);
        validator.setValidationListener(this);
        if (getArguments() != null)
            verificationCodeString = getArguments().getString(KEY_VERIFICATION_CODE, null);
        else if (savedInstanceState != null)
            verificationCodeString = savedInstanceState.getString(KEY_VERIFICATION_CODE, null);
        else
            verificationCodeString = "";

        permissionHandlerDelegate = new SimpleActivityResultPermissionDelegate(){

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
            public void handleValidResults(int requestCode, Intent data) {}// we don't start activity for result, a better design perhaps...
        };
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_VERIFICATION_CODE, verificationCodeString);
    }

    @Override
    protected void initUI() {
        title.setText(getString(R.string.signup_in_poinila));
        arrow.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(verificationCodeString)) {
            verificationCode.setText(verificationCodeString);
        }
        termsTextView.setText(Html.fromHtml(
                "<a href=\"http://ponila.com/#/terms/\">" +
                        getString(R.string.terms_of_condition_word) + "</a> " +
                        getString(R.string.terms_of_condition_tail)));
        termsTextView.setMovementMethod(LinkMovementMethod.getInstance());

        termsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!TextUtils.isEmpty(verificationCode.getText().toString()))
                    signupButton.setEnabled(isChecked);
            }
        });

        verificationCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                signupButton.setEnabled(true);
//                signupButton.setEnabled(!TextUtils.isEmpty(verificationCode.getText().toString()) && termsCheckBox.isChecked());
            }
        });

        userName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String tempUserName;
                if (!hasFocus && !(tempUserName = ((EditText) v).getText().toString().trim()).isEmpty()) {
                    PoinilaNetService.checkUserNameValidity(tempUserName);
                }
            }
        });
    }

    @Subscribe
    public void onUserNameValidityResponse(UserNameValidityEvent event) {
        if (!event.success) {
            String error = "";
            switch (event.error) {
                case UserNameValidityEvent.DUPLICATE:
                    error = getString(R.string.error_already_taken_username);
                    break;
                case UserNameValidityEvent.RESERVED:
                    error = getString(R.string.error_invalid_username);
                    break;
                case UserNameValidityEvent.LENGTH:
                    error = getString(R.string.error_username_length);
                    break;
                case UserNameValidityEvent.RULE:
                    error = getString(R.string.error_username_rule);
                    break;
            }
            if (!TextUtils.isEmpty(error)) userName.setError(error);
        }
    }

    @OnClick(R.id.signup)
    public void onSignup() {
        validator.validate();
        //onValidationSucceeded();
    }

    @Subscribe
    public void onRegisterResponse(RegisterResponseEvent event) {

        if (event.successful)
            DataRepository.getInstance().getMyInfo(ConnectionUitls.isNetworkOnline(), MyInfoReceivedEvent.MY_INFO_TYPE.LOAD);
        else if(event.errorCode == RegisterResponseEvent.USED_VERIFICATION_CODE){
            dismissProgressDialog();
            ViewUtils.temporaryError(verificationCode, getString(R.string.error_already_used_verification_code));
        }
        else if (event.errorCode == RegisterResponseEvent.DUPLICATE_USERNAME) {
            dismissProgressDialog();
            ViewUtils.temporaryError(userName, getString(R.string.error_already_taken_username));
        } else {
            dismissProgressDialog();
            Logger.toast(R.string.error_fail_register);
        }
    }

    @Subscribe
    public void onUserInfoReceived(MyInfoReceivedEvent event) {

        if(event.type != MyInfoReceivedEvent.MY_INFO_TYPE.LOAD)
            return;

        onGettingInitDataResponse(event);

        new AsyncTask<Object, Void, Void>() {

            @Override
            protected Void doInBackground(Object... params) {
                DataRepository.syncWithMyInfoResponse((MyInfoReceivedEvent)params[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void o) {
                PonilaAccountManager.getInstance().initUserTag();
                decideAboutNextPage();
            }


        }.execute(event);

    }

    private void decideAboutNextPage() {
        PageChanger.goToSelectInterest(getActivity(), true);
        getActivity().finish(); // one must not be able to navigate back to register
        dismissProgressDialog();
    }

    public void fillVerificationCode(String code) {
        verificationCode.setText(code);
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
        try{
            if (!smsPermissionDeclined)
                getActivity().unregisterReceiver(smsReceiver);
        }catch (IllegalArgumentException exception){
            // just ignore it since we don't know user granted permission or not
        }
    }


    @Subscribe
    public void onSmsReceived(SmsReceivedEvent event) {
        fillVerificationCode(event.str);
    }

    @Override
    public void onValidationSucceeded() {
        PoinilaNetService.register(
                "",
//                verificationCode.getText().toString().trim(),
                fullName.getText().toString().trim(),
                userName.getText().toString().trim(),
                getSelectedGender(),
                password.getText().toString().trim(),
                byEmail,
                emailOrPhone
        );
        showProgressDialog();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getActivity());

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Logger.toastError(message);
            }
        }
        //Logger.toast(getString(R.string.signup_faild));
    }

    public Gender getSelectedGender() {
        return genderRadioGroup.getCheckedRadioButtonId() == maleRadioBtn.getId() ? Gender.MALE : Gender.FEMALE;
    }

    @OnClick(R.id.toggle_visibility)
    public void onChangeVisibility() {
        passwordVisible ^= true; // toggle

        int start = password.getSelectionStart();
        int end = password.getSelectionEnd();
        password.setInputType(passwordVisible ?
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        CalligraphyUtils.applyFontToTextView(getActivity(), password, CalligraphyConfig.get().getFontPath());
        password.setSelection(start, end);

        toggleVisibilityBtn.setImageResource(passwordVisible ?
                R.drawable.toggle_visible_nobel_32dp :
                R.drawable.toggle_invisible_nobel_32dp);
    }

    /*---------*/

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

}
