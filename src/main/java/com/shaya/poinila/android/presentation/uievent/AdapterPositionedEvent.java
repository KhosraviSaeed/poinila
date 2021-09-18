package com.shaya.poinila.android.presentation.uievent;

/**
 * Created by iran on 2015-11-08.
 */
public abstract class AdapterPositionedEvent {
    public AdapterPositionedEvent(int adapterPosition) {
        this.adapterPosition = adapterPosition;
    }

    public int adapterPosition;
}
