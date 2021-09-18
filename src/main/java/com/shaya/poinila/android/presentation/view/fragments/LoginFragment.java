package com.shaya.poinila.android.presentation.view.fragments;


import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.SmsReceiver;
import com.shaya.poinila.android.presentation.uievent.GoogleLoginSucceedEvent;
import com.shaya.poinila.android.presentation.view.activity.SignUpLoginActivity;
import com.shaya.poinila.android.util.ConnectionUitls;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.util.PoinilaPreferences;
import com.shaya.poinila.android.utils.PonilaAccountManager;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.OnClick;
import data.PoinilaNetService;
import data.event.LoginFailedEvent;
import data.event.LoginSucceedEvent;
import data.event.MyInfoReceivedEvent;
import manager.DataRepository;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

public class LoginFragment extends BusFragment
        implements GoogleApiClient.OnConnectionFailedListener, PonilaAccountManager.OnGoogleSignInResult {//implements BackForthButtonsBox.OnBackForthListener


    /*@Bind(R.id.signup_login_buttons)
    BackForthButtonsBox backForthButtonsBox;*/
    @Bind(R.id.card_title)
    TextView title;

    @Bind(R.id.button_signup)
    Button signupButton;
    @Bind(R.id.button_login)
    Button loginButton;
    @Bind(R.id.button_guest_login)
    Button guestLoginButton;

    @Email
    @Bind(R.id.username_input)
    EditText username;

    @Bind(R.id.toggle_visibility)
    ImageView toggleVisibilityBtn;

    // TODO: custom rule to send request
    @Bind(R.id.password_input)
    EditText password;

    //actually doesn't exist in graphic design. I chose a similar layout
    //for reusability purposes.
    @Bind(R.id.left_arrow)
    ImageView arrow;

    @Bind(R.id.google_sign_in_btn)
    Button signInButton;

    @Bind(R.id.forgot_password_textview)
    TextView forgotPassTV;
    private boolean forgotPasswordShowing;
    private boolean passwordVisible;
    //Validator mValidator;

    GoogleApiClient mGoogleApiClient;
    GoogleSignInOptions gso;

    // Just for Sign in With Google
    private boolean firstLoginDoneByGoogle = false;
    private boolean isSignInWithGoogle = false;
    private static final int REQUEST_GET_ACCOUNT = 112;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public int getLayoutID() {
        return R.layout.fragment_login;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // this permission is for login by google(Android Marshmallow & above)
        if(android.os.Build.VERSION.SDK_INT > 22){
            ActivityCompat.requestPermissions(getActivity(),new String[]{android.Manifest.permission.GET_ACCOUNTS},REQUEST_GET_ACCOUNT);
        }
    }

    @Override
    protected void initUI() {
        arrow.setVisibility(View.GONE);
        title.setText(getString(R.string.login_in_poinila));
        //backForthButtonsBox.setBackForthListener(this);
    }

    @OnClick(R.id.forgot_password_textview)
    public void onForgotPassword() {
        goToForgotPassword();
    }

    private void goToForgotPassword() {
        ((SignUpLoginActivity) getActivity()).goToForgotPassword();
    }

    @Subscribe
    public void onSuccessfulLogin(LoginSucceedEvent event) {
        //DataRepository.setUserAsAnonymous(true);
        DataRepository.getInstance().getMyInfo(ConnectionUitls.isNetworkOnline(), MyInfoReceivedEvent.MY_INFO_TYPE.LOAD);
    }

    @Subscribe
    public void onSuccessfulLogin(GoogleLoginSucceedEvent event) {
        //DataRepository.setUserAsAnonymous(true);
        firstLoginDoneByGoogle = event.firstLoginDoneByGoogle;
        isSignInWithGoogle = true;
        DataRepository.getInstance().getMyInfo(ConnectionUitls.isNetworkOnline(), MyInfoReceivedEvent.MY_INFO_TYPE.LOAD);

    }

    @Subscribe
    public void onLoginFail(LoginFailedEvent event) {
        //Logger.toast("failed");
        switch (event.code) {
            case 401: // access error
                Logger.toast(R.string.error_login);
                break;
            case 400: // bad parameter like sending a null device id
                break;
        }
        dismissProgressDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Subscribe
    public void onUserInfoReceived(MyInfoReceivedEvent event) {
        onGettingInitDataResponse(event);

        if (event.type != MyInfoReceivedEvent.MY_INFO_TYPE.LOAD) return;

        if(LoginFragment.this.getActivity() == null) return;

        new AsyncTask<Object, Void, Void>() {

            @Override
            protected Void doInBackground(Object... params) {
                DataRepository.syncWithMyInfoResponse((MyInfoReceivedEvent) params[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void o) {

                if(LoginFragment.this.getActivity() == null) return;

                PonilaAccountManager.getInstance().initUserTag();

                if(isSignInWithGoogle)
                    PonilaAccountManager.getInstance().setGoogle();

                dismissProgressDialog();
                if(firstLoginDoneByGoogle){
                    PageChanger.goToSelectInterest(LoginFragment.this.getContext(), true);
                }else {
                    PageChanger.goToDashboard(LoginFragment.this.getContext());
                }
                getActivity().finish(); // one must not be able to navigate back to login
            }


        }.execute(event);


    }

    SmsReceiver smsReceiver = new SmsReceiver();

    @Override
    public void onStart() {
        super.onStart();
        // TODO: listen for sms delivery
        getActivity().registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

        PonilaAccountManager.getInstance().initGoogleAPIClient(getActivity(), this);
        PonilaAccountManager.getInstance().connectGoogleApiClient();
    }

    @Override
    public void onStop() {
        super.onStop();
        // TODO: unregister receiver. but I don't know if it's correct. may be we should listen
        // in all application life cycle
        getActivity().unregisterReceiver(smsReceiver);

        PonilaAccountManager.getInstance().stopAutoManageGoogleApiClient(getActivity());
        PonilaAccountManager.getInstance().disconnectGoogleApiClient();
    }

   /* @Override
    public void onValidationSucceeded() {
        NavigationUtils.goToActivity(MainActivity.class, getActivity());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        Logger.toast(getString(R.string.login_faild));

    }*/

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
        String uniqueNameOrEmail = username.getText().toString();
        boolean isEmail = Patterns.EMAIL_ADDRESS.matcher(uniqueNameOrEmail).matches();
        if (isEmail) {
            PoinilaNetService.login(null, uniqueNameOrEmail, password.getText().toString());
        } else {
            PoinilaNetService.login(uniqueNameOrEmail, null, password.getText().toString());
        }
    }

    @Override
    public ViewGroup getLoadableView() {
        return null;
    }

    @Override
    public boolean mustShowProgressView() {
        return false;
    }

    //-----------------

    @OnClick(R.id.button_login)
    public void onLogin() {
        if (!ConnectionUitls.isNetworkOnline()) {
            Logger.toast(R.string.warning_connect_to_network);
            return;
        }
        showProgressDialog();
        initData();
    }

    @OnClick(R.id.button_signup)
    public void onSignUp() {
        ((SignUpLoginActivity) getActivity()).goToVerificationRequest();
    }

    @OnClick(R.id.button_guest_login)
    public void onGuestLogin() {
        DataRepository.setUserAsAnonymous(true);
        PageChanger.goToDashboard(getActivity());
    }

    @OnClick(R.id.google_sign_in_btn)
    public void gLogin(){
        PonilaAccountManager.getInstance().signInWithGoogleAPI(this);
        showProgressDialog();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        dismissProgressDialog();
        Logger.toastError(R.string.error_google_connection);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if(data != null && resultCode == Activity.RESULT_OK){
            PonilaAccountManager.getInstance().getGoogleSignInAccount(data, this);
        }else
            Logger.toastError(R.string.error_google_sign_in);

    }

    @Override
    public void onSuccessGoogleSignIn(GoogleSignInAccount acct) {
//        Log.i(getClass().getName(), "getIdToken = " +  acct.getIdToken());
        PoinilaPreferences.putGoogleToken(acct.getIdToken());
        PoinilaNetService.loginByGoogle(acct.getIdToken());
    }

    @Override
    public void onFailureGoogleSignIn(GoogleSignInAccount acct) {
        dismissProgressDialog();
    }
}
