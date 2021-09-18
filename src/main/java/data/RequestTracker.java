package data;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by iran on 2015-11-15.
 */
public class RequestTracker {
    public Set<Integer> initRequestsIDs;
    public Set<Integer> loadMoreRequestIDs;

    public RequestTracker() {
        initRequestsIDs = new HashSet<>();
        loadMoreRequestIDs = new HashSet<>();
    }

    public void addInitRequestID(int requestID){
        initRequestsIDs.add(requestID);
    }

    public boolean hasInitResponseValidID(int requestID){
        return initRequestsIDs.remove(requestID);
    }

    public void addLoadMoreRequestID(int requestID){
        loadMoreRequestIDs.add(requestID);
    }

    public boolean hasLoadMoreResponseValidID(int requestID){
        return loadMoreRequestIDs.remove(requestID);
    }
}
