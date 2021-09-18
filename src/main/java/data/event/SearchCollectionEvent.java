package data.event;
import java.util.List;

import data.model.Collection;

/**
 * Created by iran on 2015-07-07.
 */
public class SearchCollectionEvent {
    private final int statusCode;
    List<Collection> data;
    public SearchCollectionEvent(List<Collection> data) {
        this.data = data;
        statusCode=200;
    }

    public SearchCollectionEvent(int statusCode) {
        this.statusCode = statusCode;
    }

    public List<Collection> getData(){
        return data;
    }

    public int getStatusCode(){
        return statusCode;
    }
}
