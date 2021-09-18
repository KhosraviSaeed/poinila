package com.shaya.poinila.android.presentation.uievent;

import data.event.BaseEvent;
import data.event.IdentifiableEvent;

/**
 * Created by AlirezaF on 7/16/2015.
 */
public class MemberClickedUIEvent extends IdentifiableEvent {
    public int adapterPosition;
    public MemberClickedUIEvent(int adapterPosition, BaseEvent.ReceiverName receiverTag) {
        super(receiverTag);
        this.adapterPosition = adapterPosition;
    }
}
