package data.event;

import java.util.List;

import data.model.ImageTag;

/**
 * Created by iran on 2015-09-08.
 */
public class UserInterestsReceivedEvent extends BaseEvent{
    public List<ImageTag> userInterests;

    public UserInterestsReceivedEvent(List<ImageTag> userInterests) {
        this.userInterests = userInterests;
    }
}
