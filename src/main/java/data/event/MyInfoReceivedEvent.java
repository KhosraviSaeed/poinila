package data.event;


import data.model.Member;

/**
 * Created by iran on 2015-07-27.
 */
public class MyInfoReceivedEvent extends BaseEvent {
    public Member me;
    public boolean fromCache;
    public MY_INFO_TYPE type;

    public enum MY_INFO_TYPE{
        LOAD, UPDATE, VERIFY
    }

    public MyInfoReceivedEvent(Member me, boolean fromCache, MY_INFO_TYPE type) {
        this.me = me;
        this.fromCache = fromCache;
        this.type  = type;
    }
}
