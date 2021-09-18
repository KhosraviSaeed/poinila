package data.model;

/**
 * Created by iran on 2015-07-20.
 */
public enum FriendRequestAnswer {
    ACCEPT ("accept"),
    REJECT ("reject");


    private final String answer;
    FriendRequestAnswer(String answer) {
        this.answer = answer;
    }
    public String getAnswer(){
        return answer;
    }
}
