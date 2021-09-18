package data.event;

import java.util.List;

import data.model.Collection;

/**
 * Created by iran on 2015-07-28.
 */
public class FrameCollectionsReceivedEvent {
    public FrameCollectionsReceivedEvent(List<Collection> frames) {
        this.frames = frames;
    }

    public List<Collection> frames;
}
