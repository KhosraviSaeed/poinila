package com.shaya.poinila.android.presentation.viewholder;

import android.view.View;
import com.shaya.poinila.android.presentation.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import data.event.BaseEvent;

/**
 * Created by iran on 2015-08-16.
 */
public class RemovablePostViewHolder extends PostViewHolder{

    public RemovablePostViewHolder(View view, BaseEvent.ReceiverName receiverTag) {
        super(view, receiverTag);

        collectionImage = ButterKnife.findById(postCollection, R.id.image);
        collectionStatusView = ButterKnife.findById(postCollection, R.id.title);
        collectionName = ButterKnife.findById(postCollection, R.id.subtitle);
    }
}
