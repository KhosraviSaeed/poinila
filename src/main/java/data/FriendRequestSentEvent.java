package data;

import data.event.BaseEvent;

/**
 * Created by iran on 2015-11-14.
 */
public class FriendRequestSentEvent extends BaseEvent {
    public FriendRequestSentEvent(int targetId) {
        this.requestType = targetId;
    }
}
