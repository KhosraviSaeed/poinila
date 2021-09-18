package com.shaya.poinila.android.presentation.uievent;

/**
 * Created by iran on 2015-08-29.
 */
public class CirclesSelectedUIEvent {
    public final boolean[] selectedCircles;
    //public final int actorID;

    public CirclesSelectedUIEvent(boolean[] selectedCircles, int actorID) {

        this.selectedCircles = selectedCircles;
        //this.actorID = actorID;
    }
}
