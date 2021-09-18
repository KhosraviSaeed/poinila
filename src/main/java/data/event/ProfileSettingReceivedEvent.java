package data.event;

import data.model.Member;

/**
 * Created by iran on 2015-07-29.
 */
public class ProfileSettingReceivedEvent {
    public ProfileSettingReceivedEvent(Member member) {
        this.member = member;
    }

    public Member member;
}
