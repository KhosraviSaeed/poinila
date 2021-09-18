package data.event;
/**
 * Created by iran on 2015-11-07.
 */
public class CollectionUpdatedEvent extends data.event.BaseEvent {
    public data.model.Collection collection;

    public CollectionUpdatedEvent(data.model.Collection collection) {
        this.collection = collection;
    }
}
