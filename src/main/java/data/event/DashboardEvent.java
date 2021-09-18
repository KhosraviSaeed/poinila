package data.event;
import java.util.List;

/**
 * Created by iran on 2015-07-06.
 */
public class DashboardEvent extends data.event.BaseEvent {
    public String statusCode;
    public List<data.model.Post> data;
    public String bookmark;
    public boolean isFromCache;

    public DashboardEvent(List<data.model.Post> posts, boolean isFromCache) {
        this.data = posts;
        this.isFromCache = isFromCache;
    }

    public DashboardEvent(String statusCode) {
        this.statusCode = statusCode;
    }

    public DashboardEvent(List<data.model.Post> data, boolean isFromCache, String bookmark) {

        this.data = data;
        this.isFromCache = isFromCache;
        this.bookmark = bookmark;
    }

    public List<data.model.Post> getData(){
        return data;
    }

    public String getMessage(){
        return statusCode;
    }
}
