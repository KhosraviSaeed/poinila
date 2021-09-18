package com.shaya.poinila.android.presentation.view.dialog;

import android.widget.EditText;

import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.activity.SettingActivity;
import com.shaya.poinila.android.util.ConstantsUtils;

import butterknife.Bind;

import static com.shaya.poinila.android.presentation.view.activity.SettingActivity.SettingType.NEW_CIRCLE;

/**
 * Created by iran on 2015-09-23.
 */
public class NewCircleDialog extends SingleTextFieldDialog {
    @Length(max = ConstantsUtils.max_length_circle_name, min = ConstantsUtils.min_length_circle_name, messageResId = R.string.error_circle_name_length)
    @Bind(R.id.input_field) public EditText inputField;

    @Override
    protected SettingActivity.SettingType getSettingType() {
        return NEW_CIRCLE;
    }

    @Override
    protected void setTextFieldInputMethod() {
        inputLayout.setCounterEnabled(true);
        inputLayout.setCounterMaxLength(ConstantsUtils.max_length_circle_name);
        inputLayout.setHint(getString(R.string.circle_name));
    }

    @Override
    protected int getItemPosition() {
        return -1;
    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(R.string.new_circle, RESOURCE_NONE, R.string.submit, R.string.cancel, RESOURCE_NONE);
    }

}
