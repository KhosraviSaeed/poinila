package com.shaya.poinila.android.presentation.viewholder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.MemberCircleToggledEvent;
import com.shaya.poinila.android.util.BusProvider;

import butterknife.Bind;
import butterknife.OnClick;
import data.model.ImageUrls;
import data.model.Member;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setImage;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;

/**
 * Created by iran on 2015-07-26.
 */
public class CircleMemberViewHolder extends BaseViewHolder<Member>{
    @Bind(R.id.image) public ImageView avatarImage;
    @Bind(R.id.title) public TextView nameView;
    @Bind(R.id.subtitle) public TextView subTitleView;
    @Bind(R.id.icon) public ImageButton addRemoveIcon;
    @Bind(R.id.icon_caption) public TextView iconCaptionView;

    public CircleMemberViewHolder(View view) {
        super(view);
        iconCaptionView.setVisibility(View.GONE);
    }

    @OnClick(R.id.icon) public void onAddOrRemove(){
        BusProvider.getBus().post(new MemberCircleToggledEvent(getAdapterPosition()));
    }

    public void fill(Member member) {
        setImage(avatarImage, member.imageUrls, ImageUrls.ImageType.MEMBER, ImageUrls.ImageSize.AVATAR);
        setText(nameView, member.fullName);
        setText(subTitleView, member.uniqueName);//(member.aboutMe);
        addRemoveIcon.setBackgroundResource(R.drawable.add_remove_checkbox_selector);
        addRemoveIcon.setSelected(member.selected);
    }

}
