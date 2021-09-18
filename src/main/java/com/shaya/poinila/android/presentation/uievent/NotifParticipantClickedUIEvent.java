package com.shaya.poinila.android.presentation.uievent;

import data.model.ImageUrls;
import data.model.Participant;

/**
 * Created by iran on 2015-11-02.
 */
public class NotifParticipantClickedUIEvent {
    public Participant participant;
    public ImageUrls.ImageType participantsType;

    public NotifParticipantClickedUIEvent(Participant participant, ImageUrls.ImageType participantsType) {
        this.participant = participant;
        this.participantsType = participantsType;
    }
}
