package com.shaya.poinila.android.presentation.uievent;


import java.util.List;

import data.model.Member;

/**
 * Created by iran on 2015-10-11.
 */
public class FriendCirclesUpdated {
    public final List<Integer> selectedCirclesIDs;
    public final Member member;

    public FriendCirclesUpdated(List<Integer> selectedCirclesIDs, Member member) {
        this.selectedCirclesIDs = selectedCirclesIDs;
        this.member = member;
    }
}
