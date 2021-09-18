package data.event;

import data.model.Frame;

/**
 * Created by iran on 2015-09-20.
 */
public class FrameReceivedEvent {
    public Frame frame;

    public FrameReceivedEvent(Frame frame) {

        this.frame = frame;
    }
}
