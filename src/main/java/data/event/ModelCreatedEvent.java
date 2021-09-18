package data.event;

import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by iran on 2015-09-27.
 */
public class ModelCreatedEvent extends data.event.ModelEvent {
    public ModelCreatedEvent(BaseModel model) {
        super(model);
    }
}
