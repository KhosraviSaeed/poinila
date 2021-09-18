package data.event;

import com.shaya.poinila.android.presentation.view.activity.SettingActivity;

/**
 * Created by iran on 1/11/2016.
 */
public class UpdateProfileSettingResponse extends BaseEvent {
    public boolean success;
    public SettingActivity.SettingType settingType;

    public UpdateProfileSettingResponse(boolean success) {
        this.success = success;
    }

    public UpdateProfileSettingResponse(boolean success, SettingActivity.SettingType settingType) {
        this.success = success;
        this.settingType = settingType;
    }
}
