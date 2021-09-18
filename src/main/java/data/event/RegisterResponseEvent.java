package data.event;

/**
 * Created by AlirezaF on 12/7/2015.
 */
public class RegisterResponseEvent extends BaseEvent {
    public static final int DUPLICATE_USERNAME = 1;
    public static final int USED_VERIFICATION_CODE = 2;

    public RegisterResponseEvent(boolean successful) {
        this.successful = successful;
    }

    public boolean successful;
    public int errorCode;

    public RegisterResponseEvent(boolean successful, int errorCode) {

        this.successful = successful;
        this.errorCode = errorCode;
    }
}
