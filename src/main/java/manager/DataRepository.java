package manager;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.ConnectionUitls;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.ContextHolder;
import com.shaya.poinila.android.util.PoinilaPreferences;
import com.shaya.poinila.android.utils.PonilaAccountManager;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import data.PoinilaNetService;
import data.database.PoinilaDataBase;
import data.event.BaseEvent;
import data.event.ContentReceivedEvent;
import data.event.ModelCreatedEvent;
import data.event.ModelDeletedEvent;
import data.event.ModelUpdatedEvent;
import data.event.MyInfoReceivedEvent;
import data.event.PostReceivedEvent;
import data.event.RemainedInvitesEvent;
import data.event.SystemPreferencesReceivedEvent;
import data.model.Circle;
import data.model.Collection;
import data.model.Content;
import data.model.Frame;
import data.model.Identifiable;
import data.model.Member;
import data.model.Post;
import data.model.SystemPreferences;

/**
 * Created by iran on 2015-07-03.
 */
public class DataRepository {
    private static DataRepository instance;
    private boolean shouldShowRatingToUser;
    private int remainedInvites = 0;
    private SystemPreferences systemPreferences;
    private boolean isUserAnonymous = true;

    public DataRepository() {
        BusProvider.getBus().register(this);
    }

    public static DataRepository getInstance() {
        if(instance == null )
            instance = new DataRepository();
        return instance;
    }

   /* public void destroy(){
        BusProvider.getBus().unregister(this);
    }*/

    public void getSuggestions(boolean fromServer, boolean readCache, String bookmark, int cachedItems) {
        if (readCache)
            manager.DBFacade.getSuggestions(cachedItems);
        if (fromServer){
            PoinilaNetService.getSuggestions(bookmark);
        }
    }

    public void saveSuggestions(List<Post> posts) {
//        ProcessModelInfo<Post> pmi = ProcessModelInfo.withModels(posts);
//        TransactionManager.getInstance().addTransaction(new SaveModelTransaction<>(pmi));
        DatabaseDefinition database  = FlowManager.getDatabase(PoinilaDataBase.class);

//        for(Post post : posts)


        ProcessModelTransaction processModelTransaction =
                new ProcessModelTransaction.Builder<>(new ProcessModelTransaction.ProcessModel<Post>() {
                    @Override
                    public void processModel(Post model) {
                        // call some operation on model here
                        model.save();
//                        model.insert(); // or
//                        model.delete(); // or
                    }
                }).processListener(new ProcessModelTransaction.OnModelProcessListener<Post>() {
                    @Override
                    public void onModelProcessed(long current, long total, Post modifiedModel) {

                    }
                }).addAll(posts).build();
        Transaction transaction = database.beginTransactionAsync(processModelTransaction).build();
        transaction.execute();

    }

    public void getPost(String postID, manager.RequestSource target, int requestId) {
        if (target != manager.RequestSource.FORCE_ONLINE){
            Post post = DBFacade.loadModel(postID, Post.class);
            if (post != null)
                BusProvider.getBus().post(new PostReceivedEvent(post.getModel(), requestId));
        }
        if (target != RequestSource.FORCE_OFFLINE && ConnectionUitls.isNetworkOnline()){
            PoinilaNetService.getPost(postID, requestId);
        }
    }

    public void getProfile(String memberID) {
        //Member member = DBFacade.getItem(profileID, Member.class);
        //if (member == null) {
        PoinilaNetService.getMemberProfile(memberID);
        // }
    }

    public void getMyProfile(){
        PoinilaNetService.getMemberProfile(getMyId());

    }

    public void getMyFollowedCollections(String frameID, String bookmark) {
        PoinilaNetService.getFollowedCollections(getMyId(), frameID, bookmark, BaseEvent.ReceiverName.MyFollowedCollections);
    }

    public void getTopics(){
        PoinilaNetService.getTopics();
    }

    // TODO:
    public String getMyId() {
        return PoinilaPreferences.getMyId();
    }

    public void getPeopleFollowingCollections(String memberId, String bookmark) {
        PoinilaNetService.getFollowedCollections(memberId, null, bookmark, BaseEvent.ReceiverName.CollectionListFragment);
    }

    public void getPostsWithQuery(List<String> queries, String bookmark) {
        PoinilaNetService.searchPostWithQuery(queries, bookmark);
    }

