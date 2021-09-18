package data.event;

/**
 * Created by AlirezaF on 7/22/2015.
 */
public class ContentReceivedEvent {
    public String content;
    public int postID;

    public ContentReceivedEvent(String content, int postID) {
        this.content = content;
        this.postID = postID;
    }
}
