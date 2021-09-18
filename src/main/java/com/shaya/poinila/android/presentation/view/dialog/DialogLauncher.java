
package com.shaya.poinila.android.presentation.view.dialog;

import android.app.FragmentManager;
import android.view.View;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.PositiveButtonClickedUIEvent;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.Logger;

import data.model.Collection;
import data.model.FriendRequestAnswer;
import data.model.Member;
import data.model.Post;
import data.model.SuggestedWebPagePost;


/**
 * Created by iran on 2015-09-26.
 */

public class DialogLauncher {
    public static void launchChangeFriendCircle(android.support.v4.app.FragmentManager fragmentManager, Member member) {
        ChangeFriendCirclesDialog.newInstance(member).show(fragmentManager, null);
    }

    public static void launchEditCollectionDialog(android.support.v4.app.FragmentManager fragmentManager, Collection collection) {
        EditCollectionDialog.newInstance(collection).show(fragmentManager, null);
    }

    public static void launchDeleteCollection(android.support.v4.app.FragmentManager fragmentManager) {
        new PoinilaAlertDialog.Builder().setTitle(R.string.remove_collectoin).
                setMessage(R.string.confirm_delete_collection).
                setNegativeBtnText(R.string.no).
                setPositiveBtnText(R.string.yes).
                build().show(fragmentManager, null);
    }

    public static void launchPickCoverFromPosts(android.support.v4.app.FragmentManager fragmentManager, String collectionID) {
        CoverFromPostsDialog.newInstance(collectionID).show(fragmentManager, null);
    }


    public static void launchNewWebsitePost(android.support.v4.app.FragmentManager fragmentManager) {
        new NewWebsitePostDialog().show(fragmentManager, null);
    }

    public static void launchAboutPoinila(FragmentManager fragmentManager) {
        new AboutPoinilaDialog().show(fragmentManager, null);
    }

    public static void launchContactUsDialog(android.support.v4.app.FragmentManager supportFragmentManager) {
        new ContactUsDialog().show(supportFragmentManager, null);
    }

    public static void launchFriendshipDialog(final Member member, android.support.v4.app.FragmentManager fragmentManager) {
        switch (member.friendshipStatus) {
            case NotFriend:
            case WaitingForAction:
                new PoinilaAlertDialog.Builder().
                        setTitle(R.string.friend_request).
                        setMessage(R.string.approve_send_friend_request).
                        setPositiveBtnText(R.string.yes).
                        setNegativeBtnText(R.string.no).
                        setPositiveBtnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                BusProvider.getBus().post(new PositiveButtonClickedUIEvent().setData(FriendRequestAnswer.ACCEPT));
                            }
                        }).
                        build().show(fragmentManager, null);
                break;
            case IsFriend:
                EditFriendShipDialog.newInstance(member).show(fragmentManager, null);
                break;
            case Pending:
                Logger.toast(R.string.info_already_requested);
                break;
        }
    }

    public static void launchNewPost(android.support.v4.app.FragmentManager fragmentManager, SuggestedWebPagePost webpagePost) {
        NewPostDialog.newInstance(webpagePost).show(fragmentManager, null);
    }


    public static void launchRepostDialog(android.support.v4.app.FragmentManager fragmentManager, Post post) {
        RepostDialog.newInstance(post).show(fragmentManager, null);
    }


    public static void launchSelectImage(android.support.v4.app.FragmentManager fragmentManager, Member member, View.OnClickListener onItemClickListener){
        SelectImageDialog.newInstance(member, onItemClickListener).show(fragmentManager, null);
    }

    public static void launchReportDialog(android.support.v4.app.FragmentManager fragmentManager, int title, int memberIdOrPostId){
        ReportDialog.newInstance(title, memberIdOrPostId).show(fragmentManager, null);
    }

    public static void launchInputVerificationCodeDialog(android.support.v4.app.FragmentManager fragmentManager, String mobileOrEmail, boolean byEmail){
        InputVerificationCodeDialog.newInstance(mobileOrEmail, byEmail).show(fragmentManager, null);
    }

    public static void launchInputVerificationCodeDialog(android.support.v4.app.FragmentManager fragmentManager, String mobileOrEmail, boolean byEmail, boolean disableResend){
        InputVerificationCodeDialog.newInstance(mobileOrEmail, byEmail, disableResend).show(fragmentManager, null);
    }

    public static void launchRequestVerificationDialog(android.support.v4.app.FragmentManager fragmentManager){
        VerificationRequestCodeDialog.newInstance().show(fragmentManager, null);
    }

    public static void launchRequestVerificationDialog(android.support.v4.app.FragmentManager fragmentManager, int titleRes, String inputValue, boolean mVerificationByEmail){
        VerificationRequestCodeDialog.newInstance(titleRes, inputValue, mVerificationByEmail).show(fragmentManager, null);
    }

    public static void launchMessageDialog(android.support.v4.app.FragmentManager fragmentManager, int titleRes, int messageRes){
        MessageDialog.newInstance(titleRes, messageRes).show(fragmentManager, null);
    }

    public static void launchSetUsernamePasswordDialog(android.support.v4.app.FragmentManager fragmentManager){
        SetUserNamePasswordDialog.newInstance().show(fragmentManager, null);
    }

}