    public void getCollectionsWithQuery(List<String> queries, String bookmark) {
        PoinilaNetService.searchCollectionsWithQuery(queries, bookmark);
    }

    public void getMembersWithQuery(List<String> queries, String bookmark) {
        PoinilaNetService.searchMembersWithQuery(queries, bookmark);
    }

    public List<Frame> getFrames() {
        // TODO: fetch from db if user is null, otherwise read from user;
       /* if (user != null)
            return user.frames;*/
        return new ArrayList<>(Arrays.asList(new Frame(1, "sport"), new Frame(2, "cinema")));
    }

    // TODO
    public void getCollection(@NotNull String collectionIdOrName, @Nullable String userName, RequestSource target) {
        /*if (target != RequestTarget.FORCE_ONLINE){
            Collection collection = DBFacade.loadModel(collectionID, Collection.class);
            if (collection != null)
                BusProvider.getBus().post(new CollectionReceivedEvent(collection.getModel()));
        }*/
        if (target != RequestSource.FORCE_OFFLINE && ConnectionUitls.isNetworkOnline()){
            PoinilaNetService.getCollection(collectionIdOrName, userName);
        }
    }

    public void getPostContent(String contentUrl, int postID) {
        Content content = DBFacade.loadModel(contentUrl, Content.class);
        if (content != null)
            BusProvider.getBus().post(new ContentReceivedEvent(content.text, postID));
        else
            PoinilaNetService.getPostContent(contentUrl, postID);
    }

    public static void getCollectionPosts(String collectionIdOrName, @Nullable String userName,
                                          String bookmark, BaseEvent.ReceiverName receiverName) {
        PoinilaNetService.getCollectionPosts(collectionIdOrName, userName, bookmark, receiverName, false);
    }

    public static void getCollectionPostsImages(String collectionIdOrName, @Nullable String userName,
                                          String bookmark, BaseEvent.ReceiverName receiverName) {
        PoinilaNetService.getCollectionPosts(collectionIdOrName, userName, bookmark, receiverName, true);
    }

    public void getMemberFriends(String memberID, String bookmark) {
        PoinilaNetService.getMemberFriends(memberID, bookmark);
    }

    public void getMyInfo(boolean fromServer, MyInfoReceivedEvent.MY_INFO_TYPE type){//boolean networkOnline) {
        if (fromServer)
            PoinilaNetService.getMyInfo(type);
        else{
            BusProvider.getBus().post(new MyInfoReceivedEvent(DBFacade.getCachedMyInfo(), true, type));
        }
    }


    Object tempModel;
    String tempModelID;
    long serverTimeDifference = -1;

    public void putTempModel(Object model) {
        tempModel = model;
        if (model instanceof Identifiable)
            tempModelID = ((Identifiable) model).getId();
    }

    public <T> T getTempModel(Class<T> clazz){
        if (clazz.isInstance(tempModel)) { //tempModel.getClass().getSimpleName().equals(clazz.getSimpleName())
            if (tempModel instanceof Identifiable && !tempModelID.equals(((Identifiable) tempModel).getId()))
                return null;
            return clazz.cast(tempModel); //clazz.cast(tempModel);
        }return null;
    }

    public void getMemberCollections(String memberID, String bookmark) {
        PoinilaNetService.getMemberCollections(memberID, bookmark);
    }

    public void getPostComments(String postID, String bookmark) {
        PoinilaNetService.getPostComments(postID, bookmark);
    }

    public void putServerTimeDifference(long timeDifference) {
        this.serverTimeDifference = timeDifference;
        PoinilaPreferences.putServerTime(serverTimeDifference);
    }

    public long getServerTimeDifference(){
        return (serverTimeDifference == -1) ? PoinilaPreferences.getServerTimeDifference() : serverTimeDifference;
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        return dir != null && dir.delete();
    }

    public static void clearDataOnLogout(){
        PonilaAccountManager.getInstance().removePonilaAccount();
        PoinilaPreferences.clearData();
        DBFacade.clearData(Member.class, Circle.class, Frame.class, Collection.class, Post.class);
        deleteCache(ContextHolder.getContext());
    }

    @Subscribe public void keepSystemPreferences(SystemPreferencesReceivedEvent event){
        systemPreferences = event.systemPreferences;
    }

