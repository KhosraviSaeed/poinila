package data.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;
import org.parceler.ParcelConverter;

/**
 * Created by iran on 2015-07-03.
 */
@Parcel(analyze=Tag.class)
@Table(database = data.database.PoinilaDataBase.class)
public class Tag extends BaseModel implements data.model.Identifiable {

    @Column
    @PrimaryKey
    public int id;

    @Column
    public String name;

    public transient boolean selected = false;

    public Tag(){}

    public Tag(int id, String name){
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return String.valueOf(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return id == tag.id;
    }

    public static Tag invalidIdTag(String tagString) {
        return new Tag(-1, tagString);
    }
}
