package data.database;

import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.converter.TypeConverter;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;
import org.parceler.ParcelClass;
import org.parceler.ParcelConverter;

import java.io.Serializable;

import data.PoinilaNetService;
import data.model.Gender;
import data.model.PrivacyType;

/**
 * Created by iran on 2015-07-23.
 */
@Database(name = PoinilaDataBase.NAME, version = PoinilaDataBase.VERSION, generatedClassSeparator = "$")
public class PoinilaDataBase {
    public static final String NAME = "poinila";

    public static final int VERSION = 1;
    public static final String SUGGESTION_INDEX = "index_suggestion";

    private static Gson gson;
    public static Gson getGson(){
        return gson;
    }
  /*  static {
        gson = new GsonBuilder().create();
        Index<Suggestion> index = new Index<>(SUGGESTION_INDEX).
                on(Suggestion.class, Suggestion$Table.CREATIONTIME);
        // begins an index
        index.enable();
    }*/

/*    @ParcelClass(value = ModelAdapter.class, annotation = @Parcel(converter = ModelAdapterConverter.class))
    public abstract static class PoinilaDBModel<T> extends BaseModel{
        @Column
        public String jsonContent;

        protected abstract T getModel();
        protected T getModelFromJson(Class<T> t){
            return PoinilaNetService.getGson().fromJson(jsonContent, t);
        }
    }

    public class ModelAdapterConverter implements ParcelConverter<ModelAdapter> {
        @Override
        public void toParcel(ModelAdapter input, android.os.Parcel parcel) {

        }

        @Override
        public ModelAdapter fromParcel(android.os.Parcel parcel) {
            return null;
        }
    }*/
    public abstract static class PoinilaDBModel<T> extends BaseModel implements Serializable{

        @Column
        public String jsonContent;

        protected abstract T getModel();
        protected T getModelFromJson(Class<T> t){
            return PoinilaNetService.getGson().fromJson(jsonContent, t);
        }
    }

    public static class PrivacyTypeConverter extends TypeConverter<String, PrivacyType>{

        @Override
        public String getDBValue(PrivacyType model) {
            return model.name();
        }

        @Override
        public PrivacyType getModelValue(String data) {
            return PrivacyType.valueOf(data);
        }
    }

    public static class SpannedTypeConverter extends TypeConverter<String, Spanned>{

        @Override
        public String getDBValue(Spanned model) {
            return model.toString();
        }

        @Override
        public Spanned getModelValue(String data) {
            return Html.fromHtml(data);
        }
    }

    public static class GenderTypeConverter extends TypeConverter<String, Gender>{

        @Override
        public String getDBValue(Gender model) {
            return model.name();
        }

        @Override
        public Gender getModelValue(String data) {
            return Gender.valueOf(data);
        }
    }

}
