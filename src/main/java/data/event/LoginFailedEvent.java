package data.event;

import com.google.gson.JsonObject;

/**
 * Created by iran on 2015-08-24.
 */
public class LoginFailedEvent {
    public int code;
    public JsonObject dataJson;
    public String error;

    public LoginFailedEvent(int code, JsonObject dataJson) {
        this.code = code;
        this.dataJson = dataJson;
    }
}
