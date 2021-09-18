package data.event;

import java.util.List;

import data.model.Comment;

/**
 * Created by iran on 2015-07-27.
 */
public class CommentsReceivedEvent {
    public List<Comment> data;
    public String bookmark;

    public CommentsReceivedEvent(List<Comment> data, String bookmark) {
        this.data = data;
        this.bookmark = bookmark;
    }
}
