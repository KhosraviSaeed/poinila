package com.shaya.poinila.android.presentation.uievent;

import android.view.View;

/**
 * Created by AlirezaF on 7/22/2015.
 */
public class RemoveTagEvent {
    public View tagView;
    public int adapterPosition;

    public RemoveTagEvent(View tagView) {
        this.tagView = tagView;
    }

    public RemoveTagEvent(int adapterPosition) {

        this.adapterPosition = adapterPosition;
    }
}
