package data;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.GoogleLoginSucceedEvent;
import com.shaya.poinila.android.presentation.uievent.SuggestionPosts;
import com.shaya.poinila.android.presentation.uievent.UpdateNewPostDialogEvent;
import com.shaya.poinila.android.presentation.uievent.UpdateUiRepostEvent;
import com.shaya.poinila.android.presentation.view.activity.SettingActivity;
import com.shaya.poinila.android.presentation.view.dialog.ForgotPasswordFragment;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.ConnectionUitls;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.ContextHolder;
import com.shaya.poinila.android.util.DeviceInfoUtils;
import com.shaya.poinila.android.util.ImageUtils;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.util.PoinilaPreferences;
import com.shaya.poinila.android.util.StringUtils;
import com.shaya.poinila.android.utils.PonilaAccountManager;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import data.event.AbstractNotificationsReceivedEvent;
import data.event.AnswerFriendRequestResponse;
import data.event.BaseEvent;
import data.event.CircleReceivedEvent;
import data.event.CollectionReceivedEvent;
import data.event.CollectionUpdatedEvent;
import data.event.CollectionsReceivedEvent;
import data.event.CommentReceivedEvent;
import data.event.CommentsReceivedEvent;
import data.event.ContentReceivedEvent;
import data.event.DashboardEvent;
import data.event.FailEvent;
import data.event.FrameReceivedEvent;
import data.event.InterestsReceivedEvent;
import data.event.InviteUsedEvent;
import data.event.LoadingImagedFailedEvent;
import data.event.LoginFailedEvent;
import data.event.LoginSucceedEvent;
import data.event.MemberReceivedEvent;
import data.event.MembersReceivedEvent;
import data.event.ModelCreatedEvent;
import data.event.ModelDeletedEvent;
import data.event.ModelUpdatedEvent;
import data.event.MyFriendshipRequestsEvent;
import data.event.MyInfoReceivedEvent;
import data.event.NotificationSettingsReceivedEvent;
import data.event.PostReceivedEvent;
import data.event.PostsReceivedEvent;
import data.event.ProfileDirtyEvent;
import data.event.ProfileSettingReceivedEvent;
import data.event.RegisterResponseEvent;
import data.event.RemainedInvitesEvent;
import data.event.ServerResponseEvent;
import data.event.SuggestedWebpagePostReceived;
import data.event.SystemPreferencesReceivedEvent;
import data.event.TopicsReceivedEvent;
import data.event.UndoFavePostEvent;
import data.event.UndoUnfavePostEvent;
import data.event.UpdateProfileSettingResponse;
import data.event.UserInterestsReceivedEvent;
import data.event.UserNameValidityEvent;
import data.event.VerificationRequestResponse;
import data.model.Circle;
import data.model.Collection;
import data.model.Comment;
import data.model.Content;
import data.model.DefaultType;
import data.model.Frame;
import data.model.FriendRequestAnswer;
import data.model.Gender;
import data.model.ImageTag;
import data.model.InvitationNotif;
import data.model.Member;
import data.model.Notification;
import data.model.OnOffSetting;
import data.model.PoinilaInvite;
import data.model.PoinilaResponse;
import data.model.Post;
import data.model.PostType;
import data.model.SuggestedWebPagePost;
import data.model.SystemPreferences;
import data.model.Tag;
import data.model.Topic;
import manager.DBFacade;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedOutput;
import static com.shaya.poinila.android.util.ConstantsUtils.CONNECT_TIME_OUT_MILLISECONDS;
import static com.shaya.poinila.android.util.ConstantsUtils.HEADER_AUTH;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_JSON_DATA_ROOT;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_JSON_OWNED_COLLECTIONS;
import static com.shaya.poinila.android.util.ConstantsUtils.POINILA_DATE_FORMAT;
import static com.shaya.poinila.android.util.ConstantsUtils.READ_TIME_OUT_MILLISECONDS;
import static com.shaya.poinila.android.util.ConstantsUtils.WRITE_TIME_OUT_MILLISECONDS;
import static com.shaya.poinila.android.util.ConstantsUtils.SHOULD_SET_INTEREST;
import static com.shaya.poinila.android.util.ImageUtils.convertBitmapToByteArray;
import static com.shaya.poinila.android.util.ResourceUtils.getString;

/**
 * Created by iran on 2015-06-30.
 */
public class PoinilaNetService {
    private static final String MULTIPART_FORMDATA = "multipart/form-data";
    private static final String MYME_TYPE_IMAGE = "image/*";
    public static final String POST = "post";
    public static final String PUT = "put";
    public static final String DELETE = "delete";
    private static RestServices restServices;

    public static Gson gson;
    private static HashSet<String> cookies = new HashSet<>();

    public static Gson getGson() {
        return gson;
    }

    private static final RestAdapter restAdapter;

    private static final GsonConverter gsonConverter;

    private static final OkHttpClient okClient;

    static {
        okClient = new OkHttpClient();
        okClient.networkInterceptors().add(new AddCookiesInterceptor());
        okClient.networkInterceptors().add(new ReceivedCookiesInterceptor());
        okClient.networkInterceptors().add(new AgentAndVersionInterceptor());
        okClient.setConnectTimeout(CONNECT_TIME_OUT_MILLISECONDS, TimeUnit.MILLISECONDS);
        okClient.setReadTimeout(READ_TIME_OUT_MILLISECONDS, TimeUnit.MILLISECONDS);
        okClient.setWriteTimeout(WRITE_TIME_OUT_MILLISECONDS, TimeUnit.MILLISECONDS);

        gson = new GsonBuilder().
                setDateFormat(POINILA_DATE_FORMAT).
                setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        // because our models inherite from DBFlow Model class which have a protected
                        // field of ModelAdapter class. we skip it for the sake of object marshalling.
                        return f.getDeclaredClass().equals(ModelAdapter.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                }).create();

        gsonConverter = new GsonConverter(gson);

