package com.shaya.poinila.android.presentation.viewholder;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.NotifActorClickedUIEvent;
import com.shaya.poinila.android.util.BusProvider;

import butterknife.Bind;
import data.model.Notification;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setNotificationImages;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setNotificationTitle;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;
import static com.shaya.poinila.android.util.ResourceUtils.getString;
import static com.shaya.poinila.android.util.ResourceUtils.getStringFormatted;

/**
 * Created by iran on 2015-08-15.
 */
public abstract class NotificationViewHolder extends BaseViewHolder<Notification> {
    protected @Bind(R.id.image) ImageView image;
    protected @Bind(R.id.title) TextView title;
    protected @Bind(R.id.subtitle) TextView subtitle;
    protected @Bind(R.id.notif_image_container) ViewGroup imageContainer;

    public NotificationViewHolder(View itemView) {
        super(itemView);
    }

    public void fill(final Notification notification){
        setNotificationTitle(title, notification);
        setText(subtitle, getSubtitleText(notification));
        setNotificationImages(imageContainer, notification.participants, notification.getParticipantImageType());

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getBus().post(new NotifActorClickedUIEvent(getAdapterPosition()));
            }
        });

        if (!notification.seen)
            rootView.setBackgroundResource(R.color.wild_sand);
    }

    protected String getSubtitleText(Notification notification){
        switch (notification.type){
            case MY_POST_LIKED:
                return (notification.participants.size() == 1) ?
                        getString(R.string.notif_my_post_liked_singular) :
                        getString(R.string.notif_my_post_liked_plural);

            case MY_POST_REPOSTED:
                return (notification.participants.size() == 1) ?
                        getString(R.string.notif_my_post_reposted_singular) :
                        getString(R.string.notif_my_post_reposted_plural);

            case COMMENT_AFTER_YOUR_COMMENT:
                return (notification.participants.size() == 1) ?
                        getString(R.string.notif_comment_after_you_singular) :
                        getString(R.string.notif_comment_after_you_plural);

            case COMMENT_MY_POST:
                return (notification.participants.size() == 1) ?
                        getString(R.string.notif_comment_my_post_singular) :
                        getString(R.string.notif_comment_my_post_plural);

            case FRIENDS_LIKED_POSTS:
                /*return (notification.participants.size() == 1) ?
                        getString(R.string.notif_friend_liked_posts_singular) :
                        getStringFormatted(R.string.notif_friend_liked_posts_plural);*/
                return getStringFormatted(R.string.notif_friend_liked_posts_singular, notification.participants.size());
                        /*getStringFormatted(R.string.notif_friend_liked_posts_singular, notification.mainActor.title):
                        getStringFormatted(R.string.notif_friend_liked_posts_plural, notification.mainActor.title);*/


            case FRIENDS_FOLLOWED_COLLECTIONS:
                return getStringFormatted(R.string.notif_friend_followed_collections_singular, notification.participants.size());
              /*  return (notification.participants.size() == 1) ?
                        getStringFormatted(R.string.notif_friend_followed_collections_singular, notification.participants.size()):
                        getStringFormatted(R.string.notif_friend_followed_collections_plural, notification.participants.size());*/

            case MY_COLLECTION_FOLLOWED:
                return (notification.participants.size() == 1) ?
                        getString(R.string.notif_my_collection_followed_singular) :
                        getString(R.string.notif_my_collection_followed_plural);

            case FRIENDS_CREATED_COLLECTIONS:
                return getStringFormatted(R.string.notif_friend_created_collections_singular, notification.participants.size());
                /*return (notification.participants.size() == 1) ?
                        getStringFormatted(R.string.notif_friend_created_collections_singular, notification.mainActor.uniqueName):
                        getStringFormatted(R.string.notif_friend_created_collections_plural,
                                notification.participants.size(), notification.mainActor.uniqueName);*/
            case FRIENDSHIP_ACCEPTED:
                return (notification.participants.size() == 1) ?
                        getString(R.string.notif_friendship_accepted_singular):
                        getString(R.string.notif_friendship_accepted_plural);
            default:
                return null;
        }
    }
}
