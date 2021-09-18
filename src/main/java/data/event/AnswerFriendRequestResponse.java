package data.event;

/**
 * Created by iran on 2015-10-04.
 */
public class AnswerFriendRequestResponse extends data.event.ServerResponseEvent {
    public data.model.FriendRequestAnswer answer;

    public AnswerFriendRequestResponse(boolean succeed, data.model.FriendRequestAnswer answer) {
        super(succeed, ReceiverName.SelectInterest);
        this.answer = answer;
    }
}
