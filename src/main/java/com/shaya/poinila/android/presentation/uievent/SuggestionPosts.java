package com.shaya.poinila.android.presentation.uievent;

import java.util.List;

import data.event.BaseEvent;
import data.model.Post;

/**
 * Created by iran on 7/24/2016.
 */
public class SuggestionPosts extends BaseEvent {
    public List<Post> posts;

    public SuggestionPosts(List<Post> posts){
        this.posts = posts;
    }

}
