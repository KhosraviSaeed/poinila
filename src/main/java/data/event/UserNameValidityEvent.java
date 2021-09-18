package data.event;

/**
 * Created by iran on 12/14/2015.
 */
public class UserNameValidityEvent {
    public static final int DUPLICATE = 1;
    public static final int RESERVED = 2;
    public static final int RULE = 3;
    public static final int LENGTH = 4;
    public boolean success;
    public final int error;

    public UserNameValidityEvent(int error) {
        this.error = error;
    }

    public UserNameValidityEvent(boolean success, int error) {

        this.success = success;
        this.error = error;
    }
}
