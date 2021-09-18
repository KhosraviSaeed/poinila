package data.event;

import data.model.Circle;

/**
 * Created by iran on 2015-09-20.
 */
public class CircleReceivedEvent {
    public Circle circle;

    public CircleReceivedEvent(Circle circle) {
        this.circle = circle;
    }
}
