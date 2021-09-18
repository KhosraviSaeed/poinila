package com.shaya.poinila.android.presentation.viewholder.notification;

import android.view.View;

import com.shaya.poinila.android.presentation.viewholder.EditableCollectionViewHolder;

import data.event.BaseEvent;

/**
 * Created by iran on 6/22/2016.
 */
public class NEditableCollectionViewHolder extends EditableCollectionViewHolder {
    public NEditableCollectionViewHolder(View view, BaseEvent.ReceiverName receiverTag) {
        super(view, receiverTag);
        bottomTag.setVisibility(View.GONE);
    }
}
