package data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import data.model.SuggestedWebPagePost;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedOutput;

/**
 * Created by iran on 2015-06-15.
 * @author Alireza Farahani
 */
public interface RestServices {
    String BOOKMARK = "bookmark";
    String ACTION = "action";
    String DATA = "data";
    String IMAGE = "image";

    /*@GET("/users/{user}/repos")
    List<Repo> testJson(@Path("user") String user);*/


    /*--------Suggestion---------*/
    @GET("/suggestion/")
    void getSuggestions(@Query(value = BOOKMARK) String bookmark, Callback<Response> cb);//PoinilaCallback<Response> cb);
    //void getSuggestions(@Query(value = "bookmark") String bookmark, PoinilaCallback<PoinilaResponse<List<Post>>> cb);

    /*----------SEARCH-----------*/
    @GET("/post/search/")
    void getPostsWithQuery(@Query(value = "q") List<String> query, @Query(value = BOOKMARK) String bookmark,
                           data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.Post>>> cb);

    @GET("/collection/search/")
    void getCollectionsWithQuery(@Query(value = "q") List<String> query, @Query(value = BOOKMARK) String bookmark,
                                 data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.Collection>>> cb);

    @GET("/member/search/")
    void getMembersWithQuery(@Query(value = "q") List<String> query, @Query(value = BOOKMARK) String bookmark,
                             data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.Member>>> cb);

    /*----------POST-----------*/
    /*------GET-------*/
    @GET("/post/{post_id}/")
    void getPost(@Path("post_id") String postID, data.PoinilaCallback<data.model.PoinilaResponse<data.model.Post>> cb);

    @GET("/post/{id}/relatedpost/")
    void getRelatedPosts(@Path("id") String postID, @Query(value = BOOKMARK) String bookmark, data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.Post>>> cb);

    //TODO: double check with server format
    @GET("/post/{id}/comment/")
    void getPostComments(@Path("id") String postID, @Query(value = BOOKMARK) String bookmark, data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.Comment>>> cb);

    /**
     * retrieve the collections consisting a specific post.
     * @param postID
     * @param cb
     */
    @GET("/post/{id}/repostcollection/")
    void getRepostCollections(@Path("id") String postID, @Query(value = BOOKMARK) String bookmark, data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.Collection>>> cb);

    @GET("/post/{id}/like/")
    void getPostLikers(@Path("id") String postID, @Query(value = BOOKMARK) String bookmark, data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.Member>>> cb);

