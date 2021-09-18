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

import static com.shaya.poinila.android.presentation.view.activity.SettingActivity.SettingType.FRAME_NAME;
import static com.shaya.poinila.android.util.ConstantsUtils.NO_RESOURCE;

public class ChangeFrameNameDialog extends SingleTextFieldDialog {
    private static final java.lang.String KEY_FRAME_NAME = "frame name";
    @Length(max = ConstantsUtils.max_length_frame_name, min = ConstantsUtils.min_length_frame_name, messageResId = R.string.error_frame_name_length)
    @Bind(R.id.input_field) public EditText inputField;
    String frameName;

    public static ChangeFrameNameDialog newInstance(String oldFrameName) {
        Bundle args = new Bundle();
        ChangeFrameNameDialog fragment = new ChangeFrameNameDialog();
        fragment.frameName = oldFrameName;
        fragment.saveStateToBundle(args);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initUI(Context context) {
        super.initUI(context);
        inputField.setText(frameName);
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {
        super.loadStateFromBundle(savedInstanceState);
        frameName = savedInstanceState.getString(KEY_FRAME_NAME, "");
    }

    @Override
    protected void saveStateToBundle(Bundle outState) {
        super.saveStateToBundle(outState);
        outState.putString(KEY_FRAME_NAME, frameName);
    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(R.string.edit_frame_name, NO_RESOURCE, R.string.submit, R.string.cancel, NO_RESOURCE);
    }

    @Override
    protected SettingActivity.SettingType getSettingType() {
        return FRAME_NAME;
    }

    @Override
    protected void setTextFieldInputMethod() {
        inputLayout.setCounterEnabled(true);
        inputLayout.setCounterMaxLength(ConstantsUtils.max_length_frame_name);
        inputLayout.setHint(getString(R.string.frame_name));
    }

    @Override
    protected int getItemPosition() {
        return adapterPosition;
    }
}
