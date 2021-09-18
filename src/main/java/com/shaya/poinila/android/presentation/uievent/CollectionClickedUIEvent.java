package com.shaya.poinila.android.presentation.uievent;

import data.event.BaseEvent;
import data.event.IdentifiableEvent;

/**
 * Created by iran on 2015-08-06.
 */
public class CollectionClickedUIEvent extends IdentifiableEvent {
    public int adapterPosition;

    public CollectionClickedUIEvent(int adapterPosition, BaseEvent.ReceiverName receiverTag) {
        super(receiverTag);
        this.adapterPosition = adapterPosition;
    }
}
