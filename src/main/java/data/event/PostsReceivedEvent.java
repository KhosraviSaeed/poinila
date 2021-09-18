package data.event;

import java.util.List;

import data.model.Post;

/**
 * Created by iran on 2015-07-27.
 */
public class PostsReceivedEvent extends IdentifiableEvent{
    public List<Post> posts;
    public String bookmark;
    public int requestId;

    public PostsReceivedEvent(List<Post> posts, String bookmark, ReceiverName receiverName, int requestId) {
        this(posts, bookmark, receiverName);
        this.requestId = requestId;
    }

    public PostsReceivedEvent(List<Post> posts, String bookmark, ReceiverName receiverName) {
        super(receiverName);
        this.posts = posts;
        this.bookmark = bookmark;
    }
}
