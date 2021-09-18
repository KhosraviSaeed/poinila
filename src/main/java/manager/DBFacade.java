package manager;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.sql.queriable.ModelQueriable;
import com.raizlabs.android.dbflow.structure.Model;
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.shaya.poinila.android.presentation.PoinilaApplication;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.Logger;

import java.util.ArrayList;
import java.util.List;


import data.database.PoinilaDataBase;
import data.event.DashboardEvent;
import data.model.Circle;
import data.model.Circle$Table;
import data.model.Collection;
import data.model.DefaultType;
import data.model.Frame;
import data.model.Member;
import data.model.Member$Table;
import data.model.Post;
import data.model.Post$Table;

import static com.shaya.poinila.android.util.ContextHolder.getContext;

/**
 * Created by AlirezaF on 7/8/2015.
 */
public class DBFacade {

    public static Member getCachedMyInfo() {
        Member me = new Select().from(Member.class).where(Condition.column(Member$Table.id.getNameAlias()).
                is(DataRepository.getInstance().getMyId())).querySingle();

        //Member me = Member.getTestItem();
        if (me != null && !TextUtils.isEmpty(me.jsonContent)) {
            me = me.getModel();
            return me;
        }
        return null;
    }

    public static List<Circle> getMyCircles() {
        // TODO: in chie akhe? gand zadi ba tarrahi!!
       /* for (int i = 0; i < circles.size(); i++){
            circles.set(i, circles.get(i).getModel());
        }*/
        //BusProvider.getBus().post(new CirclesReceivedEvent(circles));
        return new Select().from(Circle.class).where(
                Condition.column(Circle$Table.defaultType.getNameAlias()).isNot(DefaultType.DEFAULT)).queryList();
    }

    public static Circle getDefaultCircle() {
        return new Select().from(Circle.class).where
                (Condition.column(Circle$Table.defaultType.getNameAlias()).eq(DefaultType.DEFAULT)).querySingle();
        // Circle$Table.PRIVACY, PrivacyType.PUBLIC.name()
    }


    public static List<Frame> getMyFrames() {
        return new Select().from(Frame.class).queryList();
  /*      for (int i = 0; i < frames.size(); i++){
            frames.set(i, frames.get(i).getModel());
        }*/
        //BusProvider.getBus().post(new MyFrameReceivedEvent(frames));
    }

    public static void getSuggestions(int cachedItems) {

        if(FlowManager.isDatabaseIntegrityOk(PoinilaDataBase.NAME)){
            FlowManager.getDatabaseForTable(Post.class)
                    .beginTransactionAsync(new QueryTransaction.Builder<>(
                            SQLite.select(Post$Table.jsonContent)
                                    .from(Post.class)
                                    .where()
                                    .orderBy(Post$Table.creationTime, false)
                                    .limit((int) ConstantsUtils.SUGGESTION_PER_REQUEST)
                                    .offset(cachedItems))
                            .queryResult(new QueryTransaction.QueryResultCallback<Post>() {
                                @Override
                                public void onQueryResult(QueryTransaction transaction, @NonNull CursorResult tResult) {

                                    List<Post> posts = tResult.toList();
                                    List<Post> data = new ArrayList<>();

                                    int length = posts.size();
                                    for(int i=0 ; i< length ; i++) {
                                        data.add(posts.get(i).getModel());
                                    }

                                    BusProvider.getBus().post(new DashboardEvent(data, true));

                                }
                            }).build()).build().execute();
        }
    }


    public static <T extends Model> T loadModel(String modelID, Class<T> clazz) {
        return new Select().from(clazz).where(Condition.column(NameAlias.builder("id").build()).eq(modelID)).querySingle();
    }

    public static <T extends Model> void saveModels(List<T> models, Class mClass) {
//        ProcessModelInfo<T> pmi = ProcessModelInfo.withModels(models);
//        TransactionManager.getInstance().addTransaction(new SaveModelTransaction<>(pmi));

        int count = models.size();
        for (int i = 0; i < count; i++) {
            models.get(i).save();
        }


    }

    public static <T extends Model> void saveModel(T model) {
        model.save();
    }

    public static <T extends Model> void updateModel(T model) {
        model.update();
    }


    public static List<Collection> getMyCollections() {
        return new Select().from(Collection.class).queryList();
        /*List<Collection> collections = new Select().from(Collection.class).queryList();
        for (int i = 0; i < collections.size(); i++) {
            collections.set(i, collections.get(i).getModel());
        }
        return collections;*/
    }

    /**
     * @param tables
     * @see <a href=https://github.com/Raizlabs/DBFlow/blob/master/usage/SQLQuery.md>sql statements</a>
     */
    @SafeVarargs
    public static void clearData(Class<? extends Model>... tables) {
        //FlowManager.getDatabase(PoinilaDataBase.NAME).reset(getContext());
        // TODO: it's a temporary workaround. the above line doesn't work currently.
        Delete.tables(tables);
        FlowManager.init(new FlowConfig.Builder(PoinilaApplication.getAppContext()).build());
    }
}
