package com.shaya.poinila.android.presentation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.annotation.NotNull;
import com.shaya.poinila.android.presentation.view.activity.ChromeActivity;
import com.shaya.poinila.android.presentation.view.activity.CollectionListActivity;
import com.shaya.poinila.android.presentation.view.activity.CommentsListActivity;
import com.shaya.poinila.android.presentation.view.activity.FullImageActivity;
import com.shaya.poinila.android.presentation.view.activity.HelpActivity;
import com.shaya.poinila.android.presentation.view.activity.MainActivity;
import com.shaya.poinila.android.presentation.view.activity.MemberListActivity;
import com.shaya.poinila.android.presentation.view.activity.NewPostActivity;
import com.shaya.poinila.android.presentation.view.activity.NotificationOpenedActivity;
import com.shaya.poinila.android.presentation.view.activity.OthersProfileActivity;
import com.shaya.poinila.android.presentation.view.activity.PostListActivity;
import com.shaya.poinila.android.presentation.view.activity.SelectInterestActivity;
import com.shaya.poinila.android.presentation.view.activity.SignUpLoginActivity;
import com.shaya.poinila.android.presentation.view.activity.WebviewActivity;
import com.shaya.poinila.android.presentation.view.dialog.NewPostDialog;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.util.NavigationUtils;
import com.shaya.poinila.android.utils.PushNotificationUtils;
import com.shaya.poinila.android.utils.Utils;

import data.model.Collection;
import data.model.Member;
import data.model.Post;
import data.model.SuggestedWebPagePost;
import manager.DataRepository;

import static com.shaya.poinila.android.util.ConstantsUtils.KEY_CONTENT_URI;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_ENTITY;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_FIRST_LOGIN;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_ITEM_COUNT;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_MEMBER_ID;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_PAGE_TITLE_PARAMETER;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_POST_ID;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_REQUEST_ID;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_SECOND_ENTITY_ID;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_STARTED_FROM_SETTING;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_WEBSITE_URL;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_COLLECTION_POSTS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_EXPLORE;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_MEMBER_POSTS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_POST_LIKERS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_POST_RELATED_POSTS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_POST_REPOSTING_COLLECTIONS;

/**
 * Created by iran on 2015-09-27.
 */
public class PageChanger {
    public static void goToProfile(Activity activity, Member member) {
        DataRepository.getInstance().putTempModel(member);
        NavigationUtils.goToActivity(OthersProfileActivity.class,
                activity, KEY_MEMBER_ID, member.getId());
    }

    public static void goToProfile(Activity activity, String memberID) {
        DataRepository.getInstance().putTempModel(null);
        NavigationUtils.goToActivity(OthersProfileActivity.class,
                activity, KEY_MEMBER_ID, memberID);
    }

    public static void goToCollection(Activity activity, Collection collection) {
        DataRepository.getInstance().putTempModel(collection);
        goToCollection(activity, collection.getId(), collection.name, null);
    }

