package com.shaya.poinila.android.presentation.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.widget.EditText;

import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.activity.SettingActivity;
import com.shaya.poinila.android.presentation.view.activity.SettingActivity.SettingType;
import com.shaya.poinila.android.util.ConstantsUtils;

import butterknife.Bind;

/**
 * Created by iran on 2015-07-21.
 */
public class ChangeNameDialog extends SingleTextFieldDialog{

    private static final String KEY_FULL_NAME = ConstantsUtils.KEY_FULL_NAME;
    @Length(max = ConstantsUtils.max_length_full_name, min = ConstantsUtils.min_length_full_name,
            messageResId = R.string.error_full_name_length, trim = true)
    @Bind(R.id.input_field)
    TextInputEditText fullNameField;
    @Bind(R.id.field_input_layout)
    TextInputLayout inputLayout;

    private String fullName;

    public static ChangeNameDialog newInstance(String fullName) {
        Bundle args = new Bundle();
        ChangeNameDialog fragment = new ChangeNameDialog();
        fragment.fullName = fullName;
        fragment.saveStateToBundle(args);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initUI(Context context) {
        super.initUI(context);
        fullNameField.setText(fullName);
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {
        super.loadStateFromBundle(savedInstanceState);
        fullName = savedInstanceState.getString(KEY_FULL_NAME, "");
    }

    @Override
    protected void saveStateToBundle(Bundle outState) {
        super.saveStateToBundle(outState);
        outState.putString(KEY_FULL_NAME, fullName);
    }

    @Override
    protected SettingType getSettingType() {
        return SettingActivity.SettingType.FullName;
    }

    @Override
    protected void setTextFieldInputMethod() {
        inputLayout.setCounterEnabled(true);
        inputLayout.setCounterMaxLength(ConstantsUtils.max_length_full_name);
        inputLayout.setHint(getString(R.string.hint_full_name));
    }

    @Override
    protected int getItemPosition() {
        return -1;
    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(RESOURCE_NONE, RESOURCE_NONE, R.string.submit, R.string.cancel, RESOURCE_NONE);
    }
}
