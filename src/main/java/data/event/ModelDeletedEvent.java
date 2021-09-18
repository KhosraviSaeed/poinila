package data.event;

import com.raizlabs.android.dbflow.structure.BaseModel;
/**
 * Created by iran on 2015-09-27.
 */
public class ModelDeletedEvent extends ModelEvent{
    public ModelDeletedEvent(BaseModel model) {
        super(model);
    }
}
