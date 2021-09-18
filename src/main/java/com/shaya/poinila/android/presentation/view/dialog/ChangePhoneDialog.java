package com.shaya.poinila.android.presentation.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.activity.SettingActivity;

import butterknife.Bind;

/**
 * Created by iran on 1/9/2016.
 */
public class ChangePhoneDialog extends SingleTextFieldDialog{

    @NotEmpty(trim = true, messageResId = R.string.error_required_field)

    private static final String KEY_PHONE_NO = "old mobileNumber";
    private String mobileNumber;

    @Override
    protected void initUI(Context context) {
        super.initUI(context);

        inputField.setText(mobileNumber);
    }

    public static ChangePhoneDialog newInstance(String oldCellPhone) {
        Bundle args = new Bundle();
        ChangePhoneDialog fragment = new ChangePhoneDialog();
        args.putString(KEY_PHONE_NO, oldCellPhone);
        fragment.saveStateToBundle(args);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected SettingActivity.SettingType getSettingType() {
        return SettingActivity.SettingType.PHONE;
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {
        super.loadStateFromBundle(savedInstanceState);

        mobileNumber = savedInstanceState.getString(KEY_PHONE_NO, "");
    }

    @Override
    protected void setTextFieldInputMethod() {
        inputField.setInputType(InputType.TYPE_CLASS_PHONE);
    }

    @Override
    protected int getItemPosition() {
        return -1;
    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(R.string.edit_phone_no, RESOURCE_NONE, R.string.submit, R.string.cancel, RESOURCE_NONE);
    }
}
