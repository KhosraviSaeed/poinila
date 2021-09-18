package com.shaya.poinila.android.presentation.uievent.sync;

import data.model.Post;

/**
 * Created by iran on 7/11/2016.
 */
public class PostActionSyncEvent extends BaseSyncEvent {
    public Post post;

    public PostActionSyncEvent(Post post){
        this.post = post;
    }
}
