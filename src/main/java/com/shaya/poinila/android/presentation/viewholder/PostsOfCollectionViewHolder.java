package com.shaya.poinila.android.presentation.viewholder;

import android.view.View;
import com.shaya.poinila.android.presentation.R;

import butterknife.ButterKnife;
import data.event.BaseEvent;

/**
 * Created by iran on 2015-08-10.
 */
public class PostsOfCollectionViewHolder extends PostViewHolder{

    public PostsOfCollectionViewHolder(View view, BaseEvent.ReceiverName receiverTag) {
        super(view, receiverTag);

        avatar = ButterKnife.findById(postAuthor, R.id.image);
        createdByTextView = ButterKnife.findById(postAuthor, R.id.title);
        authorName = ButterKnife.findById(postAuthor, R.id.subtitle);
    }
}
