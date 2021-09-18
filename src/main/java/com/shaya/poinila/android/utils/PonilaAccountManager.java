package com.shaya.poinila.android.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.onesignal.OneSignal;
import com.shaya.poinila.android.presentation.PoinilaApplication;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.util.PoinilaPreferences;

import java.io.IOException;

import data.model.Member;
import manager.DBFacade;
import ru.noties.debug.Debug;

/**
 * Created by iran on 5/29/2016.
 */
public class PonilaAccountManager {

    private AccountManager accountManager;              // Android class
    private static PonilaAccountManager instance = null;     // Singleton instance
    private String ponilaAccountType;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;
    public final static int GOOGLE_SIGN_IN_REQUEST_CODE = 10;
    //================================================================================
    // Methods
    //================================================================================

    /**
     * Private constructor
     */
    private PonilaAccountManager(){
        accountManager = AccountManager.get(PoinilaApplication.getAppContext());
        ponilaAccountType = PoinilaApplication.getAppContext().getString(R.string.account_type);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(PoinilaApplication.getAppContext().getString(R.string.server_client_id))
                .requestEmail()
                .build();
    }

    /**
     * Get the singleton instance
     * @return PonilaAccountManager instance
     */
    public static PonilaAccountManager getInstance(){
        if(instance == null){
            instance = new PonilaAccountManager();
        }

        return instance;
    }

    /**
     * <p>
     * Get the first account of the given type
     * @param accountType Account type
     * @return Account (if not found -> null)
     */
    public Account getFirstAccount(String accountType) {
        Account[] accounts = accountManager.getAccountsByType(accountType);
        if (accounts.length > 0) {
            return accounts[0];
        } else {
            return null;
        }
    }

    /**
     * Check whether at least a Goftalk account exists
     * @return True|False
     */
    public boolean ponilaAccountExists() {
        return getPonilaAccountsNum() > 0;
    }

    /**
     * <b>Get the account token of the first found account
     * of the given type</br>
     * @param accountType Type of account
     * @return Account token (if not found = "")
     */
    public String getFirstAccountToken(String accountType) {
        Account[] accounts = accountManager.getAccountsByType(accountType);
        if (accounts.length > 0) {
            return accountManager.peekAuthToken(
                    accounts[0],
                    accountType);
        } else {
            return "";
        }
    }

