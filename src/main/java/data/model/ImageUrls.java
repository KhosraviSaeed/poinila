package data.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by iran on 2015-07-20.
 */
@Parcel
public class ImageUrls {
    public long id;

    @SerializedName(value = "is_uploaded") public boolean uploaded;
    @SerializedName(value = "1000x1000") public data.model.Image x1000square;
    @SerializedName(value = "736x") public data.model.Image x736; // for post
    public data.model.Image origin;
    @SerializedName(value = "160x160") public data.model.Image x160square;
    @SerializedName(value = "100x100") public data.model.Image x100square;
    @SerializedName(value = "200x100") public data.model.Image interest;
    @SerializedName(value = "236x") public data.model.Image x236;
    @SerializedName(value = "60x60") public data.model.Image x60square;
    @SerializedName(value = "75x75") public data.model.Image x75square;
    @SerializedName(value = "40x40") public data.model.Image x40square;
    @SerializedName("dominant_color")
    public String dominantColor;


    public data.model.Image properMemberImage(ImageSize imageSize) {
        switch (imageSize){
            case AVATAR:
                return x75square;
            case BIG:
                return x160square;
            case FULL_SIZE:
                return x1000square;
        }
        return x75square;
    }

    public data.model.Image properCollectionImage(ImageSize imageSize) {
        switch (imageSize){
            case AVATAR:
                return x100square;
            case BIG:
                return x160square;
        }
        return x75square;
    }

    public data.model.Image properPostImage(ImageSize imageSize) {
        switch (imageSize){
            case AVATAR:
                return x100square;
            case BIG:
                return x736;
            case MEDIUM:
                return x236;
            case FULL_SIZE:
                return origin;
        }
        return x75square;
    }


    public boolean isNotEmpty() {
        data.model.Image[] images = new data.model.Image[]{x236, x75square, x160square, x100square, x60square, x40square, x736, interest};
        for (data.model.Image image : images){
            if (image != null)
                return true;
        }
        return false;
    }

    public static boolean hasValidUrl(ImageUrls imageUrls, ImageType imageType, ImageSize imageSize) {
        if (imageUrls == null) return false;
        data.model.Image image;
        switch (imageType){
            case COLLECTION:
                image = imageUrls.properCollectionImage(imageSize);
                break;
            case POST:
                image = imageUrls.properPostImage(imageSize);
                break;
            case MEMBER:
                image = imageUrls.properMemberImage(imageSize);
                break;
            case INTEREST:
                image = imageUrls.interest;
                break;
            default:
                image = null;
                break;
        }
        return !(image == null || TextUtils.isEmpty(image.url));
    }

    public enum ImageType {
        @SerializedName("collection")
        COLLECTION,
        @SerializedName("member")
        MEMBER,
        @SerializedName("post")
        POST,
        INTEREST
    }
    public enum ImageSize {
        AVATAR,
        /**
         * only used for posts in dashboards
         */
        MEDIUM,
        BIG,
        FULL_SIZE,
    }
}
