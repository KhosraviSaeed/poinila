package data.event;

import java.util.List;

import data.model.Member;

/**
 * Created by iran on 2015-07-27.
 */
public class MemberFollowersEvent {
    public MemberFollowersEvent(List<Member> members, String bookmark) {
        this.members = members;
    }

    public List<Member> members;
}
