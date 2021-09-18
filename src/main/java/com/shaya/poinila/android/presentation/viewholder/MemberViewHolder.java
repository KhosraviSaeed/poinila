package com.shaya.poinila.android.presentation.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.FriendshipClickEvent;
import com.shaya.poinila.android.presentation.uievent.MemberClickedUIEvent;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.ResourceUtils;

import butterknife.Bind;
import butterknife.OnClick;
import data.event.BaseEvent;
import data.model.ImageUrls;
import data.model.Member;
import manager.DataRepository;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setFont;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setImage;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;

/**
 * Created by iran on 2015-07-07.
 */
public class MemberViewHolder extends BaseViewHolder<Member>{

    BaseEvent.ReceiverName receiverTag;

    public MemberViewHolder(View view, final BaseEvent.ReceiverName receiverTag) {
        super(view);
        this.receiverTag = receiverTag;

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getBus().post(new MemberClickedUIEvent(getAdapterPosition(), receiverTag));
            }
        });
    }

    @Bind(R.id.image)
    ImageView avatar;

    @Bind(R.id.title)
    TextView name;

    @Bind(R.id.subtitle)
    TextView subtitle;

    @Bind(R.id.icon)
    ImageView icon;

    @Bind(R.id.icon_caption)
    TextView iconCaption;

    public void fill(Member member){
        setImage(avatar, member.imageUrls, ImageUrls.ImageType.MEMBER, ImageUrls.ImageSize.AVATAR);
        setText(name, member.fullName);

        setFont(name, ResourceUtils.getString(R.string.default_bold_font_path));

        String subtitleText = (member.url == null) ? member.uniqueName : member.url;
        setText(subtitle, subtitleText);

        setFont(subtitle, ResourceUtils.getString(R.string.default_font_path));

        if (DataRepository.getInstance().isMe(member.id)) {
            icon.setVisibility(View.GONE);
            return;
        }
        else icon.setVisibility(View.VISIBLE);

        //icon.setBackgroundResource(0); // to clear old background
        if (DataRepository.getInstance().isMe(member.id)) {
            // TODO: this is terrible! must use the profile fragment instead.
            icon.setVisibility(View.GONE);
        } else if (member.friendshipStatus == null){
            icon.setVisibility(View.INVISIBLE);
        }
        else{
            icon.setVisibility(View.VISIBLE);
            switch (member.friendshipStatus) {
                case NotFriend:
                    icon.setImageResource(R.drawable.add_friend);
                    break;
                case WaitingForAction:
                    //TODO  this is temporary drawable. must replace with new drawable
                    icon.setImageResource(R.drawable.add_friend);
                    break;
                case IsFriend:
                    icon.setImageResource(R.drawable.friends);
                    break;
                case Pending:
                    icon.setImageResource(R.drawable.pending_friendship_request);
                    break;
            }
        }
    }

   /* @OnClick(R.actorID.image) public void onAvatarClick(){
        BusProvider.getBus().post(new MemberClickedUIEvent(getAdapterPosition()));
    }*/

    @OnClick(R.id.icon) public void onFriendshipClick(){
        BusProvider.getBus().post(new FriendshipClickEvent(getAdapterPosition()));
    }
}
