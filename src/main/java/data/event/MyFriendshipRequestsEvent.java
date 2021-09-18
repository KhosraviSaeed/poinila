package data.event;


import java.util.List;

import data.model.InvitationNotif;

/**
 * Created by iran on 2015-08-15.
 */
public class MyFriendshipRequestsEvent extends BaseEvent {
    public List<InvitationNotif> data;
    public String bookmark;

    public MyFriendshipRequestsEvent(List<InvitationNotif> data, String bookmark) {
        this.data = data;
        this.bookmark = bookmark;
    }
}
