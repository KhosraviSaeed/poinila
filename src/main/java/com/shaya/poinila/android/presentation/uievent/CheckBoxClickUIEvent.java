package com.shaya.poinila.android.presentation.uievent;

/**
 * Created by iran on 2015-08-19.
 */
public class CheckBoxClickUIEvent {
    public boolean checked;
    public int adapterPosition;

    public CheckBoxClickUIEvent(boolean checked, int adapterPosition) {
        this.checked = checked;
        this.adapterPosition = adapterPosition;
    }
}
