package data.event;

import java.util.List;

/**
 * Created by iran on 2015-07-28.
 */
public class FramesReceivedEvent {
    public FramesReceivedEvent(List<data.model.Frame> frames) {
        this.frames = frames;
    }

    public List<data.model.Frame> frames;
}
