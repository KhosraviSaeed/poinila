package data.model;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.shaya.poinila.android.util.JavaUtils;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by iran on 2015-06-23.
 */
@Parcel(analyze=Member.class)
@Table(database = data.database.PoinilaDataBase.class)
public class Member extends data.database.PoinilaDataBase.PoinilaDBModel<Member> implements data.model.Identifiable {
    @Column
    @PrimaryKey
    public int id;
    /*@SerializedName(value = "first_name") public String firstName;
    @SerializedName(value = "last_name") public String lastName;*/
    public Gender gender;
    @SerializedName("mobile_number")
    public String mobileNumber;
    public String email;
    @SerializedName(value = "images") public data.model.ImageUrls imageUrls;
    @SerializedName(value = "full_name") public String fullName;
    @SerializedName(value = "unique_name") public String uniqueName;
    @SerializedName(value = "description") public String aboutMe;
    @SerializedName(value = "type") public data.model.MemberType memberType;

    //TODO: String khali bashe ya entity
    public List<Tag> interests;
    @SerializedName(value = "circles") public List<Circle> circles; // for offline use.
    @SerializedName(value = "circle_ids") public List<Integer> circle_ids; // what comes from server
    @SerializedName(value = "frames") public List<data.model.Frame> frames;

    // url website taraf
    public String url;
    // esme website
    @SerializedName(value = "url_name") public String urlName;
    //@SerializedName(value = "is_verified") public boolean verified;

    @SerializedName(value = "friend_count") public int friendsCount;
    @SerializedName(value = "like_count") public int likesCount;
    @SerializedName(value = "follower_count") public int followerCount;
    @SerializedName(value = "post_count") public int postsCount;

    @SerializedName(value = "own_collections") public List<Collection> owningCollections;
    @SerializedName(value = "follow_collections") public List<Collection> followingCollections;
    @SerializedName(value = "own_collection_count") public int owningCollectionsCount;
    @SerializedName(value = "following_collection_count") public int followingCollectionsCount;

    //@SerializedName(value = "is_friend") public boolean isFriend;
    @SerializedName(value = "friendship_status") public data.model.FriendshipStatus friendshipStatus;
    //   public String siteFaviconUrl;

    // previously was false, but I think its wiser to assume user is anonymous firstly
    @SerializedName("is_anonymous") public boolean isAnonymous = true;

    public transient boolean selected = false;

    @SerializedName(value = "email_verified") public boolean isEmailVerified = false;
    @SerializedName(value = "mobile_verified") public boolean isMobileVerified = false;
    @SerializedName(value = "set_password") public boolean isPassword = true;




    public Member(){}
    /**
     * Works as cloning class instances. used in changing profile setting
     * @param originalProfile
     */
    public Member(Member originalProfile) {
        fullName = originalProfile.fullName;
        email = originalProfile.email;
        mobileNumber = originalProfile.mobileNumber;
        aboutMe = originalProfile.aboutMe;
        //mobileNumber = originalProfile.mobileNumber;
        urlName = originalProfile.urlName;
        url = originalProfile.url;
        //isActive = originalProfile.isActive;
    }


    @Override
    public Member getModel() {
        return getModelFromJson(Member.class);
    }

    @Override
    public String getId() {
        return String.valueOf(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;

        // based on what we need in profile setting
        return JavaUtils.equal(this.fullName, member.fullName) &&
                JavaUtils.equal(this.email, member.email) &&
                JavaUtils.equal(this.mobileNumber, member.mobileNumber) &&
                JavaUtils.equal(this.aboutMe, member.aboutMe) &&
                JavaUtils.equal(this.urlName, member.urlName) &&
                JavaUtils.equal(this.url, member.url);
    }

}