    public void initGoogleAPIClient(FragmentActivity activity, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener){

        if(isConnectedGoogleApiClient()) return;
        if(mGoogleApiClient != null) mGoogleApiClient.stopAutoManage(activity);
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, 0, onConnectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                }).build();

    }

    public void connectGoogleApiClient(){
        if(mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    public void signInWithGoogleAPI(Fragment fragment){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        fragment.startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
    }

    /**
     * Get Object Of Google Account
     * @param data
     * @param onGoogleSignInResult
     * @return
     */
    public GoogleSignInAccount getGoogleSignInAccount(Intent data, OnGoogleSignInResult onGoogleSignInResult){
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        GoogleSignInAccount acct = result.getSignInAccount();
        Log.d(getClass().getName(), "result = " + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            onGoogleSignInResult.onSuccessGoogleSignIn(acct);
        } else {
            // Signed out, show unauthenticated UI.
            onGoogleSignInResult.onFailureGoogleSignIn(acct);
        }

        return acct;
    }

    /**
     * Sign Out From Google Account
     * @param resultCallback
     */
    public void signOutWithGoogleAPI(ResultCallback resultCallback){
//        revokeAccessGoogleAPI(resultCallback);
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(resultCallback);

    }

    public void stopAutoManageGoogleApiClient(FragmentActivity activity){
        if(isConnectedGoogleApiClient()) mGoogleApiClient.stopAutoManage(activity);
    }

    public void disconnectGoogleApiClient() {
        if(isConnectedGoogleApiClient()) mGoogleApiClient.disconnect();
    }

    public boolean isConnectedGoogleApiClient(){
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    /**
     * Disconnect Ponila From Google Account
     * @param resultCallback
     */
    public void revokeAccessGoogleAPI(ResultCallback resultCallback){
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(resultCallback);
    }

    public interface OnGoogleSignInResult{
        public void onSuccessGoogleSignIn(GoogleSignInAccount acct);
        public void onFailureGoogleSignIn(GoogleSignInAccount acct);
    }

    public void setGoogle(){
        setPonilaAccountData("sign_in_with_google", "yes");
    }

    public boolean isSignInWithGoogle(){
        return !TextUtils.isEmpty(getPonilaAccountData("sign_in_with_google"));
    }

    /**
     * Get the Ponila account token
     * @return Ponila account token (if not found => "")
     */
    public String getPonilaAccountToken() {
        return getFirstAccountToken(ponilaAccountType);
    }

    /**
     * Get username of the Goftalk account
     * @return Ponil username (if not found => "")
     */
    public String getPonilaAccountUsername() {
        Account gAccunt = getFirstAccount(ponilaAccountType);
        if (gAccunt != null) return gAccunt.name;
        else return "";
    }


    /**
     * set extra data
     * @param key
     * @param value
     */
    public void setPonilaAccountData(String key, String value) {

        if(ponilaAccountExists()){
            // Get account (we know there exist at least one account of this type)
            Account PonilaAccount = accountManager.getAccountsByType(ponilaAccountType)[0];
            // Set the data
            accountManager.setUserData(PonilaAccount, key, value);
        }

    }

    /**
     * get extra data
     * @param key
     * @return
     */
    public String getPonilaAccountData(String key) {

        // If no account is found, return ""
        if (!ponilaAccountExists()) {
            return "";
        } else {
            // Get account
            Account account = AccountManager.get(PoinilaApplication.getAppContext()).getAccountsByType(ponilaAccountType)[0];

            // Return value
            String value = "";
            try {
                value = AccountManager.get(PoinilaApplication.getAppContext()).getUserData(account, key);
            }catch(SecurityException e){
                e.printStackTrace();
            }

            if (value == null) return "";
            return value;
        }
    }

    /**
     * Get the number of Ponila accounts
     * @return Number of Ponila accounts
     */
    private int getPonilaAccountsNum() {
        return AccountManager
                .get(PoinilaApplication.getAppContext())
                .getAccountsByType(ponilaAccountType)
                .length;
    }

    /**
//     * Remove the Ponila account from phone
//     */
    public void removePonilaAccount() {
        Debug.i("RemovePonilaAccounts");
        // Callback of removing operation
        AccountManagerCallback<Boolean> remCallback = new AccountManagerCallback<Boolean>() {
            @Override
            public void run(AccountManagerFuture<Boolean> future) {
                if (future.isDone()) {
                    // If there's still an account, call remove again
                    if (getPonilaAccountsNum() > 0) removePonilaAccount();
                }
            }
        };

        // Get all the Ponila accounts
        Account[] gaccounts = AccountManager
                .get(PoinilaApplication.getAppContext())
                .getAccountsByType(ponilaAccountType);

        // Remove all accounts
        for (Account gaccount : gaccounts) {
            AccountManager
                    .get(PoinilaApplication.getAppContext())
                    .removeAccount(gaccount, remCallback, null);
        }

    }

    /**
     * Add a Ponila account
     * @param username Username
     * @param password Password
     * @param authToken Access Token
     */
    public void addPonilaAccount(String username, String password, String authToken) {
        final Account account = new Account(username, ponilaAccountType);

        PoinilaPreferences.putAuthToken(authToken);

        accountManager.addAccountExplicitly(account, password, null);
        accountManager.setAuthToken(account, ponilaAccountType, authToken);
    }

    public void addPonilaAccountFromGoogle(String authToken) {
        final Account account = new Account(getGoogleAccount().name, ponilaAccountType);

        PoinilaPreferences.putAuthToken(authToken);

        accountManager.addAccountExplicitly(account, "google_login", null);
        accountManager.setAuthToken(account, ponilaAccountType, authToken);
    }

    public void updatePonilaAccount(String password, String authToken) {
        Account[] accounts = accountManager.getAccountsByType(ponilaAccountType);

        if(accounts.length > 0 && password != null)
            accountManager.setPassword(accounts[0], password);

        if(accounts.length > 0 && authToken != null){
            PoinilaPreferences.putAuthToken(authToken);
            accountManager.setAuthToken(accounts[0], ponilaAccountType, authToken);
        }
    }



    public void initUserTag(){
        Member member = DBFacade.getCachedMyInfo();
        if(member != null){
            OneSignal.sendTag("member_id", member.getId());
        }
    }

    public void removeUserTag(){
        OneSignal.deleteTag("member_id");
    }

    public String getPonilaAuthToken(){
        Account[] accounts = accountManager.getAccountsByType(ponilaAccountType);

        if(accounts.length > 0)
            try {
                return accountManager.getAuthToken(accounts[0], ponilaAccountType, new Bundle(), true, null, null)
                        .getResult()
                        .getString(AccountManager.KEY_AUTHTOKEN);
            } catch (OperationCanceledException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AuthenticatorException e) {
                e.printStackTrace();
            }
        return "";
    }


    public Account getGoogleAccount(){
        Account[] accounts = accountManager.getAccounts();

        for(Account account : accounts){
            if(account.type.equals("com.google")) return account;
        }

        return null;
    }



}
