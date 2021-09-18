package data.event;

import java.util.List;

import data.model.Member;

/**
 * Created by iran on 2015-07-27.
 */
public class MembersReceivedEvent extends BaseEvent{// extends IdentifiableEvent{

    public List<Member> members;
    public String bookmark;

    public MembersReceivedEvent(List<Member> members, String bookmark) {
        //super(receiverTag);

        this.members = members;
        this.bookmark = bookmark;
    }
}
