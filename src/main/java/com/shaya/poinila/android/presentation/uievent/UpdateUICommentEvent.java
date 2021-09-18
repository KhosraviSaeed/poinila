package com.shaya.poinila.android.presentation.uievent;

/**
 * Created by iran on 2015-09-30.
 */
public class UpdateUICommentEvent {
    public static final int INCREMENT_COMMENTS = 1;
    public static final int DECREMENT_COMMENTS = 2;
    public String postId;

    public int action;

    public UpdateUICommentEvent(int action, String postId) {
        this.action = action;
        this.postId = postId;
    }
}
