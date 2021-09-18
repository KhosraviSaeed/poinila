package com.shaya.poinila.android.presentation.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.util.ConstantsUtils;

import java.util.List;

import butterknife.Bind;
import data.PoinilaNetService;

/**
 * Created by iran on 2015-11-07.
 */
public class ContactUsDialog extends BaseDialogFragment implements Validator.ValidationListener{
    @Length(max = ConstantsUtils.max_length_report_title, messageResId = R.string.error_max_200)
    @NotEmpty(trim = true, messageResId = R.string.error_required_field)
    @Bind(R.id.title)
    EditText titleInput;

    @Length(max = ConstantsUtils.max_length_report_content, messageResId = R.string.error_max_5000)
    @NotEmpty(trim = true, messageResId = R.string.error_required_field)
    @Bind(R.id.content)
    EditText contentInput;

    @Bind(R.id.radio_group) public RadioGroup radioGroup;
    @Bind(R.id.left_textview) public TextView leftTextView;
    @Bind(R.id.right_textview) public TextView rightTextView;

    private Validator validator;

    @Override
    public int getLayoutResId() {
        return R.layout.dialog_contact_us;
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {

    }

    @Override
    protected void saveStateToBundle(Bundle outState) {

    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(R.string.contact_us, RESOURCE_NONE, R.string.send, R.string.cancel, RESOURCE_NONE);
    }

    @Override
    protected void initUI(Context context) {
        ViewUtils.setText(leftTextView, getString(R.string.critics_and_suggestions));
        ViewUtils.setText(rightTextView, getString(R.string.report_bug));

        validator = new Validator(this);
        validator.setValidationListener(this);

        contentInput.setMinLines(3);
        contentInput.setMaxLines(7);
    }

    @Override
    public void onPositiveButton() {
        validator.validate(false);
        super.onPositiveButton();
    }

    @Override
    public void onValidationSucceeded() {
        PoinilaNetService.sendReport(
                radioGroup.getCheckedRadioButtonId() == R.id.left_radioBtn ? "proposal" : "bug",
                titleInput.getText().toString().trim(),
                contentInput.getText().toString().trim());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        ViewUtils.handleSaripaarErrors(errors, getActivity());
    }
}
