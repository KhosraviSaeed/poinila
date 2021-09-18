package data.event;

import java.util.List;

/**
 * Created by iran on 2015-09-08.
 */
public class InterestsReceivedEvent {
    public String superInterestID;
    public List<data.model.ImageTag> interests;

    public InterestsReceivedEvent(List<data.model.ImageTag> interests) {
        this.interests = interests;
    }

    public InterestsReceivedEvent(List<data.model.ImageTag> interests, String superInterestID) {
        this.interests = interests;
        this.superInterestID = superInterestID;
    }
}
