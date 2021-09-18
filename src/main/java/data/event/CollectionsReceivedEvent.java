package data.event;

import java.util.List;

import data.model.Collection;

/**
 * Created by iran on 2015-07-06.
 */
public class CollectionsReceivedEvent extends IdentifiableEvent{
    public List<Collection> collections;
    public String bookmark;


    public CollectionsReceivedEvent(List<Collection> collections, String bookmark, ReceiverName receiverName) {
        super(receiverName);
        this.collections = collections;
        this.bookmark = bookmark;
    }

}
