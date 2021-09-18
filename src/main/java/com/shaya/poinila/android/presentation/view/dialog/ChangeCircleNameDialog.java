package com.shaya.poinila.android.presentation.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;

import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.activity.SettingActivity;
import com.shaya.poinila.android.util.ConstantsUtils;

import butterknife.Bind;

import static com.shaya.poinila.android.presentation.view.activity.SettingActivity.SettingType.CIRCLE_NAME;
import static com.shaya.poinila.android.util.ConstantsUtils.NO_RESOURCE;

/**
 * Created by iran on 2015-09-23.
 */
public class ChangeCircleNameDialog extends SingleTextFieldDialog {
    private static final String KEY_CIRCLE_NAME = "circle name";
    @Length(max = ConstantsUtils.max_length_circle_name, min = ConstantsUtils.min_length_circle_name, messageResId = R.string.error_circle_name_length)
    @Bind(R.id.input_field) public EditText inputField;
    private String circleName;

    public static ChangeCircleNameDialog newInstance(String oldCircleName) {
        Bundle args = new Bundle();
        ChangeCircleNameDialog fragment = new ChangeCircleNameDialog();
        fragment.circleName = oldCircleName;
        fragment.saveStateToBundle(args);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void saveStateToBundle(Bundle outState) {
        super.saveStateToBundle(outState);
        outState.putString(KEY_CIRCLE_NAME, circleName);
    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(R.string.edit_circle_name, NO_RESOURCE, R.string.submit, R.string.cancel, NO_RESOURCE);
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {
        super.loadStateFromBundle(savedInstanceState);
        circleName = savedInstanceState.getString(KEY_CIRCLE_NAME, "");
    }

    @Override
    protected void initUI(Context context) {
        super.initUI(context);
        inputField.setText(circleName);
    }

    @Override
    protected SettingActivity.SettingType getSettingType() {
        return CIRCLE_NAME;
    }

    @Override
    protected void setTextFieldInputMethod() {
        inputLayout.setCounterEnabled(true);
        inputLayout.setCounterMaxLength(ConstantsUtils.max_length_circle_name);
        inputLayout.setHint(getString(R.string.circle_name));
    }

    @Override
    protected int getItemPosition() {
        return adapterPosition;
    }


}
