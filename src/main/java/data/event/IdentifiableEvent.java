package data.event;

/**
 * Created by iran on 2015-08-22.
 */
public class IdentifiableEvent extends BaseEvent {
    public IdentifiableEvent(ReceiverName receiverName){
        this.receiverName = receiverName;
    }
}
