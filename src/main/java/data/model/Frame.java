package data.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;
import org.parceler.ParcelConverter;

/**
 * Created by iran on 2015-07-28.
 */
@Parcel(analyze=Frame.class)
@Table(database = data.database.PoinilaDataBase.class)
public class Frame extends BaseModel implements data.model.Identifiable {
    public Frame() {
    }

    public Frame(int id, String name) {
        this.id = id;
        this.name = name;
    }
    @Column
    @PrimaryKey
    public int id;
    @Column
    public String name;

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getId() {
        return String.valueOf(id);
    }

    public static class ModelConverter implements ParcelConverter<Frame> {
        @Override
        public void toParcel(Frame frame, android.os.Parcel parcel) {

        }

        @Override
        public Frame fromParcel(android.os.Parcel parcel) {
            return null;
        }
    }

    /*   @Override
    public Frame getModel() {
        return getModelFromJson(Frame.class);
    }*/
}
