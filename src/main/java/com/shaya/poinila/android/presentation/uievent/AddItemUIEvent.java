package com.shaya.poinila.android.presentation.uievent;

/**
 * Created by iran on 2015-07-26.
 */
public class AddItemUIEvent {
    public AddItemUIEvent(int adapterPosition) {
        this.adapterPosition = adapterPosition;
    }

    public int adapterPosition;
}
