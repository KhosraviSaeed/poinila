package com.shaya.poinila.android.presentation.viewholder;

import android.util.Log;
import android.view.View;

import com.shaya.poinila.android.presentation.uievent.MemberClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.NotifActorClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.NotifParticipantClickedUIEvent;
import com.shaya.poinila.android.util.BusProvider;

import data.model.ImageUrls;
import data.model.Notification;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setImage;

/**
 * Created by iran on 2015-08-15.
 */
public class MemberNotifViewHolder extends NotificationViewHolder {

    public final ImageUrls.ImageType participantImageType;

    public MemberNotifViewHolder(View inflatedView, ImageUrls.ImageType participantImageType) {
        super(inflatedView);
        this.participantImageType = participantImageType;
    }

    public void fill( final Notification notification){
        super.fill(notification);
        if (notification.type != Notification.NotificationType.FRIENDSHIP_ACCEPTED) {
            setImage(image, notification.mainActor.imageUrls, ImageUrls.ImageType.MEMBER, ImageUrls.ImageSize.AVATAR);
        }

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getBus().post(
                        new NotifParticipantClickedUIEvent(notification.participants.get(0), notification.getParticipantImageType()));

            }
        });
    }
}
