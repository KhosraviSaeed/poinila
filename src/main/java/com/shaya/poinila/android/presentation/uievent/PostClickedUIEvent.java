package com.shaya.poinila.android.presentation.uievent;


import data.event.BaseEvent;
import data.event.IdentifiableEvent;

/**
 * Created by iran on 2015-07-23.
 */
public class PostClickedUIEvent extends IdentifiableEvent {
    public int adapterPosition;

    public PostClickedUIEvent(int adapterPosition, BaseEvent.ReceiverName receiverTag) {
        super(receiverTag);
        this.adapterPosition = adapterPosition;
    }
}
