package data.event;

import java.util.List;

/**
 * Created by iran on 2015-10-10.
 */
public class StringsReceivedEvent {
    public List<String> strings;

    public StringsReceivedEvent(List<String> strings) {
        this.strings = strings;
    }
}
