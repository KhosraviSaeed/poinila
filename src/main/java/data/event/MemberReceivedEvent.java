package data.event;

import data.model.Member;

/**
 * Created by AlirezaF on 7/22/2015.
 */
public class MemberReceivedEvent extends BaseEvent{
    public Member member;

    public MemberReceivedEvent(Member member) {
        this.member = member;
    }
}
