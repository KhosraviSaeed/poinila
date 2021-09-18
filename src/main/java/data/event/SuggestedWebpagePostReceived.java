package data.event;

import data.model.SuggestedWebPagePost;

/**
 * Created by AlirezaF on 10/15/2015.
 */
public class SuggestedWebpagePostReceived extends data.event.BaseEvent {
    public SuggestedWebPagePost webpagePost;

    public SuggestedWebpagePostReceived(SuggestedWebPagePost webpagePost) {

        this.webpagePost = webpagePost;
    }
}
