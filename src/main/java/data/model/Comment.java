package data.model;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.shaya.poinila.android.util.PoinilaPreferences;

import org.parceler.Parcel;

import java.util.Date;

import data.database.PoinilaDataBase;

/**
 * Created by iran on 2015-06-25.
 */
@Parcel(analyze= Comment.class)
@Table(database = PoinilaDataBase.class)
public class Comment extends BaseModel implements Identifiable {
    @Column
    @PrimaryKey
    public int id;
    public String content;
    @SerializedName(value = "creation_time") public Date creationDate;
    // last_update_time
    @SerializedName(value = "is_deletable") public boolean deletable;
    public Member commenter;

    @Override
    public String getId() {
        return String.valueOf(id);
    }

    public boolean isDeletable() {
        return commenter.getId().equals(PoinilaPreferences.getMyId());
    }
}
