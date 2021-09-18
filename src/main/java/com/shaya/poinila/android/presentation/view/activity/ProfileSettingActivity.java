package com.shaya.poinila.android.presentation.view.activity;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.SimpleSettingTextSetEvent;
import com.shaya.poinila.android.presentation.view.dialog.ChangeNameDialog;
import com.shaya.poinila.android.presentation.view.dialog.EditAboutMeDialog;
import com.shaya.poinila.android.presentation.view.dialog.PoinilaAlertDialog;
import com.shaya.poinila.android.util.Logger;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;
import data.PoinilaNetService;
import data.event.ProfileSettingReceivedEvent;
import data.event.UpdateProfileSettingResponse;
import data.model.Member;

import static com.shaya.poinila.android.util.StringUtils.emptyIfNull;

public class ProfileSettingActivity extends ToolbarActivity {

    private static final String TAG_CHANGE_NAME_DIALOG = "change name";
    //private static final String TAG_CHANGE_USERNAME_DIALOG = "change username";
    private static final String TAG_CHANGE_EMAIL_DIALOG = "change email";
    private static final String TAG_CHANGE_PHONE_DIALOG = "change phone";
    @Bind(R.id.name) View nameItem;
    //@Bind(R.actorID.username) View usernameItem;
    //@Bind(R.id.password) View passwordItem;
    @Bind(R.id.email) View emailItem;
    @Bind(R.id.about_me) View aboutMeItem;
    @Bind(R.id.phone) View phoneItem;
    //@Bind(R.actorID.gender) View genderItem;
    @Bind(R.id.website) View websiteItem;
    /*@Bind(R.id.deactivate) View deactivateItem;
    @Bind(R.id.deactivate_switch) Switch deactivateSwitch;*/

    @BindString(R.string.name) String name;
    //@BindString (R.string.username) String username;
    //@BindString (R.string.password) String passwordStringRes;
    @BindString (R.string.email) String email;
    @BindString (R.string.about_me) String aboutMe;
    @BindString (R.string.phone) String phone;
    //@BindString (R.string.gender) String gender;
    @BindString (R.string.website) String website;
   /* @BindString(R.string.deactivate) String deactivate;*/

