package com.shaya.poinila.android.presentation.view.activity;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.fragments.notification.NCollectionListFragment;
import com.shaya.poinila.android.presentation.view.fragments.notification.NPostListFragment;
import com.shaya.poinila.android.utils.PushNotificationUtils;

import java.lang.reflect.Type;
import java.util.List;

import data.model.Collection;
import data.model.Post;

/**
 * Created by iran on 6/14/2016.
 */
public class NotificationOpenedActivity extends FragmentHostActivity {

    @Override
    protected Fragment getHostedFragment() {

        PushNotificationUtils.NOTIFICATION_TYPE type = PushNotificationUtils.NOTIFICATION_TYPE.valueOf(getIntent().getStringExtra("type"));
        String data = getIntent().getStringExtra("data");
        Type listType;
        List list;

        switch (type){
            case POST_SUGGESTION:
                setTitle(R.string.notification_suggestion_post_title);
//                listType = new TypeToken<List<Post>>() {}.getType();
//                list = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create().fromJson(data, listType);
                return NPostListFragment.newInstance(data, type);
            case LIKE:
                setTitle(R.string.notification_post_like_title);
                listType = new TypeToken<List<Post>>() {}.getType();
                list = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create().fromJson(data, listType);
                return NPostListFragment.newInstance(list, type);
            case COMMENT:
                setTitle(R.string.notification_post_comment_title);
                listType = new TypeToken<List<Post>>() {}.getType();
                list = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create().fromJson(data, listType);
                return NPostListFragment.newInstance(list, type);
            case FOLLOW:
                setTitle(R.string.notification_collection_follow_title);
                listType = new TypeToken<List<Collection>>() {}.getType();
                list = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create().fromJson(data, listType);
                return NCollectionListFragment.newInstance(list);
            default:
                return new Fragment();
        }
    }

    private <T> List<T> getList(String data){
        Type listType = new TypeToken<List<T>>() {}.getType();
        List<T> list = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create().fromJson(data, listType);
        return list;
    }

}
