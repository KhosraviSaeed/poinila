package data.event;

/**
 * Created by AlirezaF on 7/22/2015.
 */
public class PostReceivedEvent extends data.event.BaseEvent {
    public int requestId;
    public data.model.Post post;

    public PostReceivedEvent(data.model.Post post) {
        this.post = post;
    }

    public PostReceivedEvent(data.model.Post post, int requestId) {
        this(post);
        this.requestId = requestId;
    }
}