    private Member originalProfile;
    private Member changedProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initUI() { //TODO: gender nadarim tu requestesh! :))
        String[] labels = {name, email, phone, aboutMe, website};
        View[] items = {nameItem, emailItem, phoneItem, aboutMeItem, websiteItem};
        for (int i = 0; i < items.length; i++){
            ((TextView) items[i].findViewById(R.id.label)).setText(labels[i]);
        }

        showProgressDialog();
        /*deactivateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changedProfile.isActive = isChecked;
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_done_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_submit_changes){
            if (changedProfile != null) {
//                PoinilaNetService.updateProfileSetting(changedProfile);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe public void onResponse(UpdateProfileSettingResponse response){
        if (response.success){
            Logger.toast(R.string.successfully_updated);
            getActivity().finish();
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_profile_setting;
    }

    @OnClick(R.id.name)
    public void onName(){
        ChangeNameDialog.newInstance(changedProfile.fullName).show(getSupportFragmentManager(), null);
    }

   /* @OnClick(R.actorID.username)
    public void onUsername(){
        //TODO
        new PoinilaAlertDialog.Builder().setPositiveText(ResourceUtils.getString(R.string.submit)).
                setNegativeText(ResourceUtils.getString(R.string.cancelBtn)).
                setBody(new ChangeUserameDialog()).
                build().show(getSupportFragmentManager(), TAG_CHANGE_USERNAME_DIALOG);
    }*/

    //TODO: this is temporary. future releases must let user to change phone/email through verification process
//    @OnClick(R.id.email)
//    public void onEmail(){
//        //TODO
//        ChangeEmailDialog.newInstance(changedProfile.email).show(getFragmentManager(), TAG_CHANGE_EMAIL_DIALOG);
//    }

    @OnClick(R.id.about_me)
    public void onAboutMe(){
        EditAboutMeDialog.newInstance(changedProfile.aboutMe).show(getSupportFragmentManager(), null);
    }


    /*@OnClick( R.id.phone)
    public void onPhone(){
        ChangePhoneDialog.newInstance(changedProfile.mobileNumber).show(getFragmentManager(), TAG_CHANGE_PHONE_DIALOG);
    }*/

    /*@OnClick(R.actorID.gender) public void onGender(){
        new PoinilaAlertDialog.Builder().setPositiveText(ResourceUtils.getString(R.string.submit)).
                setNegativeText(ResourceUtils.getString(R.string.cancelBtn)).
                setBody(new CHANGE_GENDER_DIALOG()).
                build().show(getSupportFragmentManager(), null);
    }*/

    @OnClick(R.id.website) public void onWebsite(){
//        ChangeWebsiteDialog.newInstance(changedProfile.url, changedProfile.urlName).show(getSupportFragmentManager(), null);
    }

    @Subscribe public void onSimpleSettingValueSet(SimpleSettingTextSetEvent event){
        switch (event.settingType){
            case FullName:
                changedProfile.fullName = event.value;
                ((TextView) nameItem.findViewById(R.id.value)).setText(changedProfile.fullName);
                break;
            case EMAIL:
                changedProfile.email = event.value;
                ((TextView) emailItem.findViewById(R.id.value)).setText(changedProfile.email);
                break;
            case PHONE:
                changedProfile.mobileNumber = event.value;
                ((TextView) phoneItem.findViewById(R.id.value)).setText(changedProfile.mobileNumber);
                break;
            case ABOUT_ME:
                changedProfile.aboutMe = event.value;
                ((TextView) aboutMeItem.findViewById(R.id.value)).setText(changedProfile.aboutMe);
                break;
            case WEBSITE:
                String[] nameUrl = event.value.split("&");
                ((TextView)websiteItem.findViewById(R.id.value)).setText(nameUrl[0] + "\n" + nameUrl[1]);
                changedProfile.urlName = nameUrl[0];
                changedProfile.url = nameUrl[1];
                break;
            /*case PHONE:
                ((TextView)phoneItem.findViewById(R.id.value)).setText(event.value);
                changedProfile.mobileNumber = event.value;
                break;*/
        }


    }

    @Subscribe public void onUserProfileSettingReceived(ProfileSettingReceivedEvent event){
        originalProfile = event.member;
        requestOnFirstTime = false;
        changedProfile = new Member(originalProfile);
        fill(originalProfile);
        onSuccessfulResponse();
    }

    private void fill(Member originalProfile) {
        String website = emptyIfNull(originalProfile.urlName) + "\n" + emptyIfNull(originalProfile.url);
        String[] values = new String[]{originalProfile.fullName, originalProfile.email, originalProfile.aboutMe, website};
        View[] items = {nameItem, emailItem, aboutMeItem, websiteItem};
        for (int i = 0; i < values.length; i++) {
            ((TextView) items[i].findViewById(R.id.value)).setText(values[i]);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (requestOnFirstTime)
            PoinilaNetService.getProfileSettings();
    }

    // in first look it's possible for user not to notice save action menu and lose his/her data accidentally
    @Override
    protected void handleUpNavigation() {
        if (originalProfile != null && !originalProfile.equals(changedProfile)) {
            new PoinilaAlertDialog.Builder().
                    setMessage(R.string.discared_changes).
                    setPositiveBtnText(R.string.yes).
                    setNegativeBtnText(R.string.no).
                    setPositiveBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            NavUtils.navigateUpFromSameTask(getActivity());
                        }
                    }).
            build().show(getSupportFragmentManager(), null);
        }else{
            super.handleUpNavigation();
        }
    }

    @Override
    public void onBackPressed() {
        if (originalProfile != null && !originalProfile.equals(changedProfile)) {
            new PoinilaAlertDialog.Builder().
                    setMessage(R.string.discared_changes).
                    setPositiveBtnText(R.string.yes).
                    setNegativeBtnText(R.string.no).
                    setPositiveBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ProfileSettingActivity.super.onBackPressed();
                        }
                    }).
                    build().show(getSupportFragmentManager(), null);
        }else{
            super.onBackPressed();
        }
    }
}
