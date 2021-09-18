package data.model;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import org.parceler.Parcel;
import org.parceler.ParcelConverter;

import data.database.PoinilaDataBase;

/**
 * Created by iran on 2015-07-20.
 */
@Parcel(analyze= Circle.class)
@Table(database = PoinilaDataBase.class)
public class Circle extends BaseModel implements Identifiable {
    public Circle() {
    }

    public Circle(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Column
    @PrimaryKey
    public int id;

    @Column
    public String name;

    @Column
    @SerializedName(value = "is_default")
    public DefaultType defaultType;

    @Override
    public String getId() {
        return String.valueOf(id);
    }

   /* @Override
    public Circle getModel() {
        return getModelFromJson(Circle.class);
    }*/

    // used in edit friend circles we must show which circles the friend is already assigned.
    public transient boolean selected = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Circle circle = (Circle) o;
        return id == circle.id;
    }

}
