package com.shaya.poinila.android.presentation.uievent;

/**
 * Created by iran on 2015-07-26.
 */
public class RemoveItemUIEvent {
    public RemoveItemUIEvent(int adapterPosition) {
        this.adapterPosition = adapterPosition;
    }

    public int adapterPosition;
}
