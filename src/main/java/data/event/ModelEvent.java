package data.event;

import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by iran on 2015-09-27.
 */
public abstract class ModelEvent {
    public BaseModel model;

    public ModelEvent(BaseModel model) {
        this.model = model;
    }
}
