package data.event;

/**
 * Created by iran on 1/20/2016.
 */
public class SystemPreferencesReceivedEvent extends data.event.BaseEvent {
    public data.model.SystemPreferences systemPreferences;

    public SystemPreferencesReceivedEvent(data.model.SystemPreferences systemPreferences) {

        this.systemPreferences = systemPreferences;
    }
}
