package com.shaya.poinila.android.presentation.uievent;


import java.util.List;

import data.model.Frame;

/**
 * Created by iran on 2015-08-01.
 */
public class FramesUpdatedUIEvent {
    public List<Frame> frames;

    public FramesUpdatedUIEvent(List<Frame> frames) {
        this.frames = frames;
    }
}
