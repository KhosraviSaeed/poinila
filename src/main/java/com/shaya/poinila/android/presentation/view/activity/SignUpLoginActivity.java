package com.shaya.poinila.android.presentation.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.shaya.poinila.android.presentation.BuildConfig;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.dialog.DialogLauncher;
import com.shaya.poinila.android.presentation.view.dialog.ForgotPasswordFragment;
import com.shaya.poinila.android.presentation.view.fragments.LoginFragment;
import com.shaya.poinila.android.presentation.view.fragments.RegisterFragment;
import com.shaya.poinila.android.presentation.view.fragments.ResetPasswordFragment;
import com.shaya.poinila.android.presentation.view.fragments.VerificationRequestFragment;
import com.shaya.poinila.android.util.DeviceInfoUtils;
import com.shaya.poinila.android.util.Logger;

import butterknife.Bind;

public class SignUpLoginActivity extends BaseActivity {

    private static final String KEY_STATE = "state";
    private static final String TAG_LOGIN_FRAGMENT = "login";
    // public static final String PHASE_LOGIN = "select interest";
    @Bind(R.id.welcome_text)
    TextView welcome_text;

    private int state;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getData() != null) { // TODO
            if (BuildConfig.DEBUG)
                Logger.toast(getIntent().getData().toString());
            handleUrl(getIntent().getData());
        } else if (savedInstanceState != null) {
            // TODO
            state = savedInstanceState.getInt(KEY_STATE);
            switch (state) {
                case ForgotPassword:
                    goToForgotPassword();
                    break;
                case Login:
                    goToLoginFragment(true);
                    break;
                case Register:
                    goToRegister(null);
                    break;
                case RequestVerificationCode:
                    goToVerificationRequest();
                    break;
                case ResetPassword:
                    goToResetPassword(null);
                    break;
            }
        } else {
            goToLoginFragment(true);
        }
    }

    private void handleUrl(Uri uri) {
        if (uri.getPath().contains("register")) {
            goToRegister(uri.getLastPathSegment());
            /*RegisterFragment fragment = ((RegisterFragment) getSupportFragmentManager().findFragmentById(R.id.content));
            if (fragment.isVisible()) fragment.fillVerificationCode(uri.getLastPathSegment());*/

        } else if (uri.getPath().contains("resetpassword")) {
            goToResetPassword(uri.getLastPathSegment());
           /* ResetPasswordFragment fragment = ((ResetPasswordFragment) getSupportFragmentManager().findFragmentById(R.id.content));
            if (fragment.isVisible()) fragment.fillIdentificationCodeInput();*/
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_STATE, state);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void initUI() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void handleToolbar() {
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_sign_up_login;
    }

    public void goToLoginFragment(boolean proceed) {
        if (proceed) {
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.container, new LoginFragment(), TAG_LOGIN_FRAGMENT).
                    //addToBackStack(TAG_LOGIN_FRAGMENT).
                            commit();
        } else { // recede
            //getSupportFragmentManager().popBackStack(TAG_LOGIN_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            // temp
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.container, new LoginFragment(), TAG_LOGIN_FRAGMENT).
                    //addToBackStack(TAG_LOGIN_FRAGMENT).
                            commit();
        }
        welcome_text.setText(R.string.welcome_text);
        welcome_text.setOnClickListener(null);

        state = Login;
    }

    public void goToRegister(String code, boolean byEmail, String emailOrPhone) {
        getSupportFragmentManager().beginTransaction().
                setCustomAnimations(R.anim.fade_in, R.anim.fade_out).
                replace(R.id.container, RegisterFragment.newInstance(code, byEmail, emailOrPhone)).
                //addToBackStack(null).
                        commit();

        state = Register;
    }

    public void goToRegister(String code) {
        getSupportFragmentManager().beginTransaction().
                setCustomAnimations(R.anim.fade_in, R.anim.fade_out).
                replace(R.id.container, code != null ? RegisterFragment.newInstance(code) : new RegisterFragment()).
                //addToBackStack(null).
                        commit();

        state = Register;
    }

    public void goToVerificationRequest() {
        getSupportFragmentManager().beginTransaction().
                setCustomAnimations(R.anim.fade_in, R.anim.fade_out).
                replace(R.id.container, new VerificationRequestFragment()).
                //addToBackStack(null).
                        commit();

        showLoginText();

        state = RequestVerificationCode;
    }

    private void showLoginText() {
        welcome_text.setText(R.string.already_registered);
        welcome_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginFragment(false);
            }
        });
    }

    public void goToResetPassword(String code) {
        getSupportFragmentManager().beginTransaction().
                setCustomAnimations(R.anim.fade_in, R.anim.fade_out).
                replace(R.id.container, code != null ? ResetPasswordFragment.newInstance(code) : new ResetPasswordFragment()).
                //addToBackStack(null).
                        commit();

        state = ResetPassword;
    }

    public void goToForgotPassword() {
        getSupportFragmentManager().beginTransaction().
                setCustomAnimations(R.anim.fade_in, R.anim.fade_out).
                replace(R.id.container, new ForgotPasswordFragment()).
                //addToBackStack(null).
                        commit();

        showLoginText();

        state = ForgotPassword;
        /*new PoinilaDialog.Builder().setTitle(R.string.recover_password)
                .setMessage(R.string.recover_password_message).setPositiveText(R.string.submit).setNegativeText(R.string.cancel)
                .setBody(new ForgotPasswordFragment())
                .build().show(getChildFragmentManager(), null);*/
    }

    private static final int Login = 1;
    private static final int RequestVerificationCode = 2;
    private static final int ResetPassword = 3;
    private static final int ForgotPassword = 4;
    private static final int Register = 5;
}
