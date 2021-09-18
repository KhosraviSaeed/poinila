package data.event;

import java.util.List;

import data.model.Post;

/**
 * Created by iran on 2015-07-27.
 */
public class MemberPostsEvent {
    public List<Post> posts;

    public MemberPostsEvent(List<Post> posts) {
        this.posts = posts;
    }
}
