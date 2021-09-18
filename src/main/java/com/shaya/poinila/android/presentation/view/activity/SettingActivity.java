package com.shaya.poinila.android.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.AfterVerifyResponse;
import com.shaya.poinila.android.presentation.uievent.PositiveButtonClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.SimpleSettingTextSetEvent;
import com.shaya.poinila.android.presentation.view.dialog.ChangeEmailDialog;
import com.shaya.poinila.android.presentation.view.dialog.ChangeNameDialog;
import com.shaya.poinila.android.presentation.view.dialog.ChangePhoneDialog;
import com.shaya.poinila.android.presentation.view.dialog.ChangeWebsiteDialog;
import com.shaya.poinila.android.presentation.view.dialog.DialogLauncher;
import com.shaya.poinila.android.presentation.view.dialog.EditAboutMeDialog;
import com.shaya.poinila.android.presentation.view.dialog.PoinilaAlertDialog;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.util.NavigationUtils;
import com.shaya.poinila.android.utils.PonilaAccountManager;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;
import data.PoinilaNetService;
import data.event.ProfileSettingReceivedEvent;
import data.event.UpdateProfileSettingResponse;
import data.model.Member;
import manager.DataRepository;

import static com.shaya.poinila.android.util.StringUtils.emptyIfNull;

public class SettingActivity extends ToolbarActivity {

//    @Bind(R.id.profile)
//    View profileItem;

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

    @Bind(R.id.password)
    View passwordItem;
    @Bind(R.id.app_settings)
    View appSettingsItem;
    @Bind(R.id.frames)
    View framesItem;
    @Bind(R.id.circles)
    View circlesItem;
    @Bind(R.id.app_notifications)
    View appNotifsItem;
    //@Bind(R.id.email_notifications) View emailNotifsItem;
    @Bind(R.id.logout)
    View logoutItem;
    @Bind(R.id.help)
    View helpItem;
    @Bind(R.id.contact_us)
    View contactUsItem;
    @Bind(R.id.about_poinila)
    View aboutPoinilaItem;
    @Bind(R.id.rules)
    View rulesItem;

//    @BindString(R.string.profile)
//    String profileText;

    @BindString(R.string.name) String name;
    //@BindString (R.string.username) String username;
    //@BindString (R.string.password) String passwordStringRes;
    @BindString (R.string.email) String email;
    @BindString (R.string.about_me) String aboutMe;
    @BindString (R.string.phone) String phone;
    //@BindString (R.string.gender) String gender;
    @BindString (R.string.website) String website;
    /* @BindString(R.string.deactivate) String deactivate;*/

    @BindString (R.string.setting_rules_item) String rulesTxt;

    @BindString(R.string.password)
    String passwordText;
    @BindString(R.string.app_settings)
    String appSettingsTxt;
    @BindString(R.string.frames_management)
    String frameManagementTxt;
    @BindString(R.string.manage_circle)
    String circleManagementTxt;
    @BindString(R.string.app_notifs)
    String appNotifsTxt;
    //@BindString(R.string.email_notifs) String emailNotifsTxt;
    @BindString(R.string.logout)
    String logoutTxt;
    @BindString(R.string.tutorial)
    String tutorialTxt;
    @BindString(R.string.contact_us)
    String contact_usTxt;
    @BindString(R.string.about_poinila)
    String aboutPoinilaTxt;


    private Member originalProfile;
    private Member changedProfile;

