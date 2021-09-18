package data.event;


import data.RequestType;

/**
 * Created by iran on 2015-09-05.
 */
public class FailEvent {
    public RequestType requestType;

    public FailEvent(RequestType requestType) {
        this.requestType = requestType;
    }
}
