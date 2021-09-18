package com.shaya.poinila.android.presentation.uievent;

import data.model.FriendRequestAnswer;

/**
 * Created by iran on 2015-08-15.
 */
public class AnswerFriendshipUIEvent {
    public int adapterPosition;
    public FriendRequestAnswer accept;

    public AnswerFriendshipUIEvent(int adapterPosition, FriendRequestAnswer answer) {
        this.adapterPosition = adapterPosition;
        this.accept = answer;
    }
}
