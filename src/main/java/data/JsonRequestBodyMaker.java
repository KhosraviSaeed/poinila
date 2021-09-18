package data;

import android.text.TextUtils;

import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.DeviceInfoUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import data.model.Collection;
import data.model.FriendRequestAnswer;
import data.model.Member;
import data.model.Post;
import data.model.Tag;

import static com.shaya.poinila.android.util.ConstantsUtils.OS_TYPE_ANDROID;
import static com.shaya.poinila.android.util.ConstantsUtils.PHONE;
import static com.shaya.poinila.android.util.ConstantsUtils.TABLET;


/**
 * Created by iran on 2015-07-20.
 */
public class JsonRequestBodyMaker {

    public static Creator commentOnPost(String comment){
        return new Creator().put("comment", comment);//.toRequestPacketJsonObject();
    }

    public static Creator changePostColleciton(int destinationCollectionID){
        return new Creator().put("destination_collection_id", destinationCollectionID);//.toRequestPacketJsonObject();
    }

    public static Creator updateFriendCircles(List<Integer> circleIDs, int friendID){
        return new Creator().put("circle_ids", circleIDs).
                put("friend_member_id", friendID);
    }

    public static Creator answerFriendRequest(
            FriendRequestAnswer answer, int requesterID, int circleID){
        Creator creator = new Creator().
                put("requester_member_id", requesterID).
                put("answer", answer.getAnswer());//.toRequestPacketJsonObject();
        if (circleID != -1) creator.put("circle_id", circleID);
        return creator;
    }

   /* public static HashMap uploadImage(String fileAddress){
        return new Creator().put("image", fileAddress).toRequestPacketJsonObject();
    }

    public static HashMap cropPostImage(int postID, int left, int top, int right, int down){
        return new Creator().put("post_id", postID).child().
                put("left", left).put("top", top).put("right", right).put("down", down)
                .insertInParent("coordinates").toRequestPacketJsonObject();

    }*/

    /**
     * Note that to-be-friend id will be send via url parameters
     * @param circleIDs circle the user has set the friend in it.
     * @return HashMap to send by post method via retrofit
     */
    public static Creator friendRequest(List<Integer> circleIDs){
        return new Creator().put("circle_ids", circleIDs);
    }

    // TODO: you can use json ignore to send a post object instead
    /**
     * If any field is unchanged, send the Original one
     * @param postID
     * @param caption
     * @param tags
     * @return
     */
    public static Creator repost(int postID, String caption, List<Tag> tags){
        return new Creator().put("post_id", postID).put("caption", caption).put("tags", tags);
                //toRequestPacketJsonObject();
    }

    public static Creator websitePost(Post post, String siteAddress, String imageAddress, String videoAddress){
        return new Creator()
                .put("title", post.name)
                .put("caption", post.summary)
                .put("url", siteAddress)
                .put("image_url", imageAddress)
                .put("tags", post.tags)
                .put("video_url", videoAddress)
                .put("content", post.content);//.toRequestPacketJsonObject();
    }

    public static Creator createCircle(String name){
        return new Creator().put("name", name);//.toRequestPacketJsonObject();
    }

    public static Creator updateProfile(Member profile){
            /*String firstName, String lastName, String password,
                                           String oldPassword, String email, Boolean isActive){*/
        return new Creator().
                put("full_name", profile.fullName).
                put("email", profile.email).
                put("description", profile.aboutMe).
                put("mobile_number", profile.mobileNumber).
                //put("is_active", profile.isActive).
                put("url", profile.url).
                put("url_name", profile.urlName)
                .put("type", "people");
        //.toRequestPacketJsonObject();
    }

    public static Creator collectionID(int collectionID) {
        return new Creator().put("collection_id", collectionID);//.toRequestPacketJsonObject();
    }

    public static Creator loginParams(String uniqueName, String email, String password) {
        return new Creator().
                put("unique_name", uniqueName).
                put("email", email).
                put("password", password).
                put("device_os_type", OS_TYPE_ANDROID).
                put("device_type", DeviceInfoUtils.isTablet() ? TABLET : PHONE).
                put("device_model", DeviceInfoUtils.MODEL).
                put("device_unique_identifier", DeviceInfoUtils.ANDROID_ID).
                put("device_api_version", ConstantsUtils.PONILA_API_VERSION).
                put("device_os_version", DeviceInfoUtils.SDK_INT).
                put("device_brand", DeviceInfoUtils.MANUFACTURER).
                put("client_version", DeviceInfoUtils.CLIENT_VERSION_CODE).
                put("is_browser", false);
                //toRequestPacketJsonObject();
    }

    public static Creator loginByGoogleParams(String tokenId) {
        return new Creator().
                put("token_id", tokenId).
                put("device_os_type", OS_TYPE_ANDROID).
                put("device_type", DeviceInfoUtils.isTablet() ? TABLET : PHONE).
                put("device_model", DeviceInfoUtils.MODEL).
                put("device_unique_identifier", DeviceInfoUtils.ANDROID_ID).
                put("device_api_version", ConstantsUtils.PONILA_API_VERSION).
                put("device_os_version", DeviceInfoUtils.SDK_INT).
                put("device_brand", DeviceInfoUtils.MANUFACTURER).
                put("client_version", DeviceInfoUtils.CLIENT_VERSION_CODE).
                put("is_browser", false);
        //toRequestPacketJsonObject();
    }

