package com.shaya.poinila.android.presentation.view.dialog;

/**
 * Created by AlirezaF on 12/25/2015.
 */

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;

import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.activity.SettingActivity;
import com.shaya.poinila.android.util.ConstantsUtils;

import butterknife.Bind;

import static com.shaya.poinila.android.util.ConstantsUtils.KEY_ABOUT_ME;
import static com.shaya.poinila.android.util.ConstantsUtils.NO_RESOURCE;

/**
 * Created by iran on 2015-10-11.
 */
public class EditAboutMeDialog extends SingleTextFieldDialog {
    @Length(max = ConstantsUtils.max_length_about_me, messageResId = R.string.error_max_500)
    @Bind(R.id.input_field)
    public EditText inputField;

    private String aboutMeString;

    public static EditAboutMeDialog newInstance(String oldAboutMe) {

        Bundle args = new Bundle();
        EditAboutMeDialog fragment = new EditAboutMeDialog();
        fragment.aboutMeString = oldAboutMe;
        fragment.saveStateToBundle(args);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void saveStateToBundle(Bundle outState) {
        super.saveStateToBundle(outState);
        outState.putString(KEY_ABOUT_ME, aboutMeString);
    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(NO_RESOURCE, NO_RESOURCE, R.string.submit, R.string.cancel, NO_RESOURCE);
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {
        super.loadStateFromBundle(savedInstanceState);
        aboutMeString = savedInstanceState.getString(KEY_ABOUT_ME, "");
    }

    @Override
    protected SettingActivity.SettingType getSettingType() {
        return SettingActivity.SettingType.ABOUT_ME;
    }

    @Override
    protected void initUI(Context context) {
        super.initUI(context);
        inputField.setText(aboutMeString);
    }

    @Override
    protected void setTextFieldInputMethod() {
        inputLayout.setHint(getString(R.string.hint_about_me));
        inputField.setMinLines(3);
        inputField.setMaxLines(5);
        inputLayout.setCounterEnabled(true);
        inputLayout.setCounterMaxLength(ConstantsUtils.max_length_about_me);
    }

    @Override
    protected int getItemPosition() {
        return -1;
    }

}

