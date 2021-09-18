package data.event;


import java.util.List;

/**
 * Created by iran on 3/6/2016.
 */
public abstract class AbstractNotificationsReceivedEvent extends data.event.BaseEvent {
    public List<data.model.Notification> data;
    public String bookmark;

    public AbstractNotificationsReceivedEvent(List<data.model.Notification> data, String bookmark) {
        this.data = data;
        this.bookmark = bookmark;
    }

    public static class MyNotificationsReceivedEvent extends AbstractNotificationsReceivedEvent {

        public MyNotificationsReceivedEvent(List<data.model.Notification> data, String bookmark) {
            super(data, bookmark);
        }
    }

    public static class OthersNotificationsReceivedEvent extends AbstractNotificationsReceivedEvent {

        public OthersNotificationsReceivedEvent(List<data.model.Notification> data, String bookmark) {
            super(data, bookmark);
        }
    }
}
