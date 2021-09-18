package data.event;

import java.util.List;

/**
 * Created by iran on 2015-08-01.
 */
public class MyFrameReceivedEvent {
    public List<data.model.Frame> frames;

    public MyFrameReceivedEvent(List<data.model.Frame> frames) {
        this.frames = frames;
    }
}