    private static final String TAG_CHANGE_PHONE_DIALOG = "change phone";
    private static final String TAG_CHANGE_EMAIL_DIALOG = "change email";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PonilaAccountManager.getInstance().initGoogleAPIClient(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }
        });
    }

    @Override
    protected void initUI() {
        String[] labels = {passwordText, appSettingsTxt, frameManagementTxt, circleManagementTxt, appNotifsTxt,
                logoutTxt, tutorialTxt, contact_usTxt, aboutPoinilaTxt, rulesTxt};
        View[] items = {passwordItem, appSettingsItem, framesItem, circlesItem, appNotifsItem,
                logoutItem, helpItem, contactUsItem, aboutPoinilaItem, rulesItem};
        for (int i = 0; i < items.length; i++) {
            ((TextView) items[i].findViewById(R.id.label)).setText(labels[i]);
            if(items[i].equals(framesItem)){
                items[i].findViewById(R.id.help_btn).setVisibility(View.VISIBLE);
                items[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogLauncher.launchMessageDialog(getSupportFragmentManager(), R.string.frame, R.string.frame_about);
                    }
                });
            }

            if(items[i].equals(circlesItem)){
                items[i].findViewById(R.id.help_btn).setVisibility(View.VISIBLE);
                items[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogLauncher.launchMessageDialog(getSupportFragmentManager(), R.string.circle, R.string.circle_about);
                    }
                });
            }


        }

        // Profile Frame
        String[] profileLabels = {name, email, phone, aboutMe, website};
        View[] profileItems = {nameItem, emailItem, phoneItem, aboutMeItem, websiteItem};
        for (int i = 0; i < profileItems.length; i++){
            ((TextView) profileItems[i].findViewById(R.id.label)).setText(profileLabels[i]);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(requestOnFirstTime)
            PoinilaNetService.getProfileSettings();
    }

    @Subscribe public void onUserProfileSettingReceived(ProfileSettingReceivedEvent event){
        originalProfile = event.member;
        requestOnFirstTime = false;
        changedProfile = new Member(originalProfile);
        fill(originalProfile);
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
        }

        showProgressDialog();
        PoinilaNetService.updateProfileSetting(changedProfile, event.settingType);
    }