        // with removing requestInterceptor in retrofit 2.x this must be done using okhttp interceptor.
        // like adding and updating cookie we do already
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Authorization", PoinilaPreferences.getAuthToken());
                //request.addHeader("");
            }
        };

        //String[] ipPort = StorageUtils.readIpPortFromFile();
        restAdapter = new RestAdapter.Builder().
                //setEndpoint(ConstantsUtils.POINILA_BASE_URL).
                        setEndpoint(ConstantsUtils.POINILA_SERVER_ADDRESS).
                setLogLevel(RestAdapter.LogLevel.NONE). // HEADERS_AND_ARGS).
                setRequestInterceptor(requestInterceptor).
                setClient(new OkClient(okClient)).
                setConverter(gsonConverter).
                //setErrorHandler(new RetrofitErrorHandler()).
                        build();
        restServices = restAdapter.create(RestServices.class);


    }

    public static void setEmailNotificationSetting(OnOffSetting setting) {
        restServices.setEmailNotificationSetting(
                JsonRequestBodyMaker.OnOffSetting(setting.typeID, setting.value).toRequestPacketJsonObject(PUT),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {

                    }
                });
    }

    public static void setApplicationNotificationSetting(OnOffSetting setting) {
        restServices.setApplicationNotification(
                JsonRequestBodyMaker.OnOffSetting(setting.typeID, setting.value).toRequestPacketJsonObject(PUT),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {

                    }

                });
    }

    public static void getMemberInterests(String memberID) {
        restServices.getMemberInterests(memberID, new PoinilaCallback<PoinilaResponse<List<ImageTag>>>() {

            @Override
            public void poinilaSuccess(PoinilaResponse<List<ImageTag>> poinilaResponse, Response response) {
                postEvent(new UserInterestsReceivedEvent(poinilaResponse.data));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<ImageTag>>>() {
                }.getType();
            }
        });

    }

    public static void removeInterest(Tag tag) {
        restServices.removeInterest(PoinilaPreferences.getMyId(), tag.getId(),
                JsonRequestBodyMaker.emptyPacket().toRequestPacketJsonObject(DELETE),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                    }

                });
    }

    public static void getInterests() {
        restServices.getInterests(new PoinilaCallback<PoinilaResponse<List<ImageTag>>>() {

            @Override
            public void poinilaSuccess(PoinilaResponse<List<ImageTag>> listPoinilaResponse, Response response) {
                postEvent(new InterestsReceivedEvent(listPoinilaResponse.data));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<ImageTag>>>() {
                }.getType();
            }
        });
    }

    public static void getSubInterests(final String superInterestID) {
        restServices.getSubInterest(superInterestID, new PoinilaCallback<PoinilaResponse<List<ImageTag>>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<List<ImageTag>> poinilaResponse, Response response) {
                postEvent(new InterestsReceivedEvent(poinilaResponse.data, superInterestID));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<ImageTag>>>() {
                }.getType();
            }
        });

    }

    public static void updateUserInterests(List<Integer> selectedInterests) {
        restServices.updateUserInterests(PoinilaPreferences.getMyId(),
                JsonRequestBodyMaker.userInterests(selectedInterests).toRequestPacketJsonObject(PUT),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                        postEvent(new ServerResponseEvent(true, BaseEvent.ReceiverName.SelectInterest));
                    }

                    @Override
                    public boolean poinilaError(PoinilaResponse poinilaResponse) {
                        postEvent(new ServerResponseEvent(false, BaseEvent.ReceiverName.SelectInterest));
                        return super.poinilaError(poinilaResponse);
                    }
                });
    }

    public static void uploadProfilePicture(Bitmap croppedImage) {

        restServices.uploadProfilePic(PoinilaPreferences.getMyId(), POST,
                new PoinilaTypedByteArray(MYME_TYPE_IMAGE,
                        convertBitmapToByteArray(
                                ImageUtils.resizeBitmapForProfilePic(croppedImage))),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                        postEvent(new ProfileDirtyEvent());
                    }

                });
    }

    public static void getRemainedInvites() {
        restServices.getRemainedInvites(new PoinilaCallback<PoinilaResponse<PoinilaInvite>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<PoinilaInvite> poinilaResponse, Response response) {
                int remained = poinilaResponse.data.limit - poinilaResponse.data.usedInvites;
                postEvent(new RemainedInvitesEvent(remained));
            }

            @Override
            public boolean poinilaError(PoinilaResponse error) {
                postEvent(new RemainedInvitesEvent(0));
                return true;
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<PoinilaInvite>>() {
                }.getType();
            }
        });
    }

    public static void inviteToPoinila(String email, String message) {
        restServices.inviteToPoinila(
                JsonRequestBodyMaker.inviteToPoinila(email, message).toRequestPacketJsonObject(POST),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                        // Order of events are important! first one updates the data layer witch
                        // view invoked by second one uses that. (Otto dispatches the events synchronously.
                        postEvent(new RemainedInvitesEvent(-1)); // -1 for decrementing
                        postEvent(new InviteUsedEvent());
                    }
                });
    }

    public static void getWebsiteInfo(String address, PostType type) {
        String typeString = "";
        switch (type){
            case TEXT:
                typeString = "txt";
                break;
            case IMAGE:
                typeString = "img";
                break;
            case VIDEO:
                typeString = "video";
                break;
        }
        restServices.getWebsiteInfo(typeString, address,
                new PoinilaCallback<PoinilaResponse<SuggestedWebPagePost>>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse<SuggestedWebPagePost> poinilaResponse, Response response) {
                        postEvent(new SuggestedWebpagePostReceived(poinilaResponse.data));
                    }

                    @Override
                    public boolean poinilaError(PoinilaResponse poinilaResponse) {
                        postEvent(new LoadingImagedFailedEvent());
                        return true;
                    }

                    @Override
                    public Type getType() {
                        return new TypeToken<PoinilaResponse<SuggestedWebPagePost>>() {
                        }.getType();
                    }

                });
    }

    public static void createReferencedPost(String collectionID, Post newPost, String siteAddress, String imageAddress, String videoAddress) {
        restServices.createWebsitePost(collectionID,
                JsonRequestBodyMaker.websitePost(newPost, siteAddress, imageAddress, videoAddress).toRequestPacketJsonObject(POST),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                        Logger.toast(R.string.successfully_created);
                    }
                });
    }

    public static void recoverPassword(ForgotPasswordFragment.RECOVERY_PASS_TYPE recoveryPassType, String emailOrPhone) {
        HashMap data = null;
        switch (recoveryPassType){
            case EMAIL:
                data = JsonRequestBodyMaker.email(emailOrPhone).toRequestPacketJsonObject(POST);
                break;
            case MOBILE_NUMBER:
                data = JsonRequestBodyMaker.phoneNumber(emailOrPhone).toRequestPacketJsonObject(POST);
                break;
            case UNIQUE_NAME:
                data = JsonRequestBodyMaker.uniqueName(emailOrPhone).toRequestPacketJsonObject(POST);
                break;

        }


        restServices.recoverPassword(data,
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse ponilaResponse, Response response) {
                        Logger.toast(R.string.successful_recovery);
                        postEvent(new VerificationRequestResponse(true)); // avoiding duplicate events
                    }

                    @Override
                    public boolean poinilaError(PoinilaResponse poinilaResponse) {
                        if (poinilaResponse.code == 446) {
                            postEvent(new VerificationRequestResponse(false, poinilaResponse.code));
                            return true;
                        }
                        return super.poinilaError(poinilaResponse);
                    }
                });
    }

    public static void resetPassword(final String newPassword, String code) {
        restServices.resetPassword(JsonRequestBodyMaker.resetPassword(newPassword, code).toRequestPacketJsonObject(POST),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse ponilaResponse, Response response) {
                        // TODO: go To login page
                        String authToken = getJWTTokenFromHeaders(response);
                        if (authToken != null) {
                            PoinilaPreferences.putAuthToken(authToken);

                            String userName = ((ArrayList<String>)ponilaResponse.data).get(1);
                            PonilaAccountManager.getInstance().addPonilaAccount(
                                    userName, newPassword, authToken
                            );

//                            PonilaAccountManager.getInstance().updatePonilaAccount(newPassword, authToken);

                            postEvent(new LoginSucceedEvent());
                        }/*else{
                            postEvent(new LoginFailedEvent());
                        }*/
                        //Logger.debugToast("password reset successfully but auth token was not set");
                    }

                    @Override
                    public boolean poinilaError(PoinilaResponse poinilaResponse) {
                        postEvent(new LoginFailedEvent(poinilaResponse.code, null));

                        return true;
                    }
                });
    }

    public static void requestVerificationCode(final boolean byEmail, final String emailOrPhone) {
        restServices.requestVerificationCode(byEmail ?
                        JsonRequestBodyMaker.email(emailOrPhone).toRequestPacketJsonObject(POST) :
                        JsonRequestBodyMaker.phoneNumber(emailOrPhone).toRequestPacketJsonObject(POST),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse ponilaResponse, Response response) {
                        postEvent(new VerificationRequestResponse(true, byEmail, emailOrPhone));
                    }

                    @Override
                    public boolean poinilaError(PoinilaResponse poinilaResponse) {
                        switch (poinilaResponse.code) {
                            case 425:
                                postEvent(new VerificationRequestResponse(false,
                                        getString(byEmail ?
                                                R.string.error_already_registered_email :
                                                R.string.error_already_registered_phone)));
                                break;
                            case 423:
                                postEvent(new VerificationRequestResponse(false, getString(R.string.error_invalid_phone_no)));
                                break;
                        }
                        return true;
                    }
                });
    }

    public static void requestVerificationCode(final boolean byEmail, final String emailOrPhone, int memberId) {
        restServices.requestVerificationCode(
                JsonRequestBodyMaker.requestVerify(byEmail, emailOrPhone, memberId).toRequestPacketJsonObject(POST),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse ponilaResponse, Response response) {
                        postEvent(new VerificationRequestResponse(true, byEmail, emailOrPhone));
                    }

                    @Override
                    public boolean poinilaError(PoinilaResponse poinilaResponse) {
                        switch (poinilaResponse.code) {
                            case 425:
                                postEvent(new VerificationRequestResponse(false,
                                        getString(byEmail ?
                                                R.string.error_already_registered_email :
                                                R.string.error_already_registered_phone)));
                                break;
                            case 423:
                                postEvent(new VerificationRequestResponse(false, getString(R.string.error_invalid_phone_no)));
                                break;
                        }
                        return true;
                    }
                });
    }

    public static void register(String verificationCode, String fullName, final String userName, Gender gender, final String password, boolean byEmail, String emailOrPhone) {
        restServices.register(JsonRequestBodyMaker.register(
                        verificationCode, fullName, userName, gender.name().toLowerCase(), password, byEmail ? emailOrPhone : "", !byEmail ? emailOrPhone : "").
                        toRequestPacketJsonObject(POST),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse ponilaResponse, Response response) {
                        /*postEvent(new RegisterResponseEvent(true));*/
                        String authToken = getJWTTokenFromHeaders(response);
                        PoinilaPreferences.putAuthToken(authToken);
                        PonilaAccountManager.getInstance().addPonilaAccount(
                                userName, password, authToken
                        );
//                        PonilaAccountManager.getInstance().
                        postEvent(new RegisterResponseEvent(true));
                    }

                    @Override
                    public boolean poinilaError(PoinilaResponse poinilaResponse) {
                        if (poinilaResponse.code == ConstantsUtils.CODE_NO_OCCURRENCE)
                            postEvent(new RegisterResponseEvent(false, RegisterResponseEvent.USED_VERIFICATION_CODE));
                        return true;
                    }
                });
    }

    public static void checkUserNameValidity(final String tempUserName) {
        restServices.checkUserNameValidity(tempUserName, new PoinilaCallback<PoinilaResponse>() {
            @Override
            public void poinilaSuccess(PoinilaResponse ponilaResponse, Response response) {
                //postEvent(new UserNameValidityEvent(true));
            }

            @Override
            public boolean poinilaError(PoinilaResponse poinilaResponse) {
                int error = 0;
                switch (poinilaResponse.code) {
                    case ConstantsUtils.ERROR_DUPLICATE:
                        error = UserNameValidityEvent.DUPLICATE;
                        break;
                    case ConstantsUtils.ERROR_BAD_KEYWORD:
                        error = UserNameValidityEvent.RESERVED;
                        break;
                    case ConstantsUtils.ERROR_RULE_EXCEPTION:
                        error = (tempUserName.length() < 6 || tempUserName.length() > 32) ? UserNameValidityEvent.LENGTH : UserNameValidityEvent.RULE;
                        break;
                }
                postEvent(new UserNameValidityEvent(false, error));
                return true;
            }
        });
    }

    public static void login(final String uniqueName, final String email, final String password) {
        restServices.login(JsonRequestBodyMaker.loginParams(uniqueName, email, password).toRequestPacketJsonObject(POST),
                new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        PoinilaResponse poinilaResponse = gson.fromJson(getStringFromResponse(response), PoinilaResponse.class);
                        if (poinilaResponse.code == 200) {
                            String authToken = getJWTTokenFromHeaders(response);
                            PoinilaPreferences.putAuthToken(authToken);
                            PonilaAccountManager.getInstance().addPonilaAccount(
                                    uniqueName != null ? uniqueName : email, password, authToken
                            );
                            postEvent(new LoginSucceedEvent());
                            //} else if (poinilaResponse.code == 401) {
                        } else if (poinilaResponse.code == 401) {
                            JsonObject responseJson = new JsonParser().parse(getStringFromResponse(response)).getAsJsonObject();
                            JsonObject dataJson = responseJson.get(KEY_JSON_DATA_ROOT).getAsJsonObject();
                            /*if (params.has("parameter")){
                                params.get("parameter");
                            }*/
                            postEvent(new LoginFailedEvent(poinilaResponse.code, dataJson));
                        } else {
                            failure(null);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        postEvent(new LoginFailedEvent(0, null));
                        Logger.toast(R.string.error_sth_bad_happened);
                    }
                });
    }

    public static void loginByGoogle(String tokenId){
        restServices.loginByGoogle(JsonRequestBodyMaker.loginByGoogleParams(tokenId).toRequestPacketJsonObject(POST),
                new Callback<Response>() {

                    @Override
                    public void success(Response response, Response response2) {
                        PoinilaResponse poinilaResponse = gson.fromJson(getStringFromResponse(response), PoinilaResponse.class);
                        if (poinilaResponse.code == 200) {
                            String authToken = getJWTTokenFromHeaders(response);

//                            Log.i(getClass().getName(), "poinilaResponse = " + poinilaResponse.data);

                            GoogleLoginSucceedEvent loginEvent = new GoogleLoginSucceedEvent();
                            try {
                                loginEvent.firstLoginDoneByGoogle = new JSONObject(
                                        poinilaResponse.data.toString()).optBoolean(SHOULD_SET_INTEREST);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(PonilaAccountManager.getInstance().getGoogleAccount() != null){
                                PonilaAccountManager.getInstance().addPonilaAccountFromGoogle(authToken);
                                postEvent(loginEvent);
                            }else
                                Logger.toast(R.string.error_google_account_not_found);

                        }else if (poinilaResponse.code == 401) {
                            JsonObject responseJson = new JsonParser().parse(getStringFromResponse(response)).getAsJsonObject();
                            JsonObject dataJson = responseJson.get(KEY_JSON_DATA_ROOT).getAsJsonObject();
                            /*if (params.has("parameter")){
                                params.get("parameter");
                            }*/
                            postEvent(new LoginFailedEvent(poinilaResponse.code, dataJson));
                        }else {
                            failure(null);
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Logger.toast(R.string.error_sth_bad_happened);
                    }
                });
    }

    private static String getJWTTokenFromHeaders(Response response) {
        for (Header header : response.getHeaders()) {
            if (header.getName().equals(HEADER_AUTH))
                return header.getValue();
        }
        return null;
    }

    public static void logout() {
        if (ConnectionUitls.isNetworkOnline()) { // hameye requesta bayad to "ye noghte" az hamchin logici obur konan
            restServices.logout(new PoinilaCallback<PoinilaResponse>() {
                @Override
                public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                }
            });
        }
    }

    /**
     * @param bookmark passing null value ignores the query parameter
     */
    public static void getSuggestions(final String bookmark) {
        restServices.getSuggestions(bookmark, new PoinilaCallback<PoinilaResponse<List<Post>>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<List<Post>> poinilaResponse, Response response) {
                for (int i = 0; i < poinilaResponse.data.size(); i++) {
                    Post post = poinilaResponse.data.get(i);
                    post.jsonContent = getDataList().get(i).toString();
                }
                postEvent(new DashboardEvent(poinilaResponse.data, false, poinilaResponse.bookmark));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<Post>>>() {
                }.getType();
            }
        });
    }


    public static void getFollowedCollections(String memberId, String frameID, String bookmark,
                                              final BaseEvent.ReceiverName receiverTag) {
        restServices.getMemberFollowingCollections(memberId, bookmark, frameID,
                new PoinilaCallback<PoinilaResponse<List<Collection>>>() {

                    @Override
                    public void poinilaSuccess(PoinilaResponse<List<Collection>> collections, Response response) {
                        postEvent(new CollectionsReceivedEvent(collections.data,
                                collections.bookmark, receiverTag));
                    }

                    @Override
                    public Type getType() {
                        return new TypeToken<PoinilaResponse<List<Collection>>>() {
                        }.getType();
                    }
                });
    }

    public static void searchPostWithQuery(List<String> query, String bookmark) {
        restServices.getPostsWithQuery(query, bookmark, new PoinilaCallback<PoinilaResponse<List<Post>>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<List<Post>> listPoinilaResponse, Response response) {
                postEvent(new PostsReceivedEvent(listPoinilaResponse.data,
                        listPoinilaResponse.bookmark, BaseEvent.ReceiverName.SearchFragment));
            }


            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<Post>>>() {
                }.getType();
            }
        });
    }

    public static void searchCollectionsWithQuery(List<String> query, String bookmark) {
        restServices.getCollectionsWithQuery(query, bookmark, new PoinilaCallback<PoinilaResponse<List<Collection>>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<List<Collection>> listPoinilaResponse, Response response) {
                postEvent(new CollectionsReceivedEvent(listPoinilaResponse.data,
                        listPoinilaResponse.bookmark, BaseEvent.ReceiverName.SearchFragment));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<Collection>>>() {
                }.getType();
            }
        });
    }

    public static void searchMembersWithQuery(List<String> query, String bookmark) {
        restServices.getMembersWithQuery(query, bookmark, new PoinilaCallback<PoinilaResponse<List<Member>>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<List<Member>> listPoinilaResponse, Response response) {
                postEvent(new MembersReceivedEvent(listPoinilaResponse.data,
                        listPoinilaResponse.bookmark));
                //, ReceiverName.SearchFragment));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<Member>>>() {
                }.getType();
            }
        });
    }

    public static void getMemberProfile(String profileID) {
        PoinilaCallback<PoinilaResponse<Member>> cb = new PoinilaCallback<PoinilaResponse<Member>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<Member> memberPoinilaResponse, Response response) {
                postEvent(new MemberReceivedEvent(memberPoinilaResponse.data));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<Member>>() {
                }.getType();
            }
        };
        if (StringUtils.isInteger(profileID))
            restServices.getProfileById(profileID, cb);
        else
            restServices.getProfileByUserName(profileID, cb);
    }

    public static void favePost(final String postID) {
        restServices.favePost(postID,
                JsonRequestBodyMaker.emptyPacket().toRequestPacketJsonObject(POST),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                        if (poinilaResponse.code != ConstantsUtils.CODE_SUCCESS)
                            postEvent(new UndoFavePostEvent());
                    }

                    @Override
                    public boolean poinilaError(PoinilaResponse error) {
                        postEvent(new UndoFavePostEvent());
                        return true;
                    }
                });
    }

    public static void unfavePost(final String postID) {
        restServices.unfavePost(postID,
                JsonRequestBodyMaker.emptyPacket().toRequestPacketJsonObject(DELETE),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                        if (poinilaResponse.code != ConstantsUtils.CODE_SUCCESS)
                            postEvent(new UndoUnfavePostEvent());
                    }

                    @Override
                    public boolean poinilaError(PoinilaResponse error) {
                        postEvent(new UndoUnfavePostEvent());
                        return true;
                    }
                });
    }

    public static void getPostContent(final String contentUrl, final int postID) {
        Request request = new Request.Builder().url(contentUrl)
                .addHeader("Origin", ConstantsUtils.POINILA_ORIGIN_ADDRESS)
                .build();
        okClient.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
            }

            @Override
            public void onResponse(final com.squareup.okhttp.Response response) throws IOException {
                String content = response.body().string();
                postEvent(new ContentReceivedEvent(content, postID));
                postEvent(new ModelCreatedEvent(new Content(contentUrl, content)));
            }
        });
    }

    public static void getMemberFriends(String memberID, String bookmark) {
        restServices.getMemberFriends(memberID, bookmark, new PoinilaCallback<PoinilaResponse<List<Member>>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<List<Member>> listPoinilaResponse, Response response) {
                postEvent(new MembersReceivedEvent(listPoinilaResponse.data,
                        listPoinilaResponse.bookmark));
                //, ReceiverName.MemberListFragment));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<Member>>>() {
                }.getType();
            }
        });
    }

    public static void getRelatedPosts(String postID, String bookmark, final int requestId) {
        restServices.getRelatedPosts(postID, bookmark, new PoinilaCallback<PoinilaResponse<List<Post>>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<List<Post>> listPoinilaResponse, Response response) {
                postEvent(new PostsReceivedEvent(
                        listPoinilaResponse.data, listPoinilaResponse.bookmark,
                        BaseEvent.ReceiverName.PostRelatedPosts, requestId
                ));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<Post>>>() {
                }.getType();
            }
        });
    }

    public static void getPostComments(String postID, String bookmark) {
        restServices.getPostComments(postID, bookmark, new PoinilaCallback<PoinilaResponse<List<Comment>>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<List<Comment>> listPoinilaResponse, Response response) {
                postEvent(new CommentsReceivedEvent(listPoinilaResponse.data, listPoinilaResponse.bookmark));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<Comment>>>() {
                }.getType();
            }
        });
    }

    public static void getRepostCollections(String postID, String bookmark) {
        restServices.getRepostCollections(postID, bookmark, new PoinilaCallback<PoinilaResponse<List<Collection>>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<List<Collection>> listPoinilaResponse, Response response) {
                postEvent(new CollectionsReceivedEvent(listPoinilaResponse.data,
                        listPoinilaResponse.bookmark, BaseEvent.ReceiverName.CollectionListFragment));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<Collection>>>() {
                }.getType();
            }
        });
    }

    public static void getPostLikers(String postID, String bookmark) {
        restServices.getPostLikers(postID, bookmark, new PoinilaCallback<PoinilaResponse<List<Member>>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<List<Member>> listPoinilaResponse, Response response) {
                postEvent(new MembersReceivedEvent(listPoinilaResponse.data,
                        listPoinilaResponse.bookmark));
                //, ReceiverName.MemberListFragment));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<Member>>>() {
                }.getType();
            }
        });
    }

    public static void getFavedPostByMember(String memberID, String bookmark) {
        restServices.getMemberLikedPosts(memberID, bookmark, new PoinilaCallback<PoinilaResponse<List<Post>>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<List<Post>> listPoinilaResponse, Response response) {
                postEvent(new PostsReceivedEvent(listPoinilaResponse.data,
                        listPoinilaResponse.bookmark, BaseEvent.ReceiverName.PostListFragment));
            }


            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<Post>>>() {
                }.getType();
            }
        });
    }

    public static void deleteComment(String commentID) {
        restServices.deleteComment(commentID,
                JsonRequestBodyMaker.emptyPacket().toRequestPacketJsonObject(DELETE),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                        if (poinilaResponse.code != ConstantsUtils.CODE_SUCCESS) {
                            //TODO
                        }
                    }
                });
    }

    /**
     * {@code repostCaption} is the post {@code summary}
     *
     * @param collectionID
     * @param postID
     * @param repostCaption
     * @param tags
     */
    public static void repost(String collectionID, final int postID, String repostCaption, List<Tag> tags) {
        restServices.repost(collectionID,
                JsonRequestBodyMaker.repost(postID, repostCaption, tags).toRequestPacketJsonObject(POST)
                , new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                        Logger.toast(R.string.successfully_created);
                    }

                    @Override
                    public boolean poinilaError(PoinilaResponse poinilaResponse) {


                        Logger.toast(R.string.error_repost);

                        BusProvider.getSyncUIBus().post(new UpdateUiRepostEvent(postID, false));

                        return super.poinilaError(poinilaResponse);
                    }
                });
    }

    public static void commentOnPost(final String postID, String comment) {
        restServices.commentOnPost(postID, JsonRequestBodyMaker.commentOnPost(comment).toRequestPacketJsonObject(POST)
                , new PoinilaCallback<PoinilaResponse<Comment>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<Comment> commentPoinilaResponse, Response response) {
                postEvent(new CommentReceivedEvent(commentPoinilaResponse.data, postID));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<Comment>>() {
                }.getType();
            }
        });
    }

    public static void getMemberPosts(String memberID, String bookmark) {

        restServices.getMemberPosts(memberID, bookmark, new PoinilaCallback<PoinilaResponse<List<Post>>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<List<Post>> listPoinilaResponse, Response response) {
                postEvent(new PostsReceivedEvent(listPoinilaResponse.data,
                        listPoinilaResponse.bookmark, BaseEvent.ReceiverName.PostListFragment));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<Post>>>() {
                }.getType();
            }

        });

    }

    public static void getMemberFollowers(String memberID, String bookmark) {
        restServices.getMemberFollowers(memberID, bookmark, new PoinilaCallback<PoinilaResponse<List<Member>>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<List<Member>> listPoinilaResponse, Response response) {
                postEvent(new MembersReceivedEvent(listPoinilaResponse.data,
                        listPoinilaResponse.bookmark));
                //, ReceiverName.MemberListFragment));
            }


            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<Member>>>() {
                }.getType();
            }
        });
    }


    public static void getMemberCollections(String memberID, String bookmark) {
        restServices.getMemberCollections(memberID, bookmark, new PoinilaCallback<PoinilaResponse<List<Collection>>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<List<Collection>> listPoinilaResponse, Response response) {
                postEvent(new CollectionsReceivedEvent(listPoinilaResponse.data,
                        listPoinilaResponse.bookmark, BaseEvent.ReceiverName.CollectionListFragment));
            }


            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<Collection>>>() {
                }.getType();
            }
        });
    }


    /*public static void updateProfile(String memberId, String firstName, String lastName,
                                     String email, String password, String oldPassword, boolean isActive) {
        restServices.updateProfile(memberId,
                JsonRequestBodyMaker.updateProfile(firstName, lastName, password, oldPassword, email, isActive),*/

    public static void answerFriendRequest(int memberID, final FriendRequestAnswer answer, int circleID) {
        restServices.answerFriendRequest(PoinilaPreferences.getMyId(),
                JsonRequestBodyMaker.answerFriendRequest(answer, memberID, circleID).toRequestPacketJsonObject(PUT),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                        postEvent(new AnswerFriendRequestResponse(true, answer));
                    }

    /*                @Override
                    public void poinilaError(PoinilaResponse error) {
                        //postEvent(new AnswerFriendRequestResponse());
                    }*/
                });
    }

    public static void changeFriendCircle(final List<Integer> circleIDs, final int friendId) {
        restServices.updateFriendCircles(PoinilaPreferences.getMyId(),
                JsonRequestBodyMaker.updateFriendCircles(circleIDs, friendId).toRequestPacketJsonObject(PUT),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                        if (poinilaResponse.code != ConstantsUtils.CODE_SUCCESS) {
                            //postEvent(new FriendCircleNotChangedEvent(circleIDs, friendId));
                        }
                    }
                });
    }

    public static void friendRequest(String memberID, int publicCircleID) {
        restServices.friendRequest(memberID,
                JsonRequestBodyMaker.friendRequest(
                        Collections.singletonList(publicCircleID)).toRequestPacketJsonObject(POST), //new ArrayList<>(Arrays.asList(circleID))
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                    }

                    @Override
                    public boolean poinilaError(PoinilaResponse poinilaResponse) {
                        return true; // just to avoid default toast error
                    }
                });
    }

    public static void createCollection(String memberID, Collection collection, Bitmap bitmap) { //String imageAddress) {
        //if (imageAddress == null){
        if (bitmap == null) {
            restServices.createCollectionWithoutImage(memberID,
                    JsonRequestBodyMaker.createCollection(collection).toRequestPacketJsonObject(POST),
                    new PoinilaCallback<PoinilaResponse<Collection>>() {
                        @Override
                        public void poinilaSuccess(PoinilaResponse<Collection> poinilaResponse, Response response) {
                            Logger.toast(R.string.successfully_created);
                            poinilaResponse.data.jsonContent = getDataList().get(0).toString();
                            postEvent(new ModelCreatedEvent(poinilaResponse.data));
                            postEvent(new ProfileDirtyEvent());
                            postEvent(new UpdateNewPostDialogEvent(poinilaResponse.data));
                        }

                        @Override
                        public boolean poinilaError(PoinilaResponse poinilaResponse) {
                            if (poinilaResponse.code == ConstantsUtils.CODE_DUPLICATE) {
                                Logger.toast(R.string.error_duplication_collection_name);
                                return true;
                            }
                            return false;
                        }

                        @Override
                        public Type getType() {
                            return new TypeToken<PoinilaResponse<Collection>>() {
                            }.getType();
                        }

                    });
        } else {
            restServices.createCollectionWithImage(memberID, POST, collection,
                    //new TypedFile(MULTIPART_FORMDATA, new File(imageAddress)),
                    new PoinilaTypedByteArray(MYME_TYPE_IMAGE, convertBitmapToByteArray(
                            ImageUtils.resizeBitmapForCollectionCover(bitmap))),
                    new PoinilaCallback<PoinilaResponse<Collection>>() {
                        @Override
                        public void poinilaSuccess(PoinilaResponse<Collection> poinilaResponse, Response response) {
                            Logger.toast(R.string.successfully_created);
                            poinilaResponse.data.jsonContent = getDataList().get(0).toString();
                            postEvent(new ModelCreatedEvent(poinilaResponse.data));
                            postEvent(new ProfileDirtyEvent());
                        }

                        @Override
                        public boolean poinilaError(PoinilaResponse poinilaResponse) {
                            if (poinilaResponse.code == ConstantsUtils.CODE_DUPLICATE) {
                                Logger.toast(R.string.error_duplication_collection_name);
                                return true;
                            }
                            return false;
                        }

                        @Override
                        public Type getType() {
                            return new TypeToken<PoinilaResponse<Collection>>() {
                            }.getType();
                        }
                    });
        }
    }

    // TODO: separate updates having image with not having ones
    public static void updateCollection(String collectionID, Collection collection, Bitmap bitmap) {
        if (bitmap == null) {
            restServices.updateCollectionWithoutCover(collectionID,
                    JsonRequestBodyMaker.createCollection(collection).toRequestPacketJsonObject(PUT),
                    new PoinilaCallback<PoinilaResponse<Collection>>() {
                        @Override
                        public void poinilaSuccess(PoinilaResponse<Collection> poinilaResponse, Response response) {
                            Logger.toast(R.string.successfully_updated);
                            poinilaResponse.data.jsonContent = getDataList().get(0).toString();
                            postEvent(new ModelUpdatedEvent(poinilaResponse.data));
                            postEvent(new CollectionUpdatedEvent(poinilaResponse.data));
                        }

                        @Override
                        public Type getType() {
                            return new TypeToken<PoinilaResponse<Collection>>() {
                            }.getType();
                        }
                    });
        } else {
            restServices.updateCollectionWithCover(collectionID, PUT, collection,
                    new PoinilaTypedByteArray(MYME_TYPE_IMAGE, convertBitmapToByteArray(
                            ImageUtils.resizeBitmapForCollectionCover(bitmap))),
                    new PoinilaCallback<PoinilaResponse<Collection>>() {
                        @Override
                        public void poinilaSuccess(PoinilaResponse<Collection> poinilaResponse, Response response) {
                            Logger.toast(R.string.successfully_updated);
                            poinilaResponse.data.jsonContent = getDataList().get(0).toString();
                            postEvent(new ModelUpdatedEvent(poinilaResponse.data));
                            postEvent(new CollectionReceivedEvent(poinilaResponse.data));
                        }

                        @Override
                        public Type getType() {
                            return new TypeToken<PoinilaResponse<Collection>>() {
                            }.getType();
                        }
                    });
        }
    }

    public static void removeFriend(String friendID) {
        restServices.removeFriend(PoinilaPreferences.getMyId(), friendID,
                JsonRequestBodyMaker.emptyPacket().toRequestPacketJsonObject(DELETE),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                    }

                    @Override
                    public boolean poinilaError(PoinilaResponse poinilaResponse) {
                        return true; // just to avoid default toast error
                    }
                });
    }

    /**
     * fetches collection post either by collection id or combination of collection name and user name
     *
     * @param collectionIdOrName if is collection id, user is useless
     * @param userName           if first parameter is collection name it's mandatory to pass a valid user name here
     * @param bookmark
     * @param receiverName
     */
    public static void getCollectionPosts(String collectionIdOrName, @Nullable String userName,
                                          String bookmark, final BaseEvent.ReceiverName receiverName, boolean justImages) {
        PoinilaCallback<PoinilaResponse<List<Post>>> cb = new PoinilaCallback<PoinilaResponse<List<Post>>>() {

            @Override
            public void poinilaSuccess(PoinilaResponse<List<Post>> listPoinilaResponse, Response response) {
                postEvent(new PostsReceivedEvent(listPoinilaResponse.data,
                        listPoinilaResponse.bookmark, receiverName));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<Post>>>() {
                }.getType();
            }

        };
        if (userName != null)
            restServices.getCollectionPostsByName(userName, collectionIdOrName, bookmark, cb);
        else{
            if(justImages)
                restServices.getCollectionPosts(collectionIdOrName, bookmark, "img", cb);
            else
                restServices.getCollectionPosts(collectionIdOrName, bookmark, cb);
        }
    }

    public static void createTextPost(String collectionID, Post post) {
        restServices.uploadTextPost(collectionID,
                JsonRequestBodyMaker.createTextPost(post).toRequestPacketJsonObject(POST),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                        Logger.toast(R.string.successfully_created);
                        postEvent(new ProfileDirtyEvent());
                    }
                });
    }


    public static void createImagePost(String collectionID, Bitmap image, Post newPost) {
        imagedPost(collectionID, new PoinilaTypedByteArray(MYME_TYPE_IMAGE,
                convertBitmapToByteArray(image)), newPost);
    }

    /*public static void createImagePostFromFile(String collectionID, String imageAddress, Post post) {
        imagedPost(collectionID, new PoinilaTypedByteArray(MYME_TYPE_IMAGE,
                convertBitmapToByteArray(ImageUtils.loadBitmapScaledToUpload(imageAddress))), post);
    }

    public static void createImagePostFromContentUri(String collectionID, Uri uri, Post post) {
        imagedPost(collectionID, new PoinilaTypedByteArray(MYME_TYPE_IMAGE,
                convertBitmapToByteArray(ImageUtils.loadBitmapScaledToUpload(uri))), post);
    }*/

    private static void imagedPost(String collectionID, TypedOutput image, Post post) {
        restServices.uploadImagePost(collectionID, POST, post, image,
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                        Logger.toast(R.string.successfully_created);
                        postEvent(new ProfileDirtyEvent());
                    }
                });
    }

    public static void deleteCollection(final Collection collection) {
        restServices.deleteCollection(collection.getId(),
                JsonRequestBodyMaker.emptyPacket().toRequestPacketJsonObject(DELETE),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                        Logger.toast(R.string.successfully_deleted);
                        postEvent(new ModelDeletedEvent(collection));
                    }
                });
    }

    public static void followCollection(String collectionID) {
        restServices.followCollection(collectionID,
                JsonRequestBodyMaker.emptyPacket().toRequestPacketJsonObject(POST),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                    }

                    //TODO: kasif! tu search age beri collection follow koni bargardi update nemishe
                    // bad request mizane error default midim!
                    @Override
                    public boolean poinilaError(PoinilaResponse poinilaResponse) {
                        return true;
                    }
                });
    }

    public static void unfollowCollection(String collectionID) {
        restServices.unfollowCollection(collectionID,
                JsonRequestBodyMaker.emptyPacket().toRequestPacketJsonObject(DELETE),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                    }

                    //TODO: kasif! tu search age beri collection follow koni bargardi update nemishe
                    // bad request mizane error default midim!
                    @Override
                    public boolean poinilaError(PoinilaResponse poinilaResponse) {
                        return true;
                    }
                });
    }

    public static void updateCircle(final Circle circle) {
        restServices.updateCircle(circle.getId(),
                JsonRequestBodyMaker.createCircle(circle.name).toRequestPacketJsonObject(PUT)
                , new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                        postEvent(new ModelUpdatedEvent(circle));
                    }
                });
    }

    public static void deleteCircle(final Circle circle) {
        restServices.deleteCircle(circle.getId(),
                JsonRequestBodyMaker.emptyPacket().toRequestPacketJsonObject(DELETE),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                        postEvent(new ModelDeletedEvent(circle));
                    }
                });
    }

    public static void createCircle(String circleName) {
        restServices.createCircle(JsonRequestBodyMaker.createCircle(circleName).toRequestPacketJsonObject(POST),
                new PoinilaCallback<PoinilaResponse<Circle>>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse<Circle> circlePoinilaResponse, Response response) {
                        Circle newCircle = circlePoinilaResponse.data;
                        newCircle.defaultType = DefaultType.NOT_DEFAULT;
                        postEvent(new ModelCreatedEvent(newCircle));
                        postEvent(new CircleReceivedEvent(circlePoinilaResponse.data));
                    }

                    @Override
                    public boolean poinilaError(PoinilaResponse error) {
                        postEvent(new FailEvent(RequestType.CREATE_CIRCLE));
                        return true;
                    }

                    @Override
                    public Type getType() {
                        return new TypeToken<PoinilaResponse<Circle>>() {
                        }.getType();
                    }
                });
    }

    public static void updateFrame(final Frame frame) {
        restServices.updateFrame(frame.getId(),
                JsonRequestBodyMaker.createCircle(frame.name).toRequestPacketJsonObject(PUT),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                        postEvent(new ModelUpdatedEvent(frame));
                    }
                });
    }

    public static void deleteFrame(final Frame frame) {
        restServices.deleteFrame(frame.getId(),
                JsonRequestBodyMaker.emptyPacket().toRequestPacketJsonObject(DELETE),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                        postEvent(new ModelDeletedEvent(frame));
                    }
                });
    }

    public static void createFrame(final String frameName) {
        restServices.createFrame(JsonRequestBodyMaker.createCircle(frameName).toRequestPacketJsonObject(POST),
                new PoinilaCallback<PoinilaResponse<Frame>>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse<Frame> framePoinilaResponse, Response response) {
                        postEvent(new ModelCreatedEvent(framePoinilaResponse.data));
                        postEvent(new FrameReceivedEvent(framePoinilaResponse.data));
                    }

                    @Override
                    public Type getType() {
                        return new TypeToken<PoinilaResponse<Frame>>() {
                        }.getType();
                    }
                });
    }

    public static void addCollectionToFrame(String frameID, String collectionID) {
        restServices.addCollectionToFrame(frameID, collectionID,
                JsonRequestBodyMaker.emptyPacket().toRequestPacketJsonObject(POST),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {

                    }
                });
    }

    public static void removeCollectionFromFrame(String frameID, String collectionID) {
        restServices.removeCollectionFromFrame(frameID, collectionID,
                JsonRequestBodyMaker.emptyPacket().toRequestPacketJsonObject(DELETE),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {

                    }
                });
    }

    public static void getProfileSettings() {
        restServices.getProfileSettings(PoinilaPreferences.getMyId(), "people",
                new PoinilaCallback<PoinilaResponse<Member>>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse<Member> memberPoinilaResponse, Response response) {
                        postEvent(new ProfileSettingReceivedEvent(memberPoinilaResponse.data));
                    }

                    @Override
                    public Type getType() {
                        return new TypeToken<PoinilaResponse<Member>>() {
                        }.getType();
                    }
                });
    }

    public static void updateProfileSetting(Member profile, final SettingActivity.SettingType settingType) {
        restServices.updateProfile(PoinilaPreferences.getMyId(),
                JsonRequestBodyMaker.updateProfile(profile).toRequestPacketJsonObject(PUT),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                        postEvent(new UpdateProfileSettingResponse(true, settingType));
                    }

                    @Override
                    public boolean poinilaError(PoinilaResponse poinilaResponse) {
                        postEvent(new UpdateProfileSettingResponse(false, settingType));
                        return false;
                    }
                });
    }

    public static void changePassword(String newPassword, String oldPassword) {
        restServices.changePassword(PoinilaPreferences.getMyId(),
                JsonRequestBodyMaker.changePassword(newPassword, oldPassword).toRequestPacketJsonObject(PUT),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                        Logger.toast(R.string.successfully_updated);
                    }

                    @Override
                    public boolean poinilaError(PoinilaResponse poinilaResponse) {
                        if (poinilaResponse.code == 447) { // old password wrong
                            postEvent(new ServerResponseEvent(false, BaseEvent.ReceiverName.ChangePassword, poinilaResponse.code));
                            return true;
                        }
                        return super.poinilaError(poinilaResponse);
                    }
                });
    }

    public static void getMyInfo(final MyInfoReceivedEvent.MY_INFO_TYPE type) {
        restServices.getMyInfo(new PoinilaCallback<PoinilaResponse<Member>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<Member> ponilaResponse, Response response) {
                // must be analyzed in detail. not sure it does't do any harm
                String authToken = getJWTTokenFromHeaders(response);
//                PoinilaPreferences.putAuthToken(authToken);

                PonilaAccountManager.getInstance().updatePonilaAccount(null, authToken);

                Member member = ponilaResponse.data;
                member.jsonContent = getDataList().get(0).toString();

                JsonElement collectionsJsonElement = getDataList().get(0).getAsJsonObject().get(KEY_JSON_OWNED_COLLECTIONS);
                if (collectionsJsonElement != null && !member.isAnonymous) { // in anonymous login
                    JsonArray collectionsElements = collectionsJsonElement.getAsJsonArray();
                    for (int i = 0; i < collectionsElements.size(); i++) {
                        member.owningCollections.get(i).jsonContent = collectionsElements.get(i).toString();
                    }
                }

                postEvent(new MyInfoReceivedEvent(ponilaResponse.data, false, type));

            }

            @Override
            public boolean poinilaError(PoinilaResponse poinilaResponse) {
                if (poinilaResponse.code == 401) {
                    postEvent(new MyInfoReceivedEvent(null, false, type));
                    return true;
                }
                return super.poinilaError(poinilaResponse);
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<Member>>() {
                }.getType();
            }
        });
    }


    public static void getMyFriendshipRequests(String bookmark) {
        restServices.getMyFriendshipRequests(bookmark, new PoinilaCallback<PoinilaResponse<List<InvitationNotif>>>() {

            @Override
            public void poinilaSuccess(PoinilaResponse<List<InvitationNotif>> listPoinilaResponse, Response response) {
                postEvent(new MyFriendshipRequestsEvent(listPoinilaResponse.data, listPoinilaResponse.bookmark));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<InvitationNotif>>>() {
                }.getType();
            }
        });
    }

   /* public static void getMyAcceptedFriendships(String bookmark){
        restServices.getMyAcceptedFriendships(bookmark, new PoinilaCallback<PoinilaResponse<List<AcceptNotif>>>() {

            @Override
            public void poinilaSuccess(PoinilaResponse<List<AcceptNotif>> listPoinilaResponse, Response response) {
                postEvent(new AcceptedInvitationsEvent(listPoinilaResponse.data, listPoinilaResponse.bookmark));
            }

            @Override
            public void poinilaError(PoinilaResponse error) {
                Logger.toast("get accept error: " + error.getMessage());
            }
        });
    }*/

    public static void getMyNotifications(String bookmark) {
        restServices.getMyNotifications(bookmark, new PoinilaCallback<PoinilaResponse<List<Notification>>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<List<Notification>> listPoinilaResponse, Response response) {
                //removing notifications related to sharing a collection between peoples
                for (int i = listPoinilaResponse.data.size() - 1; i >= 0; i--) {
                    if (listPoinilaResponse.data.get(i).type == null)
                        listPoinilaResponse.data.remove(i);
                }
                postEvent(new AbstractNotificationsReceivedEvent.MyNotificationsReceivedEvent(listPoinilaResponse.data, listPoinilaResponse.bookmark));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<Notification>>>() {
                }.getType();
            }
        });
    }

    public static void getOthersNotification(String bookmark) {
        restServices.getOthersNotification(bookmark, new PoinilaCallback<PoinilaResponse<List<Notification>>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<List<Notification>> listPoinilaResponse, Response response) {
                postEvent(new AbstractNotificationsReceivedEvent.OthersNotificationsReceivedEvent(listPoinilaResponse.data, listPoinilaResponse.bookmark));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<Notification>>>() {
                }.getType();
            }
        });
    }


    public static void deletePost(String id) {
        restServices.deletePost(id,
                JsonRequestBodyMaker.emptyPacket().toRequestPacketJsonObject(DELETE),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {

                    }
                });
    }

    public static void getServerTime() {
        final long start = System.currentTimeMillis();
        restServices.getServerTime(new PoinilaCallback<PoinilaResponse<Date>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<Date> datePoinilaResponse, Response response) {
                //postEvent(new TimeReceivedEvent(datePoinilaResponse.data));
                Intent intent = (new Intent(ConstantsUtils.INTENT_FILTER_SERVER_TIME));
                intent.putExtra(ConstantsUtils.KEY_TIME_DIFFERENCE, datePoinilaResponse.data.getTime() - start);
                LocalBroadcastManager.getInstance(ContextHolder.getContext()).sendBroadcast(intent);
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<Date>>() {
                }.getType();
            }
        });
    }

    public static void getTopics() {
        restServices.getTopics(new PoinilaCallback<PoinilaResponse<List<Topic>>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<List<Topic>> listPoinilaResponse, Response response) {
                postEvent(new TopicsReceivedEvent(listPoinilaResponse.data));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<Topic>>>() {
                }.getType();
            }
        });
    }

    public static void getApplicationNotification() {
        restServices.getApplicationNotification(new PoinilaCallback<PoinilaResponse<List<OnOffSetting>>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<List<OnOffSetting>> listPoinilaResponse, Response response) {
                postEvent(new NotificationSettingsReceivedEvent(listPoinilaResponse.data));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<OnOffSetting>>>() {
                }.getType();
            }
        });
    }

    public static void getEmailNotification() {
        restServices.getEmailNotification(new PoinilaCallback<PoinilaResponse<List<OnOffSetting>>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<List<OnOffSetting>> listPoinilaResponse, Response response) {
                postEvent(new NotificationSettingsReceivedEvent(listPoinilaResponse.data));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<OnOffSetting>>>() {
                }.getType();
            }
        });
    }


    public static void addFriendToCircle(String circleID, String friendID) {
        restServices.addFriendToCircle(
                PoinilaPreferences.getMyId(),
                friendID, circleID, JsonRequestBodyMaker.emptyPacket().toRequestPacketJsonObject(POST),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {

                    }
                });
    }

    public static void removeFriendFromCircle(String circleID, String friendID) {
        restServices.removeFriendFromCircle(
                PoinilaPreferences.getMyId(),
                friendID, circleID, JsonRequestBodyMaker.emptyPacket().toRequestPacketJsonObject(DELETE),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse poinilaResponse, Response response) {
                    }
                });
    }


    public static void getPost(String postID, final int requestId) {
        restServices.getPost(postID, new PoinilaCallback<PoinilaResponse<Post>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<Post> poinilaResponse, Response response) {
                postEvent(new PostReceivedEvent(poinilaResponse.data, requestId));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<Post>>() {
                }.getType();
            }
        });
    }

    public static void getCollection(@NotNull String collectionIdOrName, @Nullable String userName) {
        PoinilaCallback<PoinilaResponse<Collection>> cb = new PoinilaCallback<PoinilaResponse<Collection>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<Collection> poinilaResponse, Response response) {
                postEvent(new CollectionReceivedEvent(poinilaResponse.data));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<Collection>>() {
                }.getType();
            }
        };
        if (userName != null)
            restServices.getCollectionByName(collectionIdOrName, userName, cb);
        else
            restServices.getCollection(collectionIdOrName, cb);
    }

    /**
     * @param type either bug or proposal
     */
    public static void sendReport(String type, String title, String content) {
        restServices.report(JsonRequestBodyMaker.contactUs(type, title, content).toRequestPacketJsonObject(POST),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse painingResponse, Response response) {
                        Logger.toast(R.string.successfully_submitted_report);
                    }
                });
    }

    public static void informServerOfInlineBrowsing(String postId) {
        restServices.informServerOfPostInlineBrowsing(postId, new PoinilaCallback<PoinilaResponse>() {
            @Override
            public void poinilaSuccess(PoinilaResponse ponilaResponse, Response response) {
            }
        });
    }

    public static void informServerOfExternalBrowsing(String postId) {
        restServices.informServerOfPostExternalBrowsing(postId, new PoinilaCallback<PoinilaResponse>() {
            @Override
            public void poinilaSuccess(PoinilaResponse ponilaResponse, Response response) {
            }
        });
    }

    public static void explore(String mainEntityId, String bookmark) {
        restServices.explore(mainEntityId, bookmark, new PoinilaCallback<PoinilaResponse<List<Post>>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<List<Post>> ponilaResponse, Response response) {
                postEvent(new PostsReceivedEvent(ponilaResponse.data, ponilaResponse.bookmark, BaseEvent.ReceiverName.ExploredTagPosts));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<List<Post>>>() {
                }.getType();
            }

            @Override
            public boolean poinilaError(PoinilaResponse poinilaResponse) {
                return super.poinilaError(poinilaResponse);
            }
        });
    }

    public static void verifyPhoneOrMobile(String verificationCode, int memberId, String mobileOrPhone, boolean byEmail){
        restServices.verifyPhoneOrEmail(
                JsonRequestBodyMaker.verifyPhoneOrMobile(verificationCode, memberId, mobileOrPhone, byEmail).toRequestPacketJsonObject(POST)
                , new PoinilaCallback<PoinilaResponse>() {

            @Override
            public void poinilaSuccess(PoinilaResponse ponilaResponse, Response response) {
                postEvent(new VerificationRequestResponse(true, ponilaResponse.code));
            }

            @Override
            public boolean poinilaError(PoinilaResponse poinilaResponse) {
                postEvent(new VerificationRequestResponse(false, poinilaResponse.code));
                return true;
            }
        });
    }

    public static void getSystemPreferences() {

        restServices.getSystemPreferences(new PoinilaCallback<PoinilaResponse<SystemPreferences>>() {
            @Override
            public void poinilaSuccess(PoinilaResponse<SystemPreferences> ponilaResponse, Response response) {
                postEvent(new SystemPreferencesReceivedEvent(ponilaResponse.data));
            }

            @Override
            public Type getType() {
                return new TypeToken<PoinilaResponse<SystemPreferences>>() {
                }.getType();
            }
        });

    }

    public static void reportMemberOrPost(int memberIdOrPostId){
        restServices.reportMemberOrPost(JsonRequestBodyMaker.reportMemberOrPost(
                memberIdOrPostId).
                toRequestPacketJsonObject(POST), new PoinilaCallback<PoinilaResponse>() {
            @Override
            public void poinilaSuccess(PoinilaResponse ponilaResponse, Response response) {

            }
        });
    }

    public static void getSuggestedPosts(String ids){
        try {
            JSONArray postIds = new JSONArray(ids);
            restServices.getSuggestedPosts(JsonRequestBodyMaker.postIdList(postIds).toRequestPacketJsonObject(POST), new PoinilaCallback<PoinilaResponse<List<Post>>>() {
                @Override
                public void poinilaSuccess(PoinilaResponse<List<Post>> ponilaResponse, Response response) {
                    postEvent(new SuggestionPosts(ponilaResponse.data));
                }

                @Override
                public boolean poinilaError(PoinilaResponse poinilaResponse) {
                    return super.poinilaError(poinilaResponse);
                }

                @Override
                public Type getType() {
                    return new TypeToken<PoinilaResponse<List<Post>>>() {
                    }.getType();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void setUsernamePassword(final String uniqueName, String password){
        restServices.setUserNamePassword(DBFacade.getCachedMyInfo().getId(),
                JsonRequestBodyMaker.setUsernamePassword(uniqueName, password, PoinilaPreferences.getGoogleToken()).toRequestPacketJsonObject(PUT),
                new PoinilaCallback<PoinilaResponse>() {
                    @Override
                    public void poinilaSuccess(PoinilaResponse ponilaResponse, Response response) {
                        Logger.toast(R.string.successfully_updated);
                    }

                    @Override
                    public boolean poinilaError(PoinilaResponse poinilaResponse) {

                        switch (poinilaResponse.code){
                            case 425:
                                Logger.toastError(uniqueName + " " + getString(R.string.error_already_taken_username));
                                break;
                            default:
                                Logger.toastError(getString(R.string.change_user_pass_fail));
                        }


                        return super.poinilaError(poinilaResponse);


                    }
                });
    }

    public static class AddCookiesInterceptor implements Interceptor {

        @Override
        public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            //Set<String> cookies = PoinilaPreferences.getCookies();
            for (String cookie : PoinilaNetService.cookies) { //cookies) {
                builder.addHeader("Cookie", cookie);
            }

            return chain.proceed(builder.build());
        }
    }

    public static class ReceivedCookiesInterceptor implements Interceptor {
        @Override
        public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
            com.squareup.okhttp.Response originalResponse = chain.proceed(chain.request());

            if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                HashSet<String> cookies = new HashSet<>();
                for (String header : originalResponse.headers("Set-Cookie")) {
                    if (header.startsWith("session")) {
                        header = header.split(" ")[0].replace(";", "");
                    }
                    cookies.add(header);
                }
                //PoinilaPreferences.putCookies(cookies);
                PoinilaNetService.cookies = cookies;
            }
            return originalResponse;
        }
    }



    private static class AgentAndVersionInterceptor implements Interceptor {
        @Override
        public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            builder.addHeader("ponila-version", String.valueOf(DeviceInfoUtils.CLIENT_VERSION_CODE));
            // TODO: add user-agent header.
            return chain.proceed(builder.build());
        }
    }



  /*  private static class RetrofitErrorHandler implements ErrorHandler {
        @Override
        public Throwable handleError(RetrofitError cause) {
            Exception poinilaError;
            //Response r = cause.getResponse();

            if (cause.getCause() instanceof SocketTimeoutException) {
                return new Exception(ResourceUtils.getString(R.string.error_socket_timeout));
            }
            return cause;
        }
    }*/

    public static String getStringFromResponse(Response response) {
        return new String(((TypedByteArray) response.getBody()).getBytes());
    }

    public static JsonElement getDataOfResponse(Response response) {
        JsonElement jsonElement = new JsonParser().parse(getStringFromResponse(response));
        return jsonElement.getAsJsonObject().get(KEY_JSON_DATA_ROOT);
    }

    private static <T> void postEvent(T event) {
        BusProvider.getBus().post(event);
    }


}
