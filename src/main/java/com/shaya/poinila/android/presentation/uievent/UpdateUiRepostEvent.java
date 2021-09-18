package com.shaya.poinila.android.presentation.uievent;

import data.event.BaseEvent;
import data.model.Post;

/**
 * Created by iran on 7/11/2016.
 */
public class UpdateUiRepostEvent extends BaseEvent {

    public boolean isSuccess = true;
    public int postId;

    public UpdateUiRepostEvent(int postId, boolean isSuccess){
        this.isSuccess = isSuccess;
        this.postId = postId;
    }
}
