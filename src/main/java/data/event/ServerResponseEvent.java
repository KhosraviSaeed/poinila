package data.event;

/**
 * Created by iran on 2015-10-04.
 */
public class ServerResponseEvent extends data.event.BaseEvent {
    public int errorCode;

    public ServerResponseEvent(boolean succeed, ReceiverName receiverName) {
        super(receiverName);
        this.succeed = succeed;
    }
    public boolean succeed;

    public ServerResponseEvent(boolean succeed, ReceiverName receiverName, int errorCode) {

        this.succeed = succeed;
        this.receiverName = receiverName;
        this.errorCode = errorCode;
    }
}
