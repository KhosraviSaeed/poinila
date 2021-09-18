package data.event;


/**
 * Created by AlirezaF on 12/7/2015.
 */
public class VerificationRequestResponse extends data.event.BaseEvent {
    public boolean succeed;
    public int code;
    public String errorExplanation;
    public boolean byEmail;
    public String emailOrPhone;

    public VerificationRequestResponse(boolean succeed, boolean byEmail, String emailOrPhone) {
        this(succeed, null);
        this.byEmail = byEmail;
        this.emailOrPhone = emailOrPhone;
    }

    public VerificationRequestResponse(boolean succeed, String errorExplanation) {
        this.succeed = succeed;
        this.errorExplanation = errorExplanation;
    }

    public VerificationRequestResponse(boolean succeed) {
        this(succeed, null);
    }

    public VerificationRequestResponse(boolean succeed, int code) {
        this.succeed = succeed;
        this.code = code;
    }
    /*public VerificationRequestSentEvent(String code) {
        Code = code;
    }

    public VerificationRequestSentEvent() {
    }

    public String Code;*/
}
