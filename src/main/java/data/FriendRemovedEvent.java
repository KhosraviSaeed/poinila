package data;


import data.event.BaseEvent;

/**
 * Created by iran on 2015-11-14.
 */
public class FriendRemovedEvent extends BaseEvent {
    public FriendRemovedEvent(int targetId) {
        this.requestType = targetId;
    }
}
