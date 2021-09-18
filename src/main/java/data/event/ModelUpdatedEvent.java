package data.event;

import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by iran on 2015-09-27.
 */
public class  ModelUpdatedEvent extends data.event.ModelEvent {
    public ModelUpdatedEvent(BaseModel model) {
        super(model);
    }
}
