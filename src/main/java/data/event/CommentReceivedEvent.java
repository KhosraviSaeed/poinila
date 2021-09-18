package data.event;

/**
 * Created by iran on 2015-09-20.
 */
public class CommentReceivedEvent {
    public String postID;
    public data.model.Comment comment;

    public CommentReceivedEvent(data.model.Comment comment, String postID) {
        this.comment = comment;
        this.postID = postID;
    }
}
