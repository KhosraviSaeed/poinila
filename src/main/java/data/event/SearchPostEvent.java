package data.event;

import java.util.List;

/**
 * Created by iran on 2015-07-07.
 */
public class SearchPostEvent {
    private int statusCode;
    List<data.model.Post> data;
    public SearchPostEvent(List<data.model.Post> posts) {
        this.data = posts;
        statusCode=200;
    }

    public SearchPostEvent(int statusCode) {
        this.statusCode = statusCode;
    }

    public List<data.model.Post> getData(){
        return data;
    }

    public int getStatusCode(){
        return statusCode;
    }
}