    @GET("/post/{id}/open/")
    void informServerOfPostInlineBrowsing(@Path("id") String postId, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @GET("/post/{id}/browse/")
    void informServerOfPostExternalBrowsing(@Path("id") String postId, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    /*@GET("/posts/{id}/")
    void getPost(@Path("id") String id, PoinilaCallback<Post> cb);*/

    /*----------POST-----------*/
    /*------PUT-------*/
    @POST("/post/{id}/like/")
    void favePost(@Path("id") String PostID, @Body HashMap emptyBody, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    /*@POST("/post/{id}/")
    void updatePost(@Path("id") String postID, @Body Post post, PoinilaCallback<PoinilaResponse> cb);*/

    /*-----DELETE-----*/
    @POST("/post/{id}/like/")
    void unfavePost(@Path("id") String PostID, @Body HashMap hashMap, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @POST("/post/{id}/")
    void deletePost(@Path("id") String postID, @Body HashMap hashMap, data.PoinilaCallback<data.model.PoinilaResponse> cb);


    /*-----POST------*/
    @POST("/collection/{id}/repost/")
    void repost(@Path("id") String collectionID, @Body HashMap post, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @POST("/post/{id}/comment/")
    void commentOnPost(@Path("id") String postID, @Body HashMap comment, data.PoinilaCallback<data.model.PoinilaResponse<data.model.Comment>> cb);

    @POST("/comment/{id}/")
    void deleteComment(@Path("id") String commentID, @Body HashMap hashMap, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    /*----------MEMBER-----------*/
    /*------GET-------*/
    @GET("/member/{id}/profile/")
    void getProfileById(@Path("id") String memberID, data.PoinilaCallback<data.model.PoinilaResponse<data.model.Member>> cb);

    @GET("/unique_name/{unique_name}/profile/")
    void getProfileByUserName(@Path("unique_name") String userName, data.PoinilaCallback<data.model.PoinilaResponse<data.model.Member>> cb);


    @GET("/member/{id}/like/")
    void getMemberLikedPosts(@Path("id") String memberID, @Query(value = BOOKMARK) String bookmark,
                             data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.Post>>> cb);

    @GET("/member/{id}/friend/")
    void getMemberFriends(@Path("id") String memberID, @Query(value = BOOKMARK) String bookmark,
                          data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.Member>>> cb);

    @GET("/member/{id}/post/")
    void getMemberPosts(@Path("id") String memberID, @Query(value = BOOKMARK) String bookmark,
                        data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.Post>>> cb);

    @GET("/member/{id}/follower/")
    void getMemberFollowers(@Path("id") String memberID, @Query(value = BOOKMARK) String bookmark,
                            data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.Member>>> cb);

    @GET("/member/{id}/collection/")
    void getMemberCollections(@Path("id") String memberID, @Query(value = BOOKMARK) String bookmark,
                              data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.Collection>>> cb);

    @GET("/member/{id}/followcollection/")
    void getMemberFollowingCollections(@Path("id") String memberID,
                                       @Query(value = BOOKMARK) String bookmark,
                                       @Query(value = "frame_id") String frameID,
                                       data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.Collection>>> cb);

    @GET("/member/{id}/generalconfiguration/")
    void getProfileSettings(@Path("id") String memberID, @Query("type") String type, data.PoinilaCallback<data.model.PoinilaResponse<data.model.Member>> cb);

    @GET("/member/{id}/interest/")
    void getMemberInterests(@Path("id") String memberID, data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.ImageTag>>> cb);

    /*----------MEMBER-----------*/
    /*------PUT-------*/
    @POST("/member/{id}/generalconfiguration/")
    void updateProfile(@Path("id") String memberID, @Body HashMap user, data.PoinilaCallback<data.model.PoinilaResponse> cb);


    @POST("/member/{user_id}/friendshipinvitation/")
    void answerFriendRequest(@Path("user_id") String userID, @Body HashMap circleRequester,
                             data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @POST("/member/{id}/password/")
    void changePassword(@Path("id") String memberID, @Body HashMap password, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @POST("/member/{id}/friendcircle/")
    void updateFriendCircles(@Path("id") String memberID, @Body HashMap circles, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    /*-----POST------*/

    @POST("/member/{user_id}/friend/{friend_id}/circle/{circle_id}/")
    void addFriendToCircle(
            @Path("user_id") String userID,
            @Path("friend_id") String friendID,
            @Path("circle_id") String circleID,
            @Body HashMap emptyPacket,
            data.PoinilaCallback<data.model.PoinilaResponse> cb);

    // TODO:
    @Multipart
    @POST("/member/{id}/profile/photo/")
    void uploadProfilePic(@Path("id") String memberID,
                          @Part(ACTION) String action,
                          @Part(IMAGE) TypedOutput image,
                          data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @POST("/member/{member_id}/friendshipinvitation/")
    void friendRequest(@Path("member_id") String memberID, @Body HashMap circle,//JsonObject circle, //JSONObject circle,
                       data.PoinilaCallback<data.model.PoinilaResponse> cb);


    @Multipart
    @POST("/member/{id}/collection/")
    void createCollectionWithImage(@Path("id") String memberID,
                                   @Part(ACTION) String action,
                                   @Part(DATA) data.model.Collection collection,
                                   @Part(IMAGE) TypedOutput image,
                                   data.PoinilaCallback<data.model.PoinilaResponse<data.model.Collection>> cb);
    /* @Part(value = "name") String name,
                          @Part(value = "description") String description,
                          @Part(value = "topic_id") int topicID,
                          @Part(value = "circle_ids") String circle_IDs,*/

    @POST("/member/{id}/collection/")
    void createCollectionWithoutImage(@Path("id") String memberID,
                                      @Body HashMap CollectionPacket,
                                      data.PoinilaCallback<data.model.PoinilaResponse<data.model.Collection>> cb);

    /*----DELETE---*/
    @POST("/member/{user_id}/friend/{friend_id}/circle/{circle_id}/")
    void removeFriendFromCircle(
            @Path("user_id") String userID,
            @Path("friend_id") String friendID,
            @Path("circle_id") String circleID,
            @Body HashMap emptyPacket,
            data.PoinilaCallback<data.model.PoinilaResponse> cb);

    /**
     *
     * @param userID, ID of the user not his friend! only for consistency in url formats
     * @param cb
     */
    @POST("/member/{id}/friend/{friend_id}/")
    void removeFriend(@Path("id") String userID,// JSONObject friendID,
                      @Path("friend_id") String friendID, @Body HashMap method,
                      data.PoinilaCallback<data.model.PoinilaResponse> cb);


    /*--------COLLECTION----------*/
    /*--GET--*/
    @GET("/collection/{collection_id}/")
    void getCollection(@Path("collection_id") String collectionID, data.PoinilaCallback<data.model.PoinilaResponse<data.model.Collection>> cb);

    @GET("/unique_name/{unique_name}/collection_name/{collection_name}/")
    void getCollectionByName(@Path(value = "collection_name", encode = false) String collectionName, @Path(value = "unique_name", encode = false) String uniqueName,
                             data.PoinilaCallback<data.model.PoinilaResponse<data.model.Collection>> cb);

    // TODO: man bayad vorudi bedam az che zamanio mikham??
    @GET("/collection/{id}/post/")
    void getCollectionPosts(@Path("id") String collectionID, @Query(value = BOOKMARK) String bookmark,
                            @Query(value = "post_type") String post_type,
                            data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.Post>>> cb);

    @GET("/collection/{id}/post/")
    void getCollectionPosts(@Path("id") String collectionID, @Query(value = BOOKMARK) String bookmark,
                            data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.Post>>> cb);

    @GET("/unique_name/{unique_name}/collection_name/{collection_name}/post/")
    void getCollectionPostsByName(@Path("unique_name") String userName, @Path("collection_name") String collectionName,
                                  @Query(value = BOOKMARK) String bookmark,
                                  data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.Post>>> cb);

    /*--POST--*/
    @POST("/collection/{id}/post/")
    void uploadTextPost(@Path("id") String CollectionID, @Body HashMap body, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    /*@POST("/collection/{id}/post/")
    void uploadImagePost(@Path("id") String CollectionID, @Body Post post,
                         @Body TypedFile image, PoinilaCallback<PoinilaResponse> cb);*/
    @Multipart
    @POST("/collection/{id}/post/")
    void uploadImagePost(@Path("id") String CollectionID,
                         /*@Part("title") String title,
                         @Part("caption") String caption,
                         @Part("tags") String tags,*/
                         @Part(ACTION) String action,
                         @Part(DATA) data.model.Post post,
                         @Part(IMAGE) TypedOutput image, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @POST("/collection/{id}/post/")
    void createWebsitePost(@Path("id") String collectionID, @Body HashMap post, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @POST("/collection/{id}/follow/")
    void followCollection(@Path("id") String CollectionID, @Body HashMap emptyBody, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    /*--PUT--*/
    @POST("/collection/{id}/")
    void updateCollectionWithoutCover(@Path("id") String collectionID,
                                      @Body HashMap collectionPacket,
                                      data.PoinilaCallback<data.model.PoinilaResponse<data.model.Collection>> cb);

    @POST("/collection/{id}/coverphoto/")
    void uploadCollectionCoverImage(@Path("id") String collectionID, @Body HashMap image,
                                    data.PoinilaCallback<data.model.PoinilaResponse> cb);
    @Multipart
    @POST("/collection/{id}/")
    void updateCollectionWithCover(@Path("id") String collectionID,
                                   @Part(ACTION) String action,
                                   @Part(DATA) data.model.Collection collection,
                                   @Part(IMAGE) TypedOutput image,
                                   data.PoinilaCallback<data.model.PoinilaResponse<data.model.Collection>> cb);

    /*--DELETE--*/
    @POST("/collection/{id}/")
    void deleteCollection(@Path("id") String collectionID,
                          @Body HashMap actionPacket,
                          data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @POST("/collection/{id}/follow/")
    void unfollowCollection(@Path("id") String collectionID, @Body HashMap emptyBody,
                            data.PoinilaCallback<data.model.PoinilaResponse> cb);


    /*----------CIRCLE-----------*/
    /*DELETE*/
    @POST("/circle/{id}/")
    void deleteCircle(@Path("id") String circleID, @Body HashMap emptyBody, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @POST("/frame/{id}/")
    void deleteFrame(@Path("id") String frameID, @Body HashMap emptyBody, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @POST("/frame/{id}/collection/{collection_id}/")
    void removeCollectionFromFrame(@Path("id") String frameID,
                                   @Path("collection_id") String collectionID,
                                   @Body HashMap emptyPacket,
                                   data.PoinilaCallback<data.model.PoinilaResponse> cb);
    /*PUT*/
    @POST("/circle/{id}/")
    void updateCircle(@Path("id") String circleID, @Body HashMap circleName, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @POST("/frame/{id}/")
    void updateFrame(@Path("id") String frameID, @Body HashMap frameName, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    /*POST*/
    @POST("/circle/")
    void createCircle(@Body HashMap circleName, data.PoinilaCallback<data.model.PoinilaResponse<data.model.Circle>> cb);

    @POST("/frame/{id}/collection/{collection_id}/")
    void addCollectionToFrame(@Path("id") String frameID,
                              @Path("collection_id") String collectionID,
                              @Body HashMap emptyPacket,
                              data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @POST("/frame/")
    void createFrame(@Body HashMap frameName, data.PoinilaCallback<data.model.PoinilaResponse<data.model.Frame>> cb);

    /*--------Notifications-------*/
    @GET("/notification/ownfriendshipinvitations/")
    void getMyFriendshipRequests(@Query(value = BOOKMARK) String bookmark,
                                 data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.InvitationNotif>>> cb);

    @GET("/notification/ownnotifications/")
    void getMyNotifications(@Query(value = BOOKMARK) String bookmark,
                            data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.Notification>>> cb);

    @GET("/notification/othernotifications/")
    void getOthersNotification(@Query(value = BOOKMARK) String bookmark,
                               data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.Notification>>> cb);

    /*----------OTHERS-----------*/
    // first time after loginParams and anytime user wants to set interests;

    @GET("/topic/")
    void getTopics(data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.Topic>>> cb);

    @GET("/currentservertime/")
    void getServerTime(data.PoinilaCallback<data.model.PoinilaResponse<Date>> cb);

    //TODO: methoda doroste? vorudi nemikhan ina?
    @GET("/logout/")
    void logout(data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @GET("/applicationnotificationsettings/")
    void getApplicationNotification(data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.OnOffSetting>>> cb);

    @GET("/emailnotificationsettings/")
    void getEmailNotification(data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.OnOffSetting>>> cb);

    @GET("/interest/")
    void getInterests(data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.ImageTag>>> cb);

    @GET("/interest/{id}/")
    void getSubInterest(@Path("id") String subInterests, data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.ImageTag>>> cb);

    @GET("/invitetopoinila/")
    void getRemainedInvites(data.PoinilaCallback<data.model.PoinilaResponse<data.model.PoinilaInvite>> cb);

    @GET("/websiteinfo/{type}/")
    void getWebsiteInfo(@Path("type") String postType, @Query(value = "url") String address,
                        data.PoinilaCallback<data.model.PoinilaResponse<SuggestedWebPagePost>> cb);

    @GET("/info2/")
    void getMyInfo(data.PoinilaCallback<data.model.PoinilaResponse<data.model.Member>> cb);

    /*--POST---*/
    @POST("/login/")
    void login(@Body HashMap loginParas, Callback<Response> cb);

    @POST("/loginbygoogle/")
    void loginByGoogle(@Body HashMap loginParas, Callback<Response> cb);

    @POST("/applicationnotificationsettings/")
    void setApplicationNotification(@Body HashMap settingParams, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @POST("/emailnotificationsettings/")
    void setEmailNotificationSetting(@Body HashMap settingParams, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @POST("/member/{user_id}/interest/{tag_id}/")
    void removeInterest(@Path("user_id") String userID, @Path("tag_id") String tagID,
                        @Body HashMap emptyBody, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @POST("/forgetpassword/")
    void recoverPassword(@Body HashMap recoveryDest, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @POST("/resetpassword/")
    void resetPassword(@Body HashMap newPassword, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @POST("/verificationcode/")
    void requestVerificationCode(@Body HashMap hashMap, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    /*---put----*/
    @POST("/member/{id}/interest/")
    void updateUserInterests(@Path("id") String memberID, @Body HashMap hashMap, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @POST("/invitetopoinila/")
    void inviteToPoinila(@Body HashMap invitation, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @POST("/report/")
    void report(@Body HashMap body, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @POST("/register/")
    void register(@Body HashMap registerBody, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @GET("/unique_name/validate/")
    void checkUserNameValidity(@Query("unique_name") String tempUserName, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @GET("/explore/{tag_name}/")
    void explore(@Path("tag_name") String tagName, @Query(BOOKMARK) String bookmark, data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.Post>>> cb);

    @GET("/ponilasystempreferences/")
    void getSystemPreferences(data.PoinilaCallback<data.model.PoinilaResponse<data.model.SystemPreferences>> cb);

    @POST("/post/member_id/report")
    void reportMemberOrPost(@Body HashMap memberIdOrPostId,data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @POST("/verifyuser/")
    void verifyPhoneOrEmail(@Body HashMap verificationCode, data.PoinilaCallback<data.model.PoinilaResponse> cb);

    @POST("/suggestedposts/")
    void getSuggestedPosts(@Body HashMap postIds, data.PoinilaCallback<data.model.PoinilaResponse<List<data.model.Post>>> cb);

    @POST("/member/{member_id}/setusernamepassword/")
    void setUserNamePassword(@Path("member_id") String userId, @Body HashMap data, data.PoinilaCallback<data.model.PoinilaResponse> cb);
}

