package data.event;

import java.util.List;

import data.model.Member;

/**
 * Created by iran on 2015-07-07.
 */
public class SearchMemberEvent {
    private int statusCode;
    List<Member> data;
    public SearchMemberEvent(List<Member> posts) {
        this.data = posts;
        statusCode=200;
    }

    public SearchMemberEvent(int statusCode) {
        this.statusCode = statusCode;
    }

    public List<Member> getData(){
        return data;
    }

    public int getStatusCode(){
        return statusCode;
    }
}
