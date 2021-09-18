package com.shaya.poinila.android.presentation.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.AnswerFriendshipUIEvent;
import com.shaya.poinila.android.presentation.uievent.NotifActorClickedUIEvent;
import com.shaya.poinila.android.util.BusProvider;

import butterknife.Bind;
import butterknife.OnClick;
import data.model.FriendRequestAnswer;
import data.model.ImageUrls;
import data.model.InvitationNotif;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setImage;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;
import static com.shaya.poinila.android.util.ResourceUtils.getString;

/**
 * Created by iran on 2015-08-15.
 */
public class InviteNotifViewHolder extends BaseViewHolder<InvitationNotif> {

    @Bind(R.id.image) ImageView image;
    @Bind(R.id.title) TextView title;
    @Bind(R.id.subtitle) TextView subtitle;
    @Bind(R.id.agree) TextView agreeButton;
    @Bind(R.id.ignore) TextView ignoreButton;

    //private int adapterPosition = -1;

    public InviteNotifViewHolder(View inflatedView) {
        super(inflatedView);
    }

/*
     used in showing summary of friendship request in notification page when we doesn't create
     ViewHolder through adapter so haven't adapter position consequently.
     @param adapterPosition
     */

/*    public InviteNotifViewHolder(View inflatedView, int adapterPosition){
        this(inflatedView);
        this.adapterPosition = adapterPosition;
    }*/

    @OnClick(R.id.image) public void onAvatarClicked(){
        BusProvider.getBus().post(new NotifActorClickedUIEvent(getAdapterPosition()));
    }

    @OnClick({R.id.ignore, R.id.agree})  public void onIgnore(View view){
        //if (adapterPosition == -1) adapterPosition = getAdapterPosition();
        if (view.getId() == R.id.agree)
            BusProvider.getBus().post(new AnswerFriendshipUIEvent(getAdapterPosition(), FriendRequestAnswer.ACCEPT));
        else
            BusProvider.getBus().post(new AnswerFriendshipUIEvent(getAdapterPosition(), FriendRequestAnswer.REJECT));
    }

    public void fill(InvitationNotif invitationNotif){
        setImage(image, invitationNotif.member.imageUrls, ImageUrls.ImageType.MEMBER, ImageUrls.ImageSize.AVATAR);
        setText(title, invitationNotif.member.fullName);
        setText(subtitle, getString(R.string.requested_to_be_your_friend));
    }

}
