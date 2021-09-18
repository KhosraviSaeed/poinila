package data.event;

/**
 * Created by iran on 2015-07-27.
 */
public class FriendCircleNotChangedEvent {
    public String circleID;
    public String friendId;

    public FriendCircleNotChangedEvent(String circleID, String friendId) {
        this.circleID = circleID;
        this.friendId = friendId;
    }
}
