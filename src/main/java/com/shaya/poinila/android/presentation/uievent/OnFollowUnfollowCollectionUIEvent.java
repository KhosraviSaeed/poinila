package com.shaya.poinila.android.presentation.uievent;

/**
 * Created by iran on 2015-08-06.
 */
public class OnFollowUnfollowCollectionUIEvent {
    public int adapterPosition;
    public boolean follow;

    public OnFollowUnfollowCollectionUIEvent(int adapterPosition) {

        this.adapterPosition = adapterPosition;
    }

    public OnFollowUnfollowCollectionUIEvent(int adapterPosition, boolean follow) {

        this.adapterPosition = adapterPosition;
        this.follow = follow;
    }
}
