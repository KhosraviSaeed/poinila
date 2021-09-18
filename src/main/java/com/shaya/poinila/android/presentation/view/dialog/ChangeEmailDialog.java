package com.shaya.poinila.android.presentation.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.activity.SettingActivity;
import com.shaya.poinila.android.util.ConstantsUtils;

import butterknife.Bind;

import static com.shaya.poinila.android.util.ConstantsUtils.NO_RESOURCE;

/**
 * Created by iran on 2015-07-21.
 */
public class ChangeEmailDialog extends SingleTextFieldDialog {

    private static final String KEY_EMAIL = "email";
    @NotEmpty(trim = true, messageResId = R.string.error_required_field)
    @Length(max = ConstantsUtils.max_length_email, messageResId = R.string.error_max_50)
    @Email(messageResId = R.string.error_mail)
    @Bind(R.id.input_field) public EditText inputField;
    private String email;

    public static ChangeEmailDialog newInstance(String oldEmail) {
        Bundle args = new Bundle();
        ChangeEmailDialog fragment = new ChangeEmailDialog();
        fragment.email = oldEmail;
        fragment.saveStateToBundle(args);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initUI(Context context) {
        super.initUI(context);
        inputField.setText(email);
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {
        super.loadStateFromBundle(savedInstanceState);
        email = savedInstanceState.getString(KEY_EMAIL, "");
    }

    @Override
    protected void saveStateToBundle(Bundle outState) {
        super.saveStateToBundle(outState);
        outState.putString(KEY_EMAIL, email);
    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(R.string.change_email, NO_RESOURCE, R.string.submit, R.string.cancel, NO_RESOURCE);
    }

    @Override
    protected SettingActivity.SettingType getSettingType() {
        return SettingActivity.SettingType.EMAIL;
    }

    @Override
    protected void setTextFieldInputMethod() {
        inputField.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
        inputField.setHint(R.string.hint_email);
    }

    @Override
    protected int getItemPosition() {
        return -1;
    }
}