    public static void goToCollection(Activity activity, @Nullable String collectionId,
                                      @NotNull String collectionName, @Nullable String userName){
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ENTITY, collectionId != null ? collectionId : collectionName);
        bundle.putInt(KEY_REQUEST_ID, REQUEST_COLLECTION_POSTS);
        bundle.putString(KEY_SECOND_ENTITY_ID, userName != null ? userName : collectionName);
        NavigationUtils.goToActivity(PostListActivity.class, activity, bundle);
    }

    /*public static void goToCollectionByName(Activity activity, String collectionName, String userName){
        Bundle bundle = new Bundle();
        bundle.putString(KEY_Entity, collectionName);
        bundle.putInt(KEY_REQUEST_ID, REQUEST_COLLECTION_POSTS);
        bundle.putString(KEY_PAGE_TITLE_PARAMETER, collectionName);
        bundle.putString(KEY_SECOND_ENTITY_ID, userName);
        NavigationUtils.goToActivity(PostListActivity.class, activity, bundle);
    }*/

    public static void goToPost(Activity activity, Post post) {
        DataRepository.getInstance().putTempModel(post);
        goToPost(activity, post.getId());
    }

    public static void goToPost(Activity activity, String postID) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ENTITY, postID);
        bundle.putInt(KEY_REQUEST_ID, REQUEST_POST_RELATED_POSTS);
        NavigationUtils.goToActivity(PostListActivity.class, activity, bundle);

       /* BaseFragment fragment = PostListFragment.newInstance(postID, REQUEST_POST_RELATED_POSTS);
        if (activity instanceof FragmentHostActivity)
            ((FragmentHostActivity) activity).addFragment(fragment, true);
        else {
            NavigationUtils.goToActivity(FragmentHostActivity.class, activity);
            DataRepository.getInstance().putTempModel(fragment);
        }*/
    }

    public static void goToExplore(Activity activity, String tagText) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ENTITY, tagText);
        bundle.putInt(KEY_REQUEST_ID, REQUEST_EXPLORE);
        NavigationUtils.goToActivity(PostListActivity.class, activity, bundle);
    }

    public static void goToLikersList(Activity activity, int faveCount, String postID) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ENTITY, postID);
        bundle.putInt(KEY_REQUEST_ID, REQUEST_POST_LIKERS);
        bundle.putInt(KEY_ITEM_COUNT, faveCount);
        NavigationUtils.goToActivity(MemberListActivity.class, activity, bundle);
    }

    public static void goToCommentList(Activity activity, int commentCount, String postID) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_POST_ID, postID);
        bundle.putInt(KEY_ITEM_COUNT, commentCount);
        NavigationUtils.goToActivity(CommentsListActivity.class, activity, bundle);
    }

    public static void goToRepostList(Activity activity, int repostCount, String postID) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ENTITY, postID);
        bundle.putInt(KEY_REQUEST_ID, REQUEST_POST_REPOSTING_COLLECTIONS);
        bundle.putInt(KEY_ITEM_COUNT, repostCount);
        NavigationUtils.goToActivity(CollectionListActivity.class, activity, bundle);
    }

    public static void gotToNotificationActivity(PushNotificationUtils.NOTIFICATION_TYPE type, String data) {
        Bundle bundle = new Bundle();

        bundle.putString("type", type.toString());
        if(!TextUtils.isEmpty(data))
            bundle.putString("data", data);

        Logger.log("push data = " + data, Logger.LEVEL_INFO);

        NavigationUtils.goToActivity(NotificationOpenedActivity.class, PoinilaApplication.getAppContext(), bundle, Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static void goToMemberPosts(Activity activity, String memberID, String memberName) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ENTITY, memberID);
        bundle.putInt(KEY_REQUEST_ID, REQUEST_MEMBER_POSTS);
        bundle.putString(KEY_SECOND_ENTITY_ID, memberName);
        NavigationUtils.goToActivity(PostListActivity.class, activity, bundle);
    }

    public static void goToHelpActivity(Activity activity, boolean startedFromSetting) {
        Intent intent = NavigationUtils.makeNavigationIntent(HelpActivity.class, activity);
        intent.putExtra(KEY_STARTED_FROM_SETTING, startedFromSetting);
        if (!startedFromSetting)
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        activity.startActivity(intent);
    }

    public static void goToLoginActivity(Activity activity) {
        goToLoginActivity(activity, null);
    }

    public static void goToLoginActivity(Activity activity, Uri shareUri) {
        Intent intent = NavigationUtils.makeNavigationIntent(SignUpLoginActivity.class, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (shareUri != null)
            intent.setData(shareUri);
        activity.finish();
        activity.startActivity(intent);
    }

    public static void goToInlineBrowser(Activity activity, String url, @NotNull String postId, String pageTitle) {
        Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty(pageTitle))
            bundle.putString(KEY_PAGE_TITLE_PARAMETER, pageTitle);

        if(!TextUtils.isEmpty(postId))
            bundle.putString(KEY_ENTITY, postId);

        bundle.putString(KEY_WEBSITE_URL, url);
        if(Utils.getBrowserAvailablePackageName() == null){
            NavigationUtils.goToActivity(WebviewActivity.class, activity, bundle);
        }else {
            if(!TextUtils.isEmpty(postId))
                bundle.putString("referrer", getPostUrl(postId));
            NavigationUtils.goToActivity(ChromeActivity.class, activity, bundle);
        }
    }

    public static String getPostUrl(String postId){
        return ConstantsUtils.POINILA_SERVER_ADDRESS.concat("post/" + postId + "/");
    }

    //TODO problem in showing full screen dialogs
    public static void goToNewPost(android.support.v4.app.FragmentManager fragmentManager, SuggestedWebPagePost webpagePost) {
        /*Bundle bundle = new Bundle();
        bundle.putParcelable(ConstantsUtils.KEY_WEBPAGE_POST, webpagePost == null ? null : Parcels.wrap(webpagePost));
        NavigationUtils.goToActivity(NewPostActivity.class, activity, bundle);*/
        NewPostDialog.newInstance(webpagePost).show(fragmentManager, null);
    }

    //TODO problem in showing full screen dialogs
    public static void goToNewWebSitePost(Context context, SuggestedWebPagePost webpagePost) {
        Bundle bundle = new Bundle();
        NavigationUtils.goToActivity(NewPostActivity.class, context, bundle);
    }

    public static void goToSelectInterest(Activity activity, boolean firstLogin) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_FIRST_LOGIN, firstLogin);
        NavigationUtils.goToActivity(SelectInterestActivity.class, activity, bundle);
    }

    public static void goToSelectInterest(Context context, boolean firstLogin) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_FIRST_LOGIN, firstLogin);
        NavigationUtils.goToActivity(SelectInterestActivity.class, context, bundle);
    }

    public static void goToDashboard(Activity activity) {
        Intent intent = NavigationUtils.makeNavigationIntent(MainActivity.class, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void goToDashboard(Context context) {
        Intent intent = NavigationUtils.makeNavigationIntent(MainActivity.class, context);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void goToFullImage(Activity activity, String imageAddress) {
        Intent intent = NavigationUtils.makeNavigationIntent(FullImageActivity.class, activity);
        intent.putExtra(KEY_CONTENT_URI, imageAddress);
        activity.startActivity(intent);
    }

}