    @Subscribe
    public void saveModel(ModelCreatedEvent event){
        event.model.save();
    }

    @Subscribe
    public void updateModel(ModelUpdatedEvent event){
        event.model.update();
    }

    @Subscribe
    public void deleteModel(ModelDeletedEvent event){
        event.model.delete();
    }

    @Subscribe
    public void setReminedInvites(RemainedInvitesEvent event){
        if (event.remained == -1)  // Decrementing after using on invite. not wanted to define new method! :)
            this.remainedInvites--;
        else
            this.remainedInvites = event.remained;
    }

    public int getRemainedInvites(){
        return remainedInvites;
    }

    public boolean isMe(int id) {
        return String.valueOf(id).equals(instance.getMyId());
    }

    public static boolean isMyCollection(Collection collection) {
        return collection.owner.getId().equals(getInstance().getMyId());
        //return DBFacade.getMyCollections().contains(collection);
    }

    public static void calculateIsTimeToAskAboutRating() {
        if (!userNeverWantsToRate() && enoughDays() && enoughOpenCount()){
            getInstance().shouldShowRatingToUser = true;
        }
    }

    private static boolean userNeverWantsToRate() {
        return PoinilaPreferences.getAppOpenCountThreshold() == Integer.MAX_VALUE ||
                PoinilaPreferences.getRatingDaysThreshold() == Integer.MAX_VALUE;
    }

    private static boolean enoughOpenCount() {
        return PoinilaPreferences.getOpenApplicationCount() >= PoinilaPreferences.getAppOpenCountThreshold();
    }

    private static boolean enoughDays() {
        return TimeUnit.MILLISECONDS.toDays(
                Calendar.getInstance().getTimeInMillis() - PoinilaPreferences.getFirstLoginDateTime()) >=
                PoinilaPreferences.getRatingDaysThreshold();
    }

    public static void updateAskRatingThreshold(boolean neverAskAgain){
        PoinilaPreferences.increaseRatingDaysThreshold(neverAskAgain);
        PoinilaPreferences.increaseAppOpenCountThreshold(neverAskAgain);
        getInstance().shouldShowRatingToUser = false;
    }

    public static boolean shouldAskForRating(){
        return !isUserAnonymous() &&
                getInstance().shouldShowRatingToUser &&
                DataRepository.getDestinationMarket() != null;

        // for testing
//         return true;
    }

    public static SystemPreferences.MarketPackages getDestinationMarket() {
        return instance.systemPreferences != null ?
                instance.systemPreferences.rateDestinationMarket : null;
    }

    public static void setSystemPreferences(SystemPreferences systemPreferences){
        instance.systemPreferences = systemPreferences;
    }

    public static List<String> getSMSProviderNumbers() {
        List<String> numbers = instance.systemPreferences != null ?
                instance.systemPreferences.smsProviderNumbers : null;
        return numbers != null ? numbers : new ArrayList<String>();
    }

    public static void setUserAsAnonymous(boolean anonymous) {
        //getInstance().isUserAnonymous = anonymous;
        PoinilaPreferences.putUserAnonymity(anonymous);
    }

    public static boolean isUserAnonymous() {
        //return getInstance().isUserAnonymous;
        return PoinilaPreferences.isUserAnonymous();
    }

    public static void syncWithMyInfoResponse(MyInfoReceivedEvent event) {
        if (event.me != null) {
            if (!event.fromCache) {
                DataRepository.setUserAsAnonymous(event.me.isAnonymous);
                if (DataRepository.isUserAnonymous())
                    return;

                DBFacade.clearData(Circle.class, Frame.class, Collection.class, Member.class);
                DBFacade.saveModels(event.me.circles, Circle.class);
                DBFacade.saveModels(event.me.frames, Frame.class);
                DBFacade.saveModels(event.me.owningCollections, Collection.class);
                DBFacade.saveModel(event.me);
                PoinilaPreferences.putMyId(event.me.getId());
            }
        }
    }

    public static void logout() {
        if (!isUserAnonymous())
            PoinilaNetService.logout();
        DataRepository.setUserAsAnonymous(true);
        clearDataOnLogout();
    }

    public static void logoutEvent() {
        LocalBroadcastManager.getInstance(ContextHolder.getContext()).sendBroadcast(new Intent(ConstantsUtils.INTENT_FILTER_JWT));
    }
}