//    @OnClick(R.id.profile)
//    public void onProfile() {
//        NavigationUtils.goToActivity(ProfileSettingActivity.class, getActivity());
//    }


    @Subscribe
    public void onUpdateProfileSettingResponse(UpdateProfileSettingResponse event){
        dismissProgressDialog();
        switch (event.settingType){
            case EMAIL:
                DialogLauncher.launchRequestVerificationDialog(getSupportFragmentManager(), R.string.email_change, changedProfile.email, true);
                break;
            case PHONE:
                DialogLauncher.launchRequestVerificationDialog(getSupportFragmentManager(), R.string.phone_change, changedProfile.mobileNumber, false);
                break;
            default:
                Logger.toast(R.string.successfully_updated);
                originalProfile = changedProfile;
                fill(originalProfile);
        }
    }

    @Subscribe
    public void onAfterVerifyResponse(AfterVerifyResponse event){
        originalProfile = changedProfile;
        fill(originalProfile);
    }

    @OnClick(R.id.email)
    public void onEmail(){
        //TODO
        DialogLauncher.launchRequestVerificationDialog(getSupportFragmentManager(), R.string.email_change, changedProfile.email, true);
//        ChangeEmailDialog.newInstance(changedProfile.email).show(getSupportFragmentManager(), TAG_CHANGE_EMAIL_DIALOG);
    }

    @OnClick(R.id.phone)
    public void onPhone(){
        DialogLauncher.launchRequestVerificationDialog(getSupportFragmentManager(), R.string.phone_change, changedProfile.mobileNumber, false);
//        ChangePhoneDialog.newInstance(changedProfile.mobileNumber).show(getSupportFragmentManager(), TAG_CHANGE_PHONE_DIALOG);
    }

    @OnClick(R.id.password)
    public void onPassword() {
        NavigationUtils.goToActivity(ChangePasswordActivity.class, getActivity());
    }



    @OnClick(R.id.frames)
    public void onFrames() {
        NavigationUtils.goToActivity(FramesManagementActivity.class, getActivity());
    }

    @OnClick(R.id.circles)
    public void onCircles() {
        // TODO
        NavigationUtils.goToActivity(CirclesManagementActivity.class, getActivity());
    }

    @OnClick(R.id.app_settings)
    public void onAppSettings() {
        NavigationUtils.goToActivity(AppSettingActivity.class, getActivity());
    }

    @OnClick(R.id.app_notifications)
    public void onAppNotifs() {
        Intent intent = NavigationUtils.makeNavigationIntent(NotificationSwitchActivity.class, getActivity());
        intent.putExtra(ConstantsUtils.KEY_REQUEST_ID, ConstantsUtils.REQUEST_APPLICATION_NOTIFICATION);
        startActivity(intent);
    }

    /*@OnClick(R.id.email_notifications) public void onEmailNotifs(){
        Intent intent = NavigationUtils.makeNavigationIntent(NotificationSwitchActivity.class, getActivity());
        intent.putExtra(ConstantsUtils.KEY_REQUEST_ID, ConstantsUtils.REQUEST_EMAIL_NOTIFICATION);
        startActivity(intent);
    }*/


    private String getValue(String value){
        return TextUtils.isEmpty(value) ? "" : value;
    }


    @OnClick(R.id.name)
    public void onName(){
        if(changedProfile != null)
            ChangeNameDialog.newInstance(getValue(changedProfile.fullName)).show(getSupportFragmentManager(), null);
        else
            Logger.toastError(R.string.profile_change_not_found);
    }

    @OnClick(R.id.about_me)
    public void onAboutMe(){

        if(changedProfile != null)
            EditAboutMeDialog.newInstance(getValue(changedProfile.aboutMe)).show(getSupportFragmentManager(), null);
        else
            Logger.toastError(R.string.profile_change_not_found);
    }

    @OnClick(R.id.website) public void onWebsite(){

        if(changedProfile != null)
            ChangeWebsiteDialog.newInstance(getValue(changedProfile.url), getValue(changedProfile.urlName)).show(getSupportFragmentManager(), null);
        else
            Logger.toastError(R.string.profile_change_not_found);
    }


    @OnClick(R.id.rules)
    public void onRules(){
        DialogLauncher.launchMessageDialog(getSupportFragmentManager(), R.string.setting_rules_item, R.string.setting_rule);
    }

    private void fill(Member originalProfile) {
        String website = "";
        if(!TextUtils.isEmpty(originalProfile.url))
            website = emptyIfNull(originalProfile.urlName) + "\n" + emptyIfNull(originalProfile.url);
        else
            website = emptyIfNull(originalProfile.urlName);

        String[] values = new String[]{originalProfile.fullName, originalProfile.email, originalProfile.mobileNumber, originalProfile.aboutMe, website};
        View[] items = {nameItem, emailItem, phoneItem, aboutMeItem, websiteItem};
        for (int i = 0; i < values.length; i++) {
            ((TextView) items[i].findViewById(R.id.value)).setText(values[i]);
        }
    }

    @OnClick(R.id.logout)
    public void onLogout() {
        new PoinilaAlertDialog.Builder().setTitle(R.string.logout).
                setMessage(R.string.warning_logout_earase_data).
                setPositiveBtnText(R.string.logout).
                setNegativeBtnText(R.string.cancel).
                build().show(getSupportFragmentManager(), null);
    }

    @OnClick(R.id.help)
    public void onHelp() {
        PageChanger.goToHelpActivity(getActivity(), true);
    }

    @OnClick(R.id.contact_us)
    public void onContactUs() {
        DialogLauncher.launchContactUsDialog(getSupportFragmentManager());
    }

    @OnClick(R.id.about_poinila)
    public void onAboutPoinila() {
        DialogLauncher.launchAboutPoinila(getFragmentManager());
    }

    @Subscribe
    public void onPositiveDialogButton(PositiveButtonClickedUIEvent event) {
        if(PonilaAccountManager.getInstance().isSignInWithGoogle()){
            if(PonilaAccountManager.getInstance().isConnectedGoogleApiClient()) {
                PonilaAccountManager.getInstance().signOutWithGoogleAPI(new ResultCallback() {
                    @Override
                    public void onResult(@NonNull Result result) {
                        //TODO ANY
                    }
                });
            }else {
                Logger.toastError(R.string.error_google_connection);
                return;
            }
        }

        PonilaAccountManager.getInstance().removeUserTag();
        DataRepository.logoutEvent();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_setting;
    }

    public enum SettingType {
        USERNAME,
        EMAIL,
        CIRCLE_NAME,
        NEW_CIRCLE,
        NEW_FRAME,
        FRAME_NAME,
        GENDER,
        WEBSITE,
        ABOUT_ME,
        FullName,
        PHONE
    }
}