    public static Creator setUsernamePassword(String uniqueName, String password, String gToken){
        return new Creator()
                .put("unique_name", uniqueName)
                .put("password", password)
                .put("google_token", gToken); // same auth token
    }

    public static Creator OnOffSetting(int typeID, int value) {
        return new Creator().put("type_id", typeID).put("value", value);
    }

    public static Creator emptyPacket() {
        return new Creator();
    }

    public static Creator userInterests(List<Integer> selectedInterests) {
        return new Creator().putArrayAsData(selectedInterests);
    }

    public static Creator createTextPost(Post post) {
        return new Creator().putObjectAsData(post);
    }

    public static Creator createCollection(Collection collection) {
        return new Creator().putObjectAsData(collection);
    }

    public static Creator inviteToPoinila(String email, String message) {
        return new Creator().put("email", email).put("message", message).put("suppress_warning", true);
    }

    public static Creator contactUs(String type, String title, String content) {
        return new Creator().put("type", type).put("title", title).put("content", content);
    }

    public static Creator email(String email) {
        return new Creator().put("email", email);
    }

    public static Creator requestVerify(boolean byEmail, String phoneOrMobile, int memberId) {
        return new Creator()
                .put(byEmail ? "email" : "mobile_number", phoneOrMobile)
                .put("member_id", memberId == 0 ? null : memberId);
    }

    public static Creator phoneNumber(String phone) {
        return new Creator().put("mobile_number", phone);
    }

    public static Creator uniqueName(String phone) {
        return new Creator().put("unique_name", phone);
    }

    public static Creator changePassword(String password, String oldPassword){
        return new Creator().
                put("is_browser", false).
                put("device_unique_identifier", DeviceInfoUtils.ANDROID_ID).
                put("client_version", DeviceInfoUtils.CLIENT_VERSION_CODE).
                put("password", password).
                put("old_password", oldPassword);
    }

    public static Creator resetPassword(String newPassword, String code) {
        return new Creator().
                put("password", newPassword).
                put("reset_password_hash", code).
                put("is_browser", false).
                put("device_unique_identifier", DeviceInfoUtils.ANDROID_ID).
                put("client_version", DeviceInfoUtils.CLIENT_VERSION_CODE);
    }

    public static Creator reportMemberOrPost(int memberIdOrPostId) {
        return new Creator().
                put("memberIdOrPostId", memberIdOrPostId);
    }

    public static Creator postIdList(JSONArray ids) {

        List<Integer> list = new ArrayList<>();

        int length  = ids.length();
        for(int i=0 ; i < length ; i ++){
            try {
                list.add(ids.getInt(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return new Creator().
                put("post_id_list", list);
    }

    public static Creator register(String verificationCode, String fullName, String userName, String gender, String password, String email, String phone) {
//        return loginParams(userName, email, password).put("verification_code", verificationCode).put("full_name", fullName)
//                .put("phone", phone)
//                .put("gender", gender);

        return loginParams(userName, email, password).put("full_name", fullName)
                .put("mobile_number", phone)
                .put("gender", gender);
    }

    public static Creator verifyPhoneOrMobile(String verificationCode, int memberId, String mobileOrPhone, boolean byEmail){

        if(TextUtils.isEmpty(mobileOrPhone)){
            return new Creator()
                    .put("verification_code", verificationCode)
                    .put("member_id", memberId);
        }else {
            return new Creator()
                    .put(byEmail ? "email" : "mobile_number" , mobileOrPhone)
                    .put("verification_code", verificationCode)
                    .put("member_id", memberId);
        }
    }

    public static class Creator{
        private static final String METHOD = "action";
        private static final String BODY = "data";
        /*JSONObject jsonHashMap;
                JSONArray jsonArray;*/
        HashMap<String, Object> jsonHashMap;
        List jsonArray;
        Creator parent;
        private Object jsonObject;

        public Creator(){
            jsonHashMap = new HashMap<>();
        }

        public Creator put(String key, Object value){
            if (value != null)
                jsonHashMap.put(key, value);
            return this;
        }

        public Creator putArrayAsData(List list){
            if (list != null) {
                jsonArray = list;
            }
            return this;
        }

        public Creator putObjectAsData(Object object){
            if (object != null){
                jsonObject = object;
            }
            return this;
        }

        public HashMap toRequestPacketJsonObject(String method){
            HashMap<String, Object> packet = new HashMap<>();
            packet.put(METHOD, method);
            if (jsonObject != null) packet.put(BODY, jsonObject);
            else if (jsonArray != null) packet.put(BODY, jsonArray);
            else packet.put(BODY, jsonHashMap);
            return packet;
        }

/*        public Creator child() {
            Creator c = new Creator();
            c.parent = this;
            return c;
        }

        public Creator insertInParent(String key){
            this.parent.put(key, toRequestPacketJsonObject());
            return this.parent;
        }*/
    }
}
