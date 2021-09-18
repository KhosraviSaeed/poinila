package com.shaya.poinila.android.presentation.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.mobsandgeeks.saripaar.annotation.Email;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.util.ConstantsUtils;

import butterknife.Bind;
import butterknife.OnClick;
import data.PoinilaNetService;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

/**
 * Created by iran on 8/7/2016.
 */
public class SetUserNamePasswordDialog extends BusDialogFragment {


    @Bind(R.id.username_input)
    EditText username;

    // TODO: custom rule to send request
    @Bind(R.id.password_input)
    EditText password;


    @Bind(R.id.toggle_visibility)
    ImageView toggleVisibilityBtn;


    private boolean passwordVisible;



    public static SetUserNamePasswordDialog newInstance(){
        SetUserNamePasswordDialog fragment = new SetUserNamePasswordDialog();

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
        return R.layout.dialog_set_user_pass;
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {

    }

    @Override
    protected void saveStateToBundle(Bundle outState) {

    }

    @Override
    public void onPositiveButton() {
        super.onPositiveButton();

        PoinilaNetService.setUsernamePassword(username.getText().toString(), password.getText().toString());

        onNegativeButton();
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

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(
                R.string.change_user_pass_title,
                ConstantsUtils.NO_RESOURCE,
                R.string.send,
                R.string.cancel,
                ConstantsUtils.NO_RESOURCE
        );
    }

    @Override
    protected void initUI(Context context) {

    }
}
