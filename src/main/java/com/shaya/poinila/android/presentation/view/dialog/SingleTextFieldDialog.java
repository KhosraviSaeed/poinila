package com.shaya.poinila.android.presentation.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.SimpleSettingTextSetEvent;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.presentation.view.activity.SettingActivity.SettingType;
import com.shaya.poinila.android.util.BusProvider;

import java.util.List;

import butterknife.Bind;

/**
 * Created by iran on 2015-07-21.
 */
public abstract class SingleTextFieldDialog extends BusDialogFragment implements Validator.ValidationListener {
    public static final String ADAPTER_POSITION = "adapter position";
    public int adapterPosition = -1;
    protected Validator validator;
    //protected SettingType settingType;
    @Bind(R.id.input_field) public TextInputEditText inputField;
    @Bind(R.id.field_input_layout) public TextInputLayout inputLayout;

    @Override
    public int getLayoutResId() {
        return R.layout.dialog_single_text_input;
    }


    @Override
    protected void initUI(Context context) {
        setTextFieldInputMethod();
        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    protected abstract SettingType getSettingType();

    protected abstract void setTextFieldInputMethod();

    /**
     * We need this in time of catching event;
     */
    protected abstract int getItemPosition();

    @Override
    public void onPositiveButton() {
        validator.validate(false);
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {
        adapterPosition = savedInstanceState.getInt(ADAPTER_POSITION, -1);
    }

    @Override
    protected void saveStateToBundle(Bundle outState) {
        outState.putInt(ADAPTER_POSITION, adapterPosition);
    }

    @Override
    public void onValidationSucceeded() {
        BusProvider.getBus().post(new SimpleSettingTextSetEvent(
                getSettingType(), inputField.getText().toString(), getItemPosition()));
        onNegativeButton();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        ViewUtils.handleSaripaarErrors(errors, getActivity());
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

}
