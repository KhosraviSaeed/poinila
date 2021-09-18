package com.shaya.poinila.android.presentation.view.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.OnClick;
import data.PoinilaNetService;
import data.event.ServerResponseEvent;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

public class ChangePasswordActivity extends ToolbarActivity{


    @Bind(R.id.first_text_field)
    TextInputEditText oldPasswordField;

    @Bind(R.id.second_text_field)
    TextInputEditText newPasswordField;
    private boolean passwordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initUI() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_pass, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_submit_changes){
            if (validate()) {
                PoinilaNetService.changePassword(newPasswordField.getText().toString(), oldPasswordField.getText().toString());
            }
        }else if (id == R.id.action_toggle_visibility){
            toggleVisibility(item);
        }

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    private void toggleVisibility(MenuItem item) {
        passwordVisible ^= true;
        for (EditText passwordInput : new EditText[]{oldPasswordField, newPasswordField}) {
            int start = passwordInput.getSelectionStart();
            int end = passwordInput.getSelectionEnd();
            passwordInput.setInputType(passwordVisible ?
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            CalligraphyUtils.applyFontToTextView(getActivity(), passwordInput, CalligraphyConfig.get().getFontPath());
            passwordInput.setSelection(start, end);

            item.setIcon(passwordVisible ?
                    R.drawable.invisible_black_24dp :
                    R.drawable.visible_black_24dp);
        }
    }

    private boolean validate() {
        return ViewUtils.validatePasswordInput(newPasswordField);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_change_password;
    }


    @Subscribe public void onServerResponse(ServerResponseEvent event){
        if (!event.succeed){
            switch (event.errorCode){
                case 447:
                    ViewUtils.setInputError(oldPasswordField, R.string.error_old_password_wrong);
                    break;
            }
        }
    }


}
