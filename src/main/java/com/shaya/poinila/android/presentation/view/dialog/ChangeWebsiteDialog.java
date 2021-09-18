package com.shaya.poinila.android.presentation.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Url;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.SimpleSettingTextSetEvent;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.presentation.view.activity.SettingActivity;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.ConstantsUtils;

import java.util.List;

import butterknife.Bind;

/**
 * Created by iran on 2015-10-07.
 */
public class ChangeWebsiteDialog extends BaseDialogFragment {
    private static final String KEY_WEBSITE_NAME = ConstantsUtils.KEY_WEBSITE_NAME;
    private static final String KEY_WEBSITE_ADDRESS = ConstantsUtils.KEY_WEBSITE_URL;
    @Bind(R.id.website_name)
    TextInputEditText websiteNameInput;

    @Bind(R.id.website_url)
    TextInputEditText websiteAddressInput;
    private String websiteName;
    private String websiteAddress;

    @Override
    public int getLayoutResId() {
        return R.layout.dialog_setting_website;
    }

    public static ChangeWebsiteDialog newInstance(String oldWebsiteAddress, String oldWebsiteName) {
        Bundle args = new Bundle();
        ChangeWebsiteDialog fragment = new ChangeWebsiteDialog();
        args.putString(KEY_WEBSITE_ADDRESS, oldWebsiteAddress);
        args.putString(KEY_WEBSITE_NAME, oldWebsiteName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {
        websiteAddress = savedInstanceState.getString(KEY_WEBSITE_ADDRESS, "");
        websiteName = savedInstanceState.getString(KEY_WEBSITE_NAME, "");
    }

    @Override
    protected void saveStateToBundle(Bundle outState) {
        outState.putString(KEY_WEBSITE_ADDRESS, websiteAddressInput.getText().toString());
        outState.putString(KEY_WEBSITE_NAME, websiteNameInput.getText().toString());
    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(R.string.edit_website, RESOURCE_NONE, R.string.submit, R.string.cancel, RESOURCE_NONE);
    }

    @Override
    protected void initUI(Context context) {
        websiteNameInput.setText(websiteName);
        websiteAddressInput.setText(websiteAddress);
    }

    @Override
    public void onPositiveButton() {
        // TODO: how can we achieve the same using saripaar?;
        // it's not valid name is set but url is empty. every other state is valid
        if (validateNameIsNotEmptyWhenURLIsNot(websiteAddressInput, websiteNameInput) && ViewUtils.validateUrl(websiteAddressInput)) {
            BusProvider.getBus().post(new SimpleSettingTextSetEvent(
                    SettingActivity.SettingType.WEBSITE,
                    websiteNameInput.getText().toString() + "&" + websiteAddressInput.getText().toString().toLowerCase(), -1));
        }
        dismiss();
    }

    private boolean validateNameIsNotEmptyWhenURLIsNot(EditText websiteAddressInput, EditText websiteNameInput) {
        if (!TextUtils.isEmpty(websiteNameInput.getText().toString()) &&
                TextUtils.isEmpty(websiteAddressInput.getText().toString())) {

            ViewUtils.setInputError(websiteAddressInput, R.string.error_websiteNameSetURLNot);
            return false;
        }
        return true;
    }
}
