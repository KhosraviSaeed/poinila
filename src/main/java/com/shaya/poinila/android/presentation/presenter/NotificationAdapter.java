package com.shaya.poinila.android.presentation.presenter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.viewholder.BaseViewHolder;
import com.shaya.poinila.android.presentation.viewholder.CollectionNotifViewHolder;
import com.shaya.poinila.android.presentation.viewholder.MemberNotifViewHolder;
import com.shaya.poinila.android.presentation.viewholder.PostNotifViewHolder;

import data.model.ImageUrls;
import data.model.Notification;

/**
 * Created by iran on 2015-08-15.
 */
public class NotificationAdapter extends RecyclerViewAdapter<Notification, BaseViewHolder<Notification>>{

    //public static final int DATE_HEADER = 0;
    public static final int INVITE_NOTIF = 1;
    public static final int FRIENDSHIP_ACCEPTED_NOTIF = 2;
    public static final int COMMENT_AFTER_YOUR_COMMENT = 3;
    public static final int COMMENT_MY_POST = 4;
    public static final int FRIENDS_CREATED_COLLECTIONS = 5;
    public static final int FRIENDS_FOLLOWED_COLLECTIONS = 6;
    public static final int FRIENDS_LIKED_POSTS = 7;
    public static final int MY_COLLECTION_FOLLOWED = 8;
    public static final int MY_POST_REPOSTED = 9;
    public static final int MY_POST_LIKED = 10;

    public NotificationAdapter(Context context){
        super(context, -1);
    }
    
    @Override
    public BaseViewHolder<Notification> onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            /*case DATE_HEADER:
                return new DateHeaderViewHolder(mLayoutInflater.inflate(R.layout.title_date_separator, parent, false));*/
            case MY_POST_LIKED:
            case COMMENT_AFTER_YOUR_COMMENT:
            case COMMENT_MY_POST:
            case MY_POST_REPOSTED:
                return new PostNotifViewHolder(mLayoutInflater.inflate(R.layout.notif_post, parent, false));
            case MY_COLLECTION_FOLLOWED:
                return new CollectionNotifViewHolder(mLayoutInflater.inflate(R.layout.notif_collection, parent, false));
            case FRIENDS_LIKED_POSTS:
                return new MemberNotifViewHolder(mLayoutInflater.inflate(R.layout.notif_member, parent, false), ImageUrls.ImageType.POST);
            case FRIENDS_CREATED_COLLECTIONS:
            case FRIENDS_FOLLOWED_COLLECTIONS:
                return new MemberNotifViewHolder(mLayoutInflater.inflate(R.layout.notif_member, parent, false), ImageUrls.ImageType.COLLECTION);
            case FRIENDSHIP_ACCEPTED_NOTIF:
                return new MemberNotifViewHolder(mLayoutInflater.inflate(R.layout.notif_member, parent, false), ImageUrls.ImageType.MEMBER);
            case VIEW_TYPE_LOAD_PROGRESS:
                return new BaseViewHolder.EmptyViewHolder(mLayoutInflater.inflate(R.layout.progress, parent, false));
        }
        return null;
    }

    @Override
    protected BaseViewHolder<Notification> getProperViewHolder(View v, int viewType) {
        return null;
    }

    @Override
    public int getItemViewType(int position) {
       /* else if (items.get(position) instanceof AcceptNotif){
            return FRIENDSHIP_ACCEPTED_NOTIF;
        }*/
       /* else if (items.get(position) instanceof DateHeader) {
            return DATE_HEADER;
        }*/

        int type = super.getItemViewType(position);
        if(type == VIEW_TYPE_LOAD_PROGRESS){
            return super.getItemViewType(position);
        }

        switch (getItem(position).type){
            case MY_POST_LIKED:
                return MY_POST_LIKED;
            case COMMENT_AFTER_YOUR_COMMENT:
                return COMMENT_AFTER_YOUR_COMMENT;
            case COMMENT_MY_POST:
                return COMMENT_MY_POST;
            case FRIENDS_CREATED_COLLECTIONS:
                return FRIENDS_CREATED_COLLECTIONS;
            case FRIENDS_FOLLOWED_COLLECTIONS:
                return FRIENDS_FOLLOWED_COLLECTIONS;
            case FRIENDS_LIKED_POSTS:
                return FRIENDS_LIKED_POSTS;
            case MY_COLLECTION_FOLLOWED:
                return MY_COLLECTION_FOLLOWED;
            case MY_POST_REPOSTED:
                return MY_POST_REPOSTED;
            case FRIENDSHIP_ACCEPTED:
                return FRIENDSHIP_ACCEPTED_NOTIF;
        }
        return -1;
    }
}
