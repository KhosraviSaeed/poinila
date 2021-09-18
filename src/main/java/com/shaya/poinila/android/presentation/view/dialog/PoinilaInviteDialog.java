package com.shaya.poinila.android.presentation.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;

import com.shaya.poinila.android.presentation.R;

import butterknife.Bind;
import data.PoinilaNetService;

/**
 * Created by iran on 2015-10-01.
 */
public class PoinilaInviteDialog extends BaseDialogFragment{
    @Bind(R.id.email_input) EditText emailInput;

    @Bind(R.id.message) EditText messageInput;

    @Override
    public int getLayoutResId() {
        return R.layout.dialog_invite_to_poinila;
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {
    }

    @Override
    protected void saveStateToBundle(Bundle outState) {

    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(R.string.invite_to_poinila, RESOURCE_NONE, R.string.send, R.string.cancel, RESOURCE_NONE);
    }

    @Override
    protected void initUI(Context context) {

    }

    @Override
    public void onPositiveButton() {
        PoinilaNetService.inviteToPoinila(emailInput.getText().toString(), messageInput.getText().toString());
        super.onPositiveButton();
    }
}
