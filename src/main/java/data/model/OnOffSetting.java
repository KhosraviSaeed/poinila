package data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by iran on 2015-09-07.
 */
public class OnOffSetting {
    public static final int OFF = 0;
    public static final int ON = 1;
    @SerializedName("type_id")
    public int typeID;

    public int value; // 0 for off and 1 for on

    public String name;

    public String code;
    public transient boolean enabled = true;

    public void off(){
        value = OFF;
    }
    public void on(){
        value = ON;
    }
}
