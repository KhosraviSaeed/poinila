package data.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by iran on 2015-07-03.
 */
@Table(database = data.database.PoinilaDataBase.class)
public class Content extends BaseModel implements data.model.Identifiable {
    public Content(String url) {
        this.url = url;
    }

    public Content(){}

    public Content(String url, String text) {
        this.url = url;
        this.text = text;
    }

    @Column(name = "id")
    @PrimaryKey
    public String url;

    @Column
    public String text;

  /*  public Content(String url, Spanned text) {
        this.url = url;
        this.text = text.toString();
    }*/


    @Override
    public String getId() {
        return url;
    }
}
