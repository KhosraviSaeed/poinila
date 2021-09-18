package data.event;

/**
 * Created by iran on 2015-07-25.
 */
public class CollectionReceivedEvent extends data.event.BaseEvent {
    public data.model.Collection collection;

    public CollectionReceivedEvent(data.model.Collection collection){
        this.collection = collection;
    }

    public CollectionReceivedEvent(data.model.Collection collection, ReceiverName target){
        this.collection = collection;
        this.receiverName = target;
    }
}
