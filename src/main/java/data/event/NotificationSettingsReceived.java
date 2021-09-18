package data.event;


import java.util.List;

import data.model.OnOffSetting;

/**
 * Created by iran on 2015-09-07.
 */
public class NotificationSettingsReceived {
    public List<OnOffSetting> notificationSettings;

    public NotificationSettingsReceived(List<OnOffSetting> notificationSettings) {

        this.notificationSettings = notificationSettings;
    }
}
