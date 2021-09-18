package data.event;

import java.util.List;

/**
 * Created by iran on 2015-08-26.
 */
public class TopicsReceivedEvent extends data.event.BaseEvent {
    public List<data.model.Topic> data;

    public TopicsReceivedEvent(List<data.model.Topic> data) {
        this.data = data;
    }
}
