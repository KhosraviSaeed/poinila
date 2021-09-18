package data.model;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Table;

import data.database.PoinilaDataBase;

/**
 * Created by iran on 2015-11-03.
 */
@Table(database = PoinilaDataBase.class)
public class ImageTag extends Tag{
    //TODO: DBFlow doesn't support inheritance for primary key field; it will be added in 3.0.0 release though

    @SerializedName("images")
    public ImageUrls imageUrls;

    public ImageTag(){}
}
