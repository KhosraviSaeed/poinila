package com.shaya.poinila.android.presentation.viewholder;

import android.view.View;

import com.shaya.poinila.android.presentation.uievent.NotifActorClickedUIEvent;
import com.shaya.poinila.android.util.BusProvider;

import data.model.ImageUrls;
import data.model.Notification;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setImage;

/**
 * Created by iran on 2015-08-15.
 */
public class PostNotifViewHolder extends NotificationViewHolder {

    public PostNotifViewHolder(View inflatedView) {
        super(inflatedView);
    }

    @Override
    public void fill(Notification notification) {
        super.fill(notification);
        setImage(image, notification.mainActor.imageUrls, ImageUrls.ImageType.POST, ImageUrls.ImageSize.AVATAR);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getBus().post(new NotifActorClickedUIEvent(getAdapterPosition()));
            }
        });

    }


}
