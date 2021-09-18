package com.shaya.poinila.android.presentation.uievent;


import data.event.BaseEvent;
import data.event.IdentifiableEvent;

/**
 * Created by iran on 11/22/2015.
 */
public class FABMenuExpandUIEvent extends IdentifiableEvent {
    public FABMenuExpandUIEvent(BaseEvent.ReceiverName receiverName) {
        super(receiverName);
    }
}
