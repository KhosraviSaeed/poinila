package com.shaya.poinila.android.presentation.viewholder;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.CommentLongClickUIEvent;
import com.shaya.poinila.android.presentation.uievent.MemberClickedUIEvent;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.TimeUtils;

import butterknife.Bind;
import butterknife.OnClick;
import data.model.Comment;
import data.model.ImageUrls;
import manager.DataRepository;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setImage;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;

/**
 * Created by iran on 2015-06-25.
 */
public class CommentViewHolder extends BaseViewHolder<Comment>{
    public CommentViewHolder(View view) {
        super(view);
    }

    @Bind(R.id.image) ImageView image;

    @Bind(R.id.title) TextView title;

    @Bind(R.id.subtitle) TextView subtitle;

    @Bind(R.id.date_created)  TextView dateCreated;

    public void fill(Comment comment) {
        itemView.setLongClickable(comment.deletable);
        if (itemView.isLongClickable())
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    BusProvider.getBus().post(new CommentLongClickUIEvent(getAdapterPosition()));
                    return true;
                }
            });
        else itemView.setOnLongClickListener(null);
        setImage(image, comment.commenter.imageUrls, ImageUrls.ImageType.MEMBER, ImageUrls.ImageSize.AVATAR);
        setText(title, comment.commenter.fullName);
        setText(subtitle, comment.content);
        setText(dateCreated, TimeUtils.getTimeString(comment.creationDate, DataRepository.getInstance().getServerTimeDifference()));
    }

    @OnClick(R.id.image) public void onImageClicked(){
        BusProvider.getBus().post(new MemberClickedUIEvent(getAdapterPosition(), null));
    }
}
