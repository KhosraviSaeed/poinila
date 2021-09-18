package data.model;


import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.parceler.Parcel;
import org.parceler.ParcelConverter;
import org.parceler.Parcels;
import org.parceler.TypeRangeParcelConverter;

import java.util.Date;
import java.util.List;

/**
 * @author Alireza Farahani
 * Created by iran on 2015-06-10.
 */
@Parcel(analyze=Collection.class)
@Table(database = data.database.PoinilaDataBase.class)
public class Collection extends data.database.PoinilaDataBase.PoinilaDBModel<Collection> implements data.model.Identifiable {

    // Invisible Fields
    @Column
    @PrimaryKey
    public int id;

    // Visible Fields
    @Column
    public String name;
    public data.model.Member owner;
    public data.model.PrivacyType privacy;
    public String description;
    // har collection hatman ye topic dare ke moghe
    // sakht azash miporsim va editable'e
    public Tag topic;

    @SerializedName(value = "last_post_creation_time") public Date lastPostCreationTime;
    @SerializedName(value = "like_count") public int totalLikeCount;
    @SerializedName(value = "comment_count") public int totalCommentCount;
    @SerializedName(value = "repost_count") public int totalRepostCount;
    @SerializedName(value = "follow_count") public int followerCount;
    @SerializedName(value = "post_count") public int postCount;
    @SerializedName(value = "followed_by_me") public boolean followedByMe;
    @SerializedName(value = "images") public data.model.ImageUrls coverImageUrls;
    @SerializedName(value = "first_post_images") public data.model.ImageUrls image1Url;
    @SerializedName(value = "second_post_images") public data.model.ImageUrls image2Url;
    @SerializedName(value = "third_post_images") public data.model.ImageUrls image3Url;
    @SerializedName(value = "frame_ids") public List<Integer> frameIDs;
    @SerializedName(value = "unseen_posts_count") public int unseenPostsCount;
    // TODO: az koja miad. felan tu api nist
    @SerializedName(value = "circle_ids") public List<Integer> circleIDs;

    // used in frame management.
    public transient boolean selected = false;

    @Override
    public Collection getModel() {
        return getModelFromJson(Collection.class);
    }

    @Override
    public String getId() {
        return String.valueOf(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Collection that = (Collection) o;
        return id == that.id;
    }

    public String getOriginalCoverUrl() {
        if (coverImageUrls == null) return null;
        Image image = coverImageUrls.properCollectionImage(data.model.ImageUrls.ImageSize.BIG);
        return (image != null) ? image.url : null;
    }

}
