package com.shaya.poinila.android.presentation.uievent;


import data.model.SuggestedWebPagePost;

public class NewWebsitePostEvent {
    public SuggestedWebPagePost suggestedPost;

    public NewWebsitePostEvent(SuggestedWebPagePost suggestedPost) {
        this.suggestedPost = suggestedPost;
    }
}